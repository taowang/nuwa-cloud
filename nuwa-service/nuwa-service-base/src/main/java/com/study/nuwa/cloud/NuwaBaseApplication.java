package com.study.nuwa.cloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * nuwa-base 启动类
 */
@EnableFeignClients(basePackages = "com.study.nuwa")
@ComponentScan(basePackages = "com.study.nuwa")
@MapperScan("com.study.nuwa.*.*.mapper")
@SpringBootApplication
public class NuwaBaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(NuwaBaseApplication.class, args);
    }

}
