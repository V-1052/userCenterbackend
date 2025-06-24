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

/** * 用户控制器
 * 处理用户相关的请求
 * @author james
 * @date 2025-06-13
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register")
    public Long register(@RequestBody UserRegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
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
            throw new IllegalArgumentException("请求参数不能为空");
        }
        User user = userService.login(
                request.getUserAccount(),
                request.getUserPassword(),
                httpRequest);
        if (user == null) {
            throw new IllegalArgumentException("登录失败，用户不存在或密码错误");
        }
        return user.getId(); // 返回用户ID或null
    }
    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new IllegalArgumentException("无权限访问");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        return userList.stream()
                .map(user -> userService.getSafetyUser(user)) // 避免敏感信息泄露
                .toList();
    }
    @PostMapping("/delete")
    public List<User> deleteUser(@RequestBody List<Long> userIds, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new IllegalArgumentException("无权限访问");
        }
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("用户ID列表不能为空");
        }
        return userService.removeByIds(userIds) ? userService.listByIds(userIds) : null;
    }

    @PostMapping("/logout")
    public String userLogout(HttpServletRequest request) {
        userService.userLogout(request);
        return "注销成功";
    }

    private boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        logger.info("Login userRole: {}", user.getUserRole());
        logger.info("Session user: {}", request.getSession().getAttribute(USER_LOGIN_STATE));

        return user != null && user.getUserRole() == USER_ROLE_ADMIN;
    }

}
