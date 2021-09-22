package com.util;

import com.po.InsuranceUser;
import redis.clients.jedis.Jedis;


public class RidesUtil {
    private static Jedis jedis=new Jedis("127.0.0.1",6379);
    public static String jedisSend(InsuranceUser user){
         //判断是手机号还是邮箱
        if (user.getUserCode().indexOf("@")!=-1){//是邮箱
            System.out.println("是邮箱，发送邮件激活");
            String emailcheck=EmilSendUtil.emilSend(user);
            //redis中缓存激活码
            jedis.setex(user.getUserCode(),120,emailcheck);
            return emailcheck;
        }else {//是手机号
            System.out.println("是手机号，发送短信件激活");
            String smscheck=SmsSendUtil.smssend(user.getUserCode());
            //redis中缓存激活码
            jedis.setex(user.getUserCode(),120,smscheck);
            return smscheck;
        }
    }
}
