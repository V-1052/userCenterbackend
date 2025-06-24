package com.yanshui.usercenter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class UseRCenterApplicationTests {

    @Test
    void passwordEncryptionTest() {
        // This test is a placeholder for password encryption logic
        // You can implement your password encryption test here
        String rawPassword = "测试密码123";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("encodedPassword: " + encodedPassword);

        // 加密后密码应与原始密码不同
        Assertions.assertNotEquals(rawPassword, encodedPassword);

        // 匹配原始密码和加密后密码
        Assertions.assertTrue(encoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void contextLoads() {
    }

}
