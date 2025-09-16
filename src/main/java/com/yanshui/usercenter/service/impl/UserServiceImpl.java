package com.yanshui.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanshui.usercenter.model.domain.User;
import com.yanshui.usercenter.service.UserService;
import com.yanshui.usercenter.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.yanshui.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author yanshui
* @description for the table 【user】 database operation Service implementation
* @createDate 2025-06-13 16:58:52
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserMapper userMapper;


    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }



    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. no null check
        if (userAccount == null || userAccount.isEmpty()) {
            throw new IllegalArgumentException("user account cannot be empty");
        }
        if (userPassword == null || userPassword.isEmpty()) {
            throw new IllegalArgumentException("user password cannot be empty");
        }
        if (checkPassword == null || checkPassword.isEmpty()) {
            throw new IllegalArgumentException("check password cannot be empty");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new IllegalArgumentException("the two passwords do not match");
        }

        // 2. account format and length check
        if (userAccount.length() < 4 || userAccount.length() > 20) {
            throw new IllegalArgumentException("user account length must be between 4 and 20 characters");
        }
        if (!userAccount.matches("^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$")) {
            throw new IllegalArgumentException("illegal user account");
        }

        // 3. password length check
        if (userPassword.length() < 8 || userPassword.length() > 20) {
            throw new IllegalArgumentException("user password length must be between 8 and 20 characters");
        }
        // 4. check if account exists
        User existingUser = userMapper.selectOne(new QueryWrapper<User>().eq("userAccount", userAccount));
        if (existingUser != null) {
            throw new IllegalArgumentException("user account already exists");
        }

        // 5. encrypt password
        String encryptedPassword = passwordEncoder.encode(userPassword);
        // 6. insert user into database
        User newUser = new User();
        newUser.setUserAccount(userAccount);
        newUser.setUserPassword(encryptedPassword);
        int result = userMapper.
                insert(newUser);
        if (result <= 0) {
            throw new RuntimeException("failed to register user, please try again later");
        }

        return newUser.getId();
    }

    @Override
    public User login(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. no null check
        if (userAccount == null || userAccount.isEmpty()) {
            throw new IllegalArgumentException("user account cannot be empty");
        }
        if (userPassword == null || userPassword.isEmpty()) {
            throw new IllegalArgumentException("user password cannot be empty");
        }
        // 2. account format and length check
        if (userAccount.length() < 4 || userAccount.length() > 20) {
            throw new IllegalArgumentException("user account length must be between 4 and 20 characters");
        }
        if (!userAccount.matches("^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$")) {
            throw new IllegalArgumentException("illegal user account");
        }
        // 3. password length check
        if (userPassword.length() < 8 || userPassword.length() > 20) {
            throw new IllegalArgumentException("user password length must be between 8 and 20 characters");
        }
        // 4. check if account exists
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("userAccount", userAccount)
        .eq("isDelete", 0)); // ensure the user is not deleted
        if (user == null) {
            throw new IllegalArgumentException("user account does not exist");
        }
        // 5. verify password
        if (!passwordEncoder.matches(userPassword, user.getUserPassword())) {
            throw new IllegalArgumentException("incorrect password");
        }

        // 6. update user status to logged in
        user.setUserStatus(1); // set status to logged in
        userMapper.updateById(user);

        // 7. record user login state
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }
        logger.info("Login userRole: {}", user.getUserRole());
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        logger.info("Session user: {}", request.getSession().getAttribute(USER_LOGIN_STATE));

        // 8. masking data
        return getSafetyUser(user);
    }

    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUserRole(originUser.getUserRole());
        return safetyUser;
    }

    @Override
    public void userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("invalid request");
        }
        // get user before removing session
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user != null) {
            user.setUserStatus(0); // set status to logged out
            userMapper.updateById(user);
        }
        // remove user login state
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        logger.info("User logged out successfully");
    }

    /**
     * Update user info for the logged-in user
     */
    public User updateUserInfo(User updateUser, HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null || !(userObj instanceof User)) {
            throw new IllegalArgumentException("user not logged in");
        }
        User currentUser = (User) userObj;
        // Only allow updating safe fields
        if (updateUser.getUsername() != null) {
            currentUser.setUsername(updateUser.getUsername());
        }
        if (updateUser.getAvatarUrl() != null) {
            currentUser.setAvatarUrl(updateUser.getAvatarUrl());
        }
        if (updateUser.getGender() != null) {
            currentUser.setGender(updateUser.getGender());
        }
        if (updateUser.getPhone() != null) {
            currentUser.setPhone(updateUser.getPhone());
        }
        if (updateUser.getEmail() != null) {
            currentUser.setEmail(updateUser.getEmail());
        }
        // Save changes to DB
        int result = userMapper.updateById(currentUser);
        if (result <= 0) {
            throw new RuntimeException("failed to update user info");
        }
        return getSafetyUser(currentUser);
    }

    @Override
    public boolean logicDeleteUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return false;
        }
        for (Long userId : userIds) {
            User user = userMapper.selectById(userId);
            if (user != null && (user.getIsDelete() == null || user.getIsDelete() == 0)) {
                user.setIsDelete(1);
                userMapper.updateById(user);
            }
        }
        return true;
    }

}
