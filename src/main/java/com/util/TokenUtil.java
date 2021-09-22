package com.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.po.InsuranceUser;
import cz.mallat.uasparser.UserAgentInfo;
import redis.clients.jedis.Jedis;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TokenUtil {
    private static String tokenPrefix = "token:";//统一加入 token前缀标识
    private static Jedis jedis = new Jedis("127.0.0.1", 6379);
    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }
    /***
     * @param agent Http头中的user-agent信息
     * @param us 用户信息
     * @return Token格式<br/>
     * 	PC：前缀PC-USERCODE-USERID-CREATIONDATE-RONDEM[6位]
     *  <br/>
     *  Android：“前缀ANDROID-USERCODE-USERID-CREATIONDATE-RONDEM[6位]”
     *  生成token
     */
    public static String getTokenGenerator(String agent, InsuranceUser us) {
        try {
            /**
             * 解析浏览器的user agent字符串，返回UserAgentInfo对象。
             * 将请求头的信息转换成UserAgentInfo的对象格式
             * 按照请求头的格式信息将请求头分割出来
             */
             UserAgentInfo userAgentInfo = UserAgentUtil.getUasParser().parse(agent);
            System.out.println(userAgentInfo.toString());
            StringBuilder sb = new StringBuilder();
            sb.append(tokenPrefix);//统一前缀
            /**
             * 函数：getDeviceType 获取设备类型
             * */
            System.out.println("userAgentInfo.getDeviceType():"+userAgentInfo.getDeviceType());
            System.out.println("UserAgentInfo.UNKNOWN:"+UserAgentInfo.UNKNOWN);
            if (userAgentInfo.getDeviceType().equals(UserAgentInfo.UNKNOWN)) {
                System.out.println("44444444");
                if (UserAgentUtil.CheckAgent(agent)) {
                    sb.append("MOBILE-");
                } else {
                    sb.append("PC-");
                }
            } else if (userAgentInfo.getDeviceType()
                    .equals("Personal computer")||userAgentInfo.getDeviceType()
                    .equals("Smartphone")) {

                sb.append("PC-");
            } else {
                sb.append("MOBILE-");
            }
                sb.append(MD5Util.getMd5(us.getUserCode(), 32) + "-");//加密用户名称
                sb.append(us.getId() + "-");
                sb.append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
                        + "-");
                sb.append(MD5Util.getMd5(agent, 6));// 识别客户端的简化实现——6位MD5码
            System.out.println("自己生成的Token："+sb);
            return sb.toString();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

       return  null;
    }
   /*删除token*/
    public static void delete(HttpServletRequest request, HttpServletResponse response, String token, String usersCoe) {
        if (jedis.get(token)!=null) {
            Cookie usernameCookie = new Cookie("token",null);//cookie名字要相同
            usernameCookie.setMaxAge(0); //删除cookie的方法
            /**
             * request.getContextPath()其作用是获取当前的系统路径。
             * */
            usernameCookie.setPath(request.getContextPath());  // 相同路径
            response.addCookie(usernameCookie);
            jedis.del(token);
            jedis.del(usersCoe);

        }
    }
    /*置换token*/
    public static String replaceToken(String agent, String token,HttpServletResponse response )
            throws TokenValidationFailedException {

        // 验证旧token是否有效
        if (jedis.get(token)==null) {// token不存在
            throw new TokenValidationFailedException("未知的token或 token已过期");// 终止置换
        }
        Date TokenGenTime;// token生成时间
        try {
            String[] tokenDetails = token.split("-");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            TokenGenTime = formatter.parse(tokenDetails[3]);
        } catch (ParseException e) {
            throw new TokenValidationFailedException("token格式错误:" + token);
        }
        /**用系统当前毫秒数-Redis中token的毫秒数，判断是否过期
         * TokenGenTime.getTime()，Redis中Token的生成时间
         * */
        long passed = Calendar.getInstance().getTimeInMillis()
                - TokenGenTime.getTime();// token已产生时间
        if (passed <  60000) {// 置换保护期内
            throw new TokenValidationFailedException("token处于置换保护期内，剩余"
                    + (60000 - passed) / 1000
                    + "(s),禁止置换");
        }
        // 置换token(二次登录成功)
        String newToken = "";
        System.out.println(jedis.get(token).toString());
        //将json字符串转java对象
       InsuranceUser user = JSONArray.parseObject(jedis.get(token).toString(),InsuranceUser.class);
       //InsuranceUser user=listuser.get(0);
//        long ttl = jedis.ttl(token);// token有效期（剩余秒数 ）
        // ttl == -1,key 存在，但没有设置剩余生存时间
//        if (ttl > 0 || ttl == -1) {// 判断token在有效期(防止Token过期了)
            //删除旧的token的用户
            jedis.del(token);
            newToken = getTokenGenerator(agent, user);
            //this.save(newToken, user);// 缓存新token
            //将token存入redis(因为将来取值时需要返回对象所以转成json存进去)
            String strJson = JSON.toJSONString(user);
            jedis.setex(newToken,7200,strJson);
            //将token存入redis
            jedis.setex(user.getUserCode(), 7200, newToken);
            //后台存储到浏览器cookie
            Cookie userrnameCookie = new Cookie("token", newToken);
            userrnameCookie.setMaxAge(60 * 60 * 2);
            response.addCookie(userrnameCookie);
//        } else {// 其它未考虑情况，不予置换
//            throw new TokenValidationFailedException("当前token的过期时间异常,禁止置换");
//        }
        return newToken;
    }
}
