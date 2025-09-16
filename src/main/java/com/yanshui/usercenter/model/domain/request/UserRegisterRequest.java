package com.yanshui.usercenter.model.domain.request;

import lombok.Setter;

import java.io.Serial;

public class UserRegisterRequest implements java.io.Serializable{
    @Serial
    private static final long serialVersionUID = 1L;
    @Setter

    private String userAccount;
    @Setter
    private String userPassword;
    @Setter
    private String checkPassword;

    public String getUserAccount() {
        return userAccount;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getCheckPassword() {
        return checkPassword;
    }

}
