package com.controller;


import com.alibaba.fastjson.JSON;
import com.po.Dto;
import com.po.InsuranceUser;
import com.service.InsuranceUserService;
import com.util.*;
import com.util.vo.InsuranceUserSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
@RequestMapping("/api")//全局访问项
public class InsuranceUserController {
    private  Jedis jedis = new Jedis("127.0.0.1", 6379);
    @Autowired
    private InsuranceUserService insuranceUserService;


    /**
     * 用来做登录的方法
     **/
    @RequestMapping(value = "/login")
    public Dto login(HttpServletRequest request,HttpServletResponse response,@RequestBody InsuranceUserSaveVo insuranceUserSaveVo) {
        System.out.println("用来做登录的方法........" + insuranceUserSaveVo.toString());
        String upasswd = MD5Util.getMd5(insuranceUserSaveVo.getUserPassword(), 32);
        System.out.println("upasswd:"+upasswd);
        InsuranceUser user = insuranceUserService.findByUserCode(insuranceUserSaveVo.getUserCode());
        System.out.println("user:"+user);
        if (user!=null){
            System.out.println("该用户已注册");
            if (jedis.get(user.getUserCode())!=null){
                 String oldToken=jedis.get(user.getUserCode());
                //System.out.println("222222222");
                try {
                    /**判断相关参数完成不同浏览器登录操作*/
                    String toke=TokenUtil.replaceToken(request.getHeader("user-agent"),oldToken,response);
                        return DtoUtil.returnSuccess("登录成功！");
                } catch (TokenValidationFailedException e) {
                    e.printStackTrace();
                    return DtoUtil.returnFail(e.getMessage(),ErrorCode.AUTH_REPLACEMENT_FAILED);
                }
            }else {
               if(user.getActivated()==1){//已经激活的情况
                   if (user.getUserPassword().equals(upasswd)){
                       //System.out.println("11111111111");
                    /**获取浏览器请求头信息,user-agent固定写法*/
                    String requestHeader=request.getHeader("user-agent");
                    System.out.println("requestHeader:"+requestHeader);
                    /**生成Token*/
                    String token= TokenUtil.getTokenGenerator(requestHeader,user);
                    /**将Token存入Redis中*/
                    jedis.setex(user.getUserCode(),7200,token);
                    /**将Token作为key值，将该用户对象作为value值存入Redis中，目标是后边用来判断该用户是否已经登录*/
                    String userJson= JSON.toJSONString(user);
                    jedis.setex(token,7200,userJson);
                    /**将Token存入前端浏览器(Cookie)中*/
                    Cookie usToken=new Cookie("token",token);
                    /**该方法用于设置cookie的生存时间，传入的参数表示生存时间，是int型的秒数值*/
                    usToken.setMaxAge(60*60*2);
                    /**存入浏览器Cookie中*/
                    response.addCookie(usToken);
                       System.out.println("登录成功");
                    return DtoUtil.returnSuccess("登录成功！");
                }else {
                    return DtoUtil.returnFail("登录失败！密码错误,请重新输入密码",ErrorCode.AUTH_PARAMETER_ERROR_PASSWD);
                }

            }else {//未激活的情况
                RidesUtil.jedisSend(user);
                return DtoUtil.returnFail("登录失败！该用户未激活，即将去激活",ErrorCode.AUTH_PARAMETER_ERROR);
              }
            }
        }else{//用户未注册
            return DtoUtil.returnFail("登录失败！账号输入错误，请重新输入",ErrorCode.AUTH_ACTIVATE_FAILED);
        }
    }
    /**
     * 用来判断是否重名的方法
     **/
    @RequestMapping(value = "/find")
    public Dto findByUserCode(@RequestBody InsuranceUserSaveVo insuranceUserSaveVo) {
        System.out.println("判断用户名是否存在的方法........"+insuranceUserSaveVo.toString());
            InsuranceUser user = insuranceUserService.findByUserCode(insuranceUserSaveVo.getUserCode());
            System.out.println(user);
            if (user!= null) {
                System.out.println("注册失败该用户已经存在");
                if (user.getActivated() == 1) {//用户存在已经激活的情况
                    return DtoUtil.returnFail("注册失败,该用户已经存在,且已激活，即将去激活", ErrorCode.AUTH_AUTHENTICATION_FAILED);
                } else {//用户存在未激活情况(需要激活，重新发送验证码)
                    InsuranceUser olduser = new InsuranceUser();
                    olduser.setUserCode(insuranceUserSaveVo.getUserCode());
                    RidesUtil.jedisSend(olduser);
                    return DtoUtil.returnFail("注册失败,该用户已经存在,未激活,即将去激活", ErrorCode.AUTH_AUTHENTICATION_FAILED_NOT_ACTIVATED);
                }
            } else {
                return DtoUtil.returnSuccess("该账户可以正常使用");
            }
    }

    /**
     * 用来做激活的方法
     **/
    @RequestMapping(value = "/activedByUserCode/{activedCode}")
    public Dto actived(@PathVariable String activedCode, @RequestBody InsuranceUserSaveVo insuranceUserSaveVo) {
        System.out.println("用来做账号激活的方法........" + insuranceUserSaveVo.toString() + "-----" + activedCode);
        //首先判断该用户是否存在 ，是否激活
            System.out.println(insuranceUserSaveVo.getUserCode());
            InsuranceUser user = insuranceUserService.findByUserCode(insuranceUserSaveVo.getUserCode());
            System.out.println("user:"+user);
            if (user != null) {//user对象存在，可以激活
                if (user.getActivated() == 0) {//未激活,开始激活操作
                    if (activedCode.equals(jedis.get(user.getUserCode()))) {
                        Integer code = insuranceUserService.updateActived(user.getUserCode());
                        jedis.del(user.getUserCode());
                        return DtoUtil.returnSuccess("激活成功！");
                    } else {
                        if(jedis.get(user.getUserCode())!=null){
                            return DtoUtil.returnFail("激活码错误，激活失败，请重新输入激活码", ErrorCode.AUTH_ACTIVATE_FAILED_ERROR);
                        }else
                            RidesUtil.jedisSend(user);
                            return DtoUtil.returnFail("激活码过期，激活失败，请重新输入激活码", ErrorCode.AUTH_ACTIVATE_FAILED_PAST);


                    }
                } else {//已经激活
                    return DtoUtil.returnFail("激活失败,该用户已经激活", ErrorCode.AUTH_ACTIVATE_FAILED_TRUE);
                }
            } else {//用户不存在，不可激活,请先注册
                return DtoUtil.returnFail("激活失败,用户不存在，请先注册", ErrorCode.AUTH_ACTIVATE_FAILED_NULL);
            }
                //return DtoUtil.returnFail("用户注册异常:" , ErrorCode.AUTH_UNKNOWN);
    }
    /**
     * 用来做注册的方法
     * **/
    @RequestMapping("/save")
    public Dto save(HttpServletRequest request, HttpServletResponse response, @RequestBody InsuranceUserSaveVo insuranceUserSaveVo){
        System.out.println("自注册的方法......"+insuranceUserSaveVo.toString());
        try {
            if (insuranceUserSaveVo != null && insuranceUserSaveVo.getUserCode() != null) {
//                InsuranceUser user = insuranceUserService.findByUserCode(insuranceUserSaveVo.getUserCode());
//                if (user != null) {
//                    System.out.println("注册失败该用户已经存在");
//                    return DtoUtil.returnFail("注册失败该用户已经存在", ErrorCode.AUTH_AUTHENTICATION_FAILED);
//                } else {
                    //处理传递过来的数据(封装对象)
                    InsuranceUser olduser = new InsuranceUser();
                    olduser.setUserCode(insuranceUserSaveVo.getUserCode());
                    olduser.setUserName(insuranceUserSaveVo.getUserName());
                    olduser.setWeChat(insuranceUserSaveVo.getWeChat());
                    olduser.setIdnumber(insuranceUserSaveVo.getIdnumber());
                    //自处理的数据（前台没有传的数据）
                    olduser.setUserType(1);
                    olduser.setCreationDate(new Date());
                    olduser.setCreatedBy(insuranceUserSaveVo.getUserName());
                    olduser.setModifyDate(new Date());
                    olduser.setModifiedBy(insuranceUserSaveVo.getUserName());
                    olduser.setActivated(0);
                    //处理密码的加密操作(MD5)
                    String upasswd = MD5Util.getMd5(insuranceUserSaveVo.getUserPassword(), 32);
                    olduser.setUserPassword(upasswd);
                    Integer code = insuranceUserService.save(olduser);
                    if (code > 0) {
                        System.out.println("自注册成功....");
                        //该用户第一次注册，需要发送验证码激活
                        RidesUtil.jedisSend(olduser);
                        return DtoUtil.returnSuccess("注册成功!");
                    } else {
                        System.out.println("自注册失败....");
                        return DtoUtil.returnFail("注册失败!后台数据错误", ErrorCode.AUTH_USER_ALREADY_EXISTS);
                    }
                //}

            }else{
                    return DtoUtil.returnFail("注册失败前台传入数据有误", ErrorCode.AUTH_AUTHENTICATION_UPDATE);
                }
            } catch(Exception e){
                e.printStackTrace();
                return DtoUtil.returnFail("用户注册异常:" + e.getMessage(), ErrorCode.AUTH_UNKNOWN);
            }
    }
}
