package com.yanshui.usercenter.model.domain.request;

import java.io.Serial;

public class UserRegisterRequest implements java.io.Serializable{
    @Serial
    private static final long serialVersionUID = 1L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;

    // Getters and Setters
    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getCheckPassword() {
        return checkPassword;
    }

    public void setCheckPassword(String checkPassword) {
        this.checkPassword = checkPassword;
    }
}
