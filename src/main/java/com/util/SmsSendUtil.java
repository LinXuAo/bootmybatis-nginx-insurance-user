package com.util;



import com.cloopen.rest.sdk.CCPRestSmsSDK;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

public class SmsSendUtil {
    public static String smssend(String userCode) {
        System.out.println("做信息发送的方法。。。。。。");
        HashMap<String, Object> result = null;
        CCPRestSmsSDK restAPI = new CCPRestSmsSDK();
        restAPI.init("app.cloopen.com", "8883");
        // 初始化服务器地址和端口，生产环境配置成app.cloopen.com，端口是8883.
        restAPI.setAccount("8aaf07087bc82708017bec984e5c09dc", "7cb5c9ccc31541ff8d75c78da53f173a");
        // 初始化主账号名称和主账号令牌，登陆云通讯网站后，可在控制首页中看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN。
        restAPI.setAppId("8aaf07087bc82708017bec984f4009e2");
        // 请使用管理控制台中已创建应用的APPID。
        //做验证码处理
           //获取系统当前时间
        Date date=new Date();
           //获取当前时间的毫秒数
        Long datetime=date.getTime();
        String starttime=datetime+"";
        String smsdk=starttime.substring(starttime.length()-6,starttime.length());
        result = restAPI.sendTemplateSMS("17302963615","1" ,new String[]{smsdk,"1"});
        System.out.println("SDKTestGetSubAccounts result=" + result);
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            System.out.println("短信发送成功");
            Set<String> keySet = data.keySet();
            for(String key:keySet){
                Object object = data.get(key);
                System.out.println(key +" = "+object);
            }
            return smsdk;
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
            return null;
        }

    }
}
