package com.study.service.base;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * nuwa-base 启动类
 */
@EnableFeignClients(basePackages = "com.study")
@ComponentScan(basePackages = "com.study")
@MapperScan("com.study.*.*.mapper")
@SpringBootApplication
public class NuwaBaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(NuwaBaseApplication.class, args);
    }

}
