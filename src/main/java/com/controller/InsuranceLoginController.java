package com.controller;

import com.alibaba.fastjson.JSONArray;
import com.po.Dto;
import com.po.InsuranceUser;
import com.service.InsuranceUserService;

import com.util.*;
import com.util.vo.InsuranceUserSaveVo;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")//全局访问项
public class InsuranceLoginController {
    private Jedis jedis = new Jedis("127.0.0.1", 6379);
    @Autowired
    private InsuranceUserService insuranceUserService;
    /**
     * 用来验证Token的方法
     **/
    @RequestMapping(value = "/checkToken")
    public Dto checkToken(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("用来验证Token的方法........" + request.getHeader("Cookie"));
        /**获取浏览器中的Cookie*/
        String cookieToken=request.getHeader("Cookie").substring(6);
        System.out.println("Token:"+cookieToken);
        /**根据拿到的Token获取Redis中的user对象字符串*/
        String userString=jedis.get(cookieToken);
        if (userString!=null){
        //根据Token从Redis中取出对象（json字符格式），并转换成java对象
        InsuranceUser user = JSONArray.parseObject(userString,InsuranceUser.class);
           System.out.println(user.toString());
            return DtoUtil.returnSuccess("用户信息验证成功",user);
        }else {
            return DtoUtil.returnFail("该用户异地登录，请注意账号安全", ErrorCode.AUTH_REPLACEMENT_FAILED);
        }
        }
    /**
     * 退出登录的方法
     **/
    @RequestMapping(value = "/loginOut")
    public Dto loginOut(HttpServletRequest request, HttpServletResponse response,@RequestBody InsuranceUserSaveVo insuranceUserSaveVo) {
        System.out.println("退出登录的方法........"+insuranceUserSaveVo.getUserCode());
        if (insuranceUserSaveVo.getUserCode()!=null){
            //判断Redis中有没有该用户
            String token=jedis.get(insuranceUserSaveVo.getUserCode());
            if (token!=null){//退出删除
                TokenUtil.delete(request,response,token,insuranceUserSaveVo.getUserCode());
                  return DtoUtil.returnSuccess("退出登录，跳转登录页面");
            }else {//token不存在的情况，
                return DtoUtil.returnSuccess("用户登录过期，退出登录，跳转登录页面");
            }

        }
        return DtoUtil.returnFail("账户异常，请重新登录",ErrorCode.AUTH_REPLACEMENT_FAILED);
    }
    /**
     * 修改密码时获取验证码的方法
     * */
    @RequestMapping(value = "/getCode")
    public Dto getCode(@RequestBody InsuranceUserSaveVo insuranceUserSaveVo) {
        System.out.println("修改密码时获取验证码的方法........" + insuranceUserSaveVo.getUserCode());
        InsuranceUser user = insuranceUserService.findByUserCode(insuranceUserSaveVo.getUserCode());
        if (user!=null ){
             RidesUtil.jedisSend(user);
             return DtoUtil.returnSuccess("验证码发送成功");
          }
        return DtoUtil.returnFail("账户输入错误",ErrorCode.AUTH_ILLEGAL_USERCODE);
    }
    /**
     * 确定验证码是否一致的方法
     * */
    @RequestMapping(value = "/confirmCode/{code}")
    public Dto confirmCode(@PathVariable String code,@RequestBody InsuranceUserSaveVo insuranceUserSaveVo) {
        System.out.println("确定验证码是否一致的方法........" + insuranceUserSaveVo.getUserCode()+code);
        if (insuranceUserSaveVo.getUserCode()!=null){
            if(code!=null && jedis.get(insuranceUserSaveVo.getUserCode())!=null){
                if (code.equals(jedis.get(insuranceUserSaveVo.getUserCode()))){
                    return DtoUtil.returnSuccess("验证成功！");
                }else{
                    return DtoUtil.returnFail("验证码输入错误，请重新输入",ErrorCode.AUTH_CODE_FAILED);
                }
           }else {
                return DtoUtil.returnFail("验证码过期，请重新获取验证码",ErrorCode.AUTH_CODE_FAILED_NOT);
            }
        }
        return DtoUtil.returnFail("数据异常，请重新输入",ErrorCode.AUTH_UNKNOWN);
    }
    /**
     * 修改密码的方法
     * */
    @RequestMapping(value = "/updateCode")
    public Dto updateCode(@RequestBody InsuranceUserSaveVo insuranceUserSaveVo) {
        System.out.println("修改密码的方法........" + insuranceUserSaveVo.toString());
        if (insuranceUserSaveVo!=null) {
            //处理密码的加密操作(MD5)
           String userPassword = MD5Util.getMd5(insuranceUserSaveVo.getUserPassword(), 32);
            System.out.println("userPassword:"+userPassword);
            String userCode = insuranceUserSaveVo.getUserCode();
            Integer code = insuranceUserService.updateCode(userPassword, userCode);
            if (code > 0) {
                return DtoUtil.returnSuccess("密码修改成功！");
            }
            return DtoUtil.returnFail("修改失败", ErrorCode.AUTH_UPDATECODE_FAILED);
        }
        return DtoUtil.returnFail("网络异常，请重新输入",ErrorCode.AUTH_UNKNOWN);
    }
}
