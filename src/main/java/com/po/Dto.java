package com.po;


/**
 * 数据传输对象（后端输出对象）
 * 通用结果返回前台对象（通过ajax+json+String 返回后台数据部分）
 * 专门做后台返回给前台数据的实体类
 */
public class Dto<T> {
    private String success;//判断系统是否出错做出相应的true或者false的返回，与业务无关，出现的各种异常
    private String errorCode;//该错误码为自定义，一般0表示无错,根据不同的代码提示判断是否成功
    private String msg;//返回前台对应的提示信息
    private T data;//具体返回数据内容(pojo、自定义VO、其他)

    public Dto() {
    }

    public Dto(String success, String errorCode, String msg, T data) {
        this.success = success;
        this.errorCode = errorCode;
        this.msg = msg;
        this.data = data;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
