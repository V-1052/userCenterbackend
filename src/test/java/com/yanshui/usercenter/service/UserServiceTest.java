package com.yanshui.usercenter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanshui.usercenter.model.domain.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {


    @Resource
    private UserService userService;
    @Test
    void testAddUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setUserAccount("testAccount");
        user.setAvatarUrl("http://example.com/avatar.png");
        user.setGender(1);
        user.setUserPassword("testPassword");
        user.setPhone("12345678901");
        user.setEmail("123@mail.com");
        user.setUserStatus(0);
        user.setCreateTime(new java.util.Date());
        user.setUpdateTime(new java.util.Date());
        user.setIsDelete(0);

        boolean result = userService.save(user);
        assertTrue(result);
    }

    @Test
    void testUserRegister() {
        String userAccount = "测试账户";
        userService.remove(new QueryWrapper<User>().eq("userAccount", userAccount));
        String userPassword = "testPassword";
        String checkPassword = "testPassword";
        long userId = userService.userRegister(userAccount, userPassword, checkPassword);
        assertTrue(userId > 0, "用户注册失败，返回的用户ID应大于0");
        User user = userService.getById(userId);
        assertNotNull(user, "注册后应能通过ID查询到用户");
        assertEquals(userAccount, user.getUserAccount(), "注册的用户账号应与输入的账号一致");
        assertTrue(userService.getById(userId).getUserPassword().startsWith("$2a$"), "密码应已加密");
    }
    @Test
    void testUserRegisterWithEmptyAccount() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            userService.userRegister("", "password123", "password123")
        );
        assertTrue(exception.getMessage().contains("账号"));
    }

    @Test
    void testUserRegisterWithEmptyPassword() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            userService.userRegister("testAccount2", "", "")
        );
        assertTrue(exception.getMessage().contains("密码"));
    }

    @Test
    void testUserRegisterWithDifferentPasswords() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            userService.userRegister("testAccount3", "password123", "password456")
        );
        assertTrue(exception.getMessage().contains("密码"));
    }

    @Test
    void testUserRegisterWithDuplicateAccount() {
        String userAccount = "duplicateAccount";
        userService.remove(new QueryWrapper<User>().eq("userAccount", userAccount));
        String userPassword = "password123";
        userService.userRegister(userAccount, userPassword, userPassword);
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            userService.userRegister(userAccount, userPassword, userPassword)
        );
        assertTrue(exception.getMessage().contains("已存在"));
    }
}