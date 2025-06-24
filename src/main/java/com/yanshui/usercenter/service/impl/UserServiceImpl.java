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
import static com.yanshui.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author james
* @description 针对表【user(用户)】的数据库操作Service实现
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
        // 1. 非空校验
        if (userAccount == null || userAccount.isEmpty()) {
            throw new IllegalArgumentException("用户账号不能为空");
        }
        if (userPassword == null || userPassword.isEmpty()) {
            throw new IllegalArgumentException("用户密码不能为空");
        }
        if (checkPassword == null || checkPassword.isEmpty()) {
            throw new IllegalArgumentException("确认密码不能为空");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new IllegalArgumentException("两次输入的密码不一致");
        }
        // 2. 账号格式和长度校验
        if (userAccount.length() < 4 || userAccount.length() > 20) {
            throw new IllegalArgumentException("用户账号长度必须在4到20个字符之间");
        }
        if (!userAccount.matches("^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$")) {
            throw new IllegalArgumentException("用户账号不合法");
        }
        // 3. 密码长度校验
        if (userPassword.length() < 8 || userPassword.length() > 20) {
            throw new IllegalArgumentException("用户密码长度必须在8到20个字符之间");
        }
        // 4. 检查账号是否已存在（数据库操作放最后）
        User existingUser = userMapper.selectOne(new QueryWrapper<User>().eq("userAccount", userAccount));
        if (existingUser != null) {
            throw new IllegalArgumentException("用户账号已存在");
        }
        // 5.密码加密
        String encryptedPassword = passwordEncoder.encode(userPassword);
        // 6. 创建新用户
        User newUser = new User();
        newUser.setUserAccount(userAccount);
        newUser.setUserPassword(encryptedPassword);
        int result = userMapper.
                insert(newUser);
        if (result <= 0) {
            throw new RuntimeException("用户注册失败，请稍后再试");
        }

        return newUser.getId();
    }

    @Override
    public User login(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 非空校验
        if (userAccount == null || userAccount.isEmpty()) {
            throw new IllegalArgumentException("用户账号不能为空");
        }
        if (userPassword == null || userPassword.isEmpty()) {
            throw new IllegalArgumentException("用户密码不能为空");
        }
        // 2. 账号格式和长度校验
        if (userAccount.length() < 4 || userAccount.length() > 20) {
            throw new IllegalArgumentException("用户账号长度必须在4到20个字符之间");
        }
        if (!userAccount.matches("^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$")) {
            throw new IllegalArgumentException("用户账号不合规");
        }
        // 3. 密码长度校验
        if (userPassword.length() < 8 || userPassword.length() > 20) {
            throw new IllegalArgumentException("用户密码长度必须在8到20个字符之间");
        }
        // 4. 检查账号是否存在
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("userAccount", userAccount)
        .eq("isDelete", 0)); // 确保查询未被删除的用户
        if (user == null) {
            throw new IllegalArgumentException("用户账号不存在");
        }
        // 5. 验证密码
        if (!passwordEncoder.matches(userPassword, user.getUserPassword())) {
            throw new IllegalArgumentException("密码错误");
        }


        // 6. 记录登录状态
        if (request == null) {
            throw new IllegalArgumentException("请求对象不能为空");
        }
        logger.info("Login userRole: {}", user.getUserRole());
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        logger.info("Session user: {}", request.getSession().getAttribute(USER_LOGIN_STATE));

        // 7. 返回用户信息（脱敏处理）
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

        return safetyUser;
    }

    @Override
    public void userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求对象不能为空");
        }
        // 清除会话中的用户登录状态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        logger.info("User logged out successfully");
    }

}




