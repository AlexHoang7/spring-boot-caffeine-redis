package com.example.caffeine;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 *
 * @author Alex
 */
@SpringBootApplication
@MapperScan("com.example.caffeine.dao")
public class SpringBootCaffeineApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootCaffeineApplication.class, args);
    }

}
