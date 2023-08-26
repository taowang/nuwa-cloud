package com.study.service.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * nuwa-system 启动类
 */
@ComponentScan(basePackages = "com.study")
@MapperScan("com.study.*.*.mapper")
@EnableDiscoveryClient
@SpringBootApplication
@EnableCaching
public class NuwaSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(NuwaSystemApplication.class, args);
    }

}
