package com.util;

/**
 * 系统错误编码，根据业务定义如下
 * 用户部分编码以10000开始
 *
 *
 */
public class ErrorCode {

	/*认证模块错误码-start   全大写表示常量*/
	public final static String AUTH_UNKNOWN="10000";//异常反馈
	public final static String AUTH_USER_ALREADY_EXISTS="10001";//注册失败，后台数据异常
	public final static String AUTH_AUTHENTICATION_FAILED="10002";//注册失败，用户已存在（用户存在已激活）
	public final static String AUTH_AUTHENTICATION_FAILED_NOT_ACTIVATED="10003";//注册失败，用户已存在（用户存在未激活）
    public final static String AUTH_AUTHENTICATION_UPDATE="10004";//注册失败，前台传入数据有误
	public final static String AUTH_PARAMETER_ERROR="10005";//登录失败，用户未激活
	public final static String AUTH_PARAMETER_ERROR_PASSWD="100051";//登录失败，密码错误
	public final static String AUTH_ACTIVATE_FAILED="10006";//登录失败,账号输入错误
	public final static String AUTH_ACTIVATE_FAILED_ERROR="100061";//激活失败,激活码错误
	public final static String AUTH_ACTIVATE_FAILED_TRUE="100062";//激活失败,该用户已经激活
	public final static String AUTH_ACTIVATE_FAILED_NULL="100063";//激活失败,该用户不存在
	public final static String AUTH_ACTIVATE_FAILED_PAST="100064";//激活失败,激活码过期
	public final static String AUTH_REPLACEMENT_FAILED="10007";//置换token失败,Token不匹配，异地登录
	public final static String AUTH_TOKEN_INVALID="10008";//token无效
	public static final String AUTH_ILLEGAL_USERCODE = "10009";//非法的用户名
	/*认证模块错误码-end*/
}
