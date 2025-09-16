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
        String userAccount = "testAccount1";
        userService.remove(new QueryWrapper<User>().eq("userAccount", userAccount));
        String userPassword = "testPassword";
        String checkPassword = "testPassword";
        long userId = userService.userRegister(userAccount, userPassword, checkPassword);
        assertTrue(userId > 0, "failed to register user");
        User user = userService.getById(userId);
        assertNotNull(user, "the registered user should exist");
        assertEquals(userAccount, user.getUserAccount(), "user account should match");
        assertTrue(userService.getById(userId).getUserPassword().startsWith("$2a$"), "password should be encrypted");
    }
    @Test
    void testUserRegisterWithEmptyAccount() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            userService.userRegister("", "password123", "password123")
        );
        assertTrue(exception.getMessage().contains("account"));
    }

    @Test
    void testUserRegisterWithEmptyPassword() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            userService.userRegister("testAccount2", "", "")
        );
        assertTrue(exception.getMessage().contains("password"));
    }

    @Test
    void testUserRegisterWithDifferentPasswords() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            userService.userRegister("testAccount3", "password123", "password456")
        );
        assertTrue(exception.getMessage().contains("password"));
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
        assertTrue(exception.getMessage().contains("exists"));
    }
}