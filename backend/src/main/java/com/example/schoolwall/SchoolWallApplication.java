package com.example.schoolwall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 校园微墙后端启动类
 */
@SpringBootApplication
@MapperScan("com.example.schoolwall.mapper")
public class SchoolWallApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchoolWallApplication.class, args);
    }
}
