package com.yanshui.usercenter.model.domain.request;

public class UserLoginRequest implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    private String userAccount;
    private String userPassword;

    // Getters and Setters
    public String getUserAccount() {
        return userAccount;
    }

    public String getUserPassword() {
        return userPassword;
    }
}
