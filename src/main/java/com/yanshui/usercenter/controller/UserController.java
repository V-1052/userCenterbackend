package com.yanshui.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanshui.usercenter.model.domain.User;
import com.yanshui.usercenter.model.domain.request.UserLoginRequest;
import com.yanshui.usercenter.model.domain.request.UserRegisterRequest;
import com.yanshui.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

import static com.yanshui.usercenter.constant.UserConstant.USER_LOGIN_STATE;
import static com.yanshui.usercenter.constant.UserConstant.USER_ROLE_ADMIN;

/** * user controller
 * process user registration, login, search, delete, logout
 * @author yanshui
 * @date 2025-06-13
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:3000", "https://usercenter-frontend-89e77fb22c09.herokuapp.com"}, allowCredentials = "true", maxAge = 3600)
public class UserController {

    @Resource
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register")
    public Long register(@RequestBody UserRegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("non-null request required");
        }
        return userService.userRegister(
                request.getUserAccount(),
                request.getUserPassword(),
                request.getCheckPassword()
        );
    }
    @PostMapping("/login")
    public Long userLogin(@RequestBody UserLoginRequest request, HttpServletRequest httpRequest) {
        if (request == null) {
            throw new IllegalArgumentException("non-null request required");
        }
        User user = userService.login(
                request.getUserAccount(),
                request.getUserPassword(),
                httpRequest);
        if (user == null) {
            throw new IllegalArgumentException("login failed" );
        }
        return user.getId();
    }
    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new IllegalArgumentException("insufficient permissions" );
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDelete", 0); // filter out deleted users
        if (username != null && !username.isEmpty()) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        return userList.stream()
                .map(user -> userService.getSafetyUser(user))
                .toList();
    }
    @PostMapping("/delete")
    public List<User> deleteUser(@RequestBody List<Long> userIds, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new IllegalArgumentException("insufficient permissions" );
        }
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("user ID list cannot be empty" );
        }
        boolean success = userService.logicDeleteUsers(userIds);
        // Return all users that are not deleted
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDelete", 0);
        List<User> userList = userService.list(queryWrapper);
        return userList.stream()
                .map(user -> userService.getSafetyUser(user))
                .toList();
    }

    @PostMapping("/logout")
    public String userLogout(HttpServletRequest request) {
        userService.userLogout(request);
        return "logout successful";
    }


    @GetMapping("/current")
    public User getCurrentUser(HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        logger.info("Session ID: {}", sessionId);
        logger.info("Session USER_LOGIN_STATE: {}", request.getSession().getAttribute(USER_LOGIN_STATE));
        User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (currentUser == null) {
            return null;
        }
        User user = userService.getById(currentUser.getId());
        return userService.getSafetyUser(user);
    }


    private boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        logger.info("Login userRole: {}", user.getUserRole());
        logger.info("Session user: {}", request.getSession().getAttribute(USER_LOGIN_STATE));

        return user != null && user.getUserRole() == USER_ROLE_ADMIN;
    }

    @PostMapping("/update")
    public User updateUser(@RequestBody User updateUser, HttpServletRequest request) {
        if (updateUser == null) {
            throw new IllegalArgumentException("non-null request required");
        }
        return userService.updateUserInfo(updateUser, request);
    }

}
