package com.yanshui.usercenter.service;

import com.yanshui.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author james
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-06-13 16:58:52
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 确认密码
     * @return 注册成功的用户ID
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param request HTTP请求对象
     * @return masking data
     */
    User login(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * masking data
     * @param originUser 原始用户对象
     * @return 安全的用户对象，去除敏感信息
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     *
     * @param request HTTP请求对象
     * @return 是否注销成功
     */
    void userLogout(HttpServletRequest request);




}
