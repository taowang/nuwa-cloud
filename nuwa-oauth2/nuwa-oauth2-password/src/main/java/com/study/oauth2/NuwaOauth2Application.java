package com.study.oauth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients(basePackages = "com.study")
@ComponentScan(basePackages = "com.study")
@EnableDiscoveryClient
@SpringBootApplication
@EnableCaching
public class NuwaOauth2Application {

    public static void main(String[] args) {
        SpringApplication.run(NuwaOauth2Application.class, args);
    }
}