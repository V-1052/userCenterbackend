package com.yanshui.usercenter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yanshui.usercenter.mapper")
public class UseRCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(UseRCenterApplication.class, args);
    }

}
