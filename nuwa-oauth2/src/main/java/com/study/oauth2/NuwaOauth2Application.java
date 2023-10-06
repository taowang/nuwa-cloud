package com.study.oauth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients(basePackages = "com.study")
@ComponentScan(basePackages = "com.study")
@SpringBootApplication
@EnableCaching
public class NuwaOauth2Application {

    public static void main(String[] args) {
        SpringApplication.run(NuwaOauth2Application.class, args);
    }
}