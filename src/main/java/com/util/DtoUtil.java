package com.util;


import com.po.Dto;

/**
 * 用于返回Dto的工具类
 *提供各种情况返回结果的类型（成功，失败，带参，不带参）
 */
public class DtoUtil {
    public static String success="true";
    public static String fail="false";
    public static String errorCode="0";

    /***
     * 统一返回成功的DTO  不带提示信息 不带数据
     */
    public static Dto returnSuccess(){
        Dto dto=new Dto();
        dto.setSuccess(success);
        return  dto;
    }
    /***
     * 统一返回成功的DTO 带提示信息 带数据
     */
    public static Dto returnSuccess(String message,Object data){
        Dto dto=new Dto();
        dto.setSuccess(success);
        dto.setMsg(message);
        dto.setErrorCode(errorCode);
        dto.setData(data);
        return  dto;
    }
    /***
     * 统一返回成功的DTO 带提示信息 不带数据
     */
    public static Dto returnSuccess(String message){
        Dto dto=new Dto();
        dto.setSuccess(success);
        dto.setMsg(message);
        dto.setErrorCode(errorCode);
        return  dto;
    }
    /***
     * 统一返回成功的DTO 带数据 不带提示信息
     */
    public static Dto returnDataSuccess(Object data){
        Dto dto=new Dto();
        dto.setSuccess(success);
        dto.setErrorCode(errorCode);
        dto.setData(data);
        return  dto;
    }
    /**
     * 统一返回失败的DTO 带提示信息和失败或错误编号
     * */
    public static Dto returnFail(String message,String errorCode){
        Dto dto=new Dto();
        dto.setSuccess(fail);
        dto.setMsg(message);
        dto.setErrorCode(errorCode);
        return  dto;
    }
}
