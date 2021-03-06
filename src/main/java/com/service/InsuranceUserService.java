package com.service;

import com.po.InsuranceUser;

public interface InsuranceUserService {
    /**
     * 做数据添加（注册）的方法
     * */
    public Integer save(InsuranceUser user);
    /**
     * 查询账号的方法(做注册账号时判断是否存在的)
     * */
    public InsuranceUser findByUserCode(String userCode);
    /**
     * 用来做账号激活的方法
     * **/
    public Integer updateActived(String userCode);
    /**
     * 修改密码的方法
     * */
    public Integer updateCode(String userPassword,String userCode);
}
