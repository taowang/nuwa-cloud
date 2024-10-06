package com.study.nuwa.cloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

/**
 * nuwa-system 启动类
 */
@ComponentScan(basePackages = "com.study.nuwa")
@MapperScan("com.study.nuwa.*.*.mapper")
@SpringBootApplication
@EnableCaching
public class NuwaSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(NuwaSystemApplication.class, args);
    }

}
