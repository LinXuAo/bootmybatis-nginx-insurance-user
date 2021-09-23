package com.controller;

import com.alibaba.fastjson.JSONArray;
import com.po.Dto;
import com.po.InsuranceUser;
import com.service.InsuranceUserService;

import com.util.DtoUtil;
import com.util.ErrorCode;
import com.util.TokenUtil;
import com.util.vo.InsuranceUserSaveVo;
import org.springframework.beans.factory.annotation.Autowired;

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

}
