package com.study.service.system.client.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients(value = {"com.study.service.system.client.fegin"})
@ComponentScan(basePackages = "com.study.service.system.client.fegin.fallback")
@Configuration
public class CommentsAutoConfig {
}
