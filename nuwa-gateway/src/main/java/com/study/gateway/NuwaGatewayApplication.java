package com.study.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.study")
@SpringBootApplication
@EnableCaching
public class NuwaGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(NuwaGatewayApplication.class,args);
    }
}