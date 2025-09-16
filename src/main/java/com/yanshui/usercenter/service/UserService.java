package com.yanshui.usercenter.service;

import com.yanshui.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author yanshui
* @description for the table 【user】 database operation Service
* @createDate 2025-06-13 16:58:52
*/
public interface UserService extends IService<User> {

    /**
     * user registration
     *
     * @param userAccount  user account
     * @param userPassword  user password
     * @param checkPassword confirm password
     * @return new user id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * user login
     *
     * @param userAccount user account
     * @param userPassword user password
     * @param request HTTP request object
     * @return masking data
     */
    User login(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * masking data
     * @param originUser original user object
     * @return masking user object
     */
    User getSafetyUser(User originUser);

    /**
     * user logout
     *
     * @param request HTTP request object
     * @return logout result
     */
    void userLogout(HttpServletRequest request);

    /**
     * infomation edit
     *
     * @param User user
     * @return update result
     */
    User updateUserInfo(User updateUser, HttpServletRequest request);

    /**
     * Logic delete users by setting isDelete = 1
     * @param userIds list of user IDs
     * @return true if successful
     */
    boolean logicDeleteUsers(List<Long> userIds);




}
