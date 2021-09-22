package com.po;

import java.io.Serializable;
import java.util.Date;

public class InsuranceUser implements Serializable {
    private Integer id;//用户编号，主键
    private String userCode;//注册账号，可以是手机号或邮箱号（自注册用户，超级管理员分配的用户）
    private String userPassword;//账号密码，若是管理员分配，系统将自动生成唯一密码；自注册用户则为自定义密码',
    private Integer userType;//用户类型（标识：0为管理员 1为自注册用户  2为分配的用户）',
    private String userName; //用户真实姓名
    private String weChat;//做激活用的联系方式，手机号或邮箱号根据账号确认（账号是手机号这就是邮箱，反之）',
    private String idnumber;//'身份证号',
    private Date creationDate;//创建时间
    private String createdBy;//创建人
    private Date modifyDate;//修改时间
    private String modifiedBy;//修改人
    private Integer activated;//'账号是否激活,(0 false，1 true,默认是0)'

    public InsuranceUser() {
    }

    public InsuranceUser(Integer id, String userCode, String userPassword, Integer userType, String userName, String weChat, String idnumber, Date creationDate, String createdBy, Date modifyDate, String modifiedBy, Integer activated) {
        this.id = id;
        this.userCode = userCode;
        this.userPassword = userPassword;
        this.userType = userType;
        this.userName = userName;
        this.weChat = weChat;
        this.idnumber = idnumber;
        this.creationDate = creationDate;
        this.createdBy = createdBy;
        this.modifyDate = modifyDate;
        this.modifiedBy = modifiedBy;
        this.activated = activated;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWeChat() {
        return weChat;
    }

    public void setWeChat(String weChat) {
        this.weChat = weChat;
    }

    public String getIdnumber() {
        return idnumber;
    }

    public void setIdnumber(String idnumber) {
        this.idnumber = idnumber;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Integer getActivated() {
        return activated;
    }

    public void setActivated(Integer activated) {
        this.activated = activated;
    }

    @Override
    public String toString() {
        return "InsuranceUser{" +
                "id=" + id +
                ", userCode='" + userCode + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userType=" + userType +
                ", userName='" + userName + '\'' +
                ", weChat='" + weChat + '\'' +
                ", idnumber='" + idnumber + '\'' +
                ", creationDate=" + creationDate +
                ", createdBy='" + createdBy + '\'' +
                ", modifyDate=" + modifyDate +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", activated=" + activated +
                '}';
    }
}
