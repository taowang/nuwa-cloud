package com.study.nuwa.cloud.monitor;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@EnableAdminServer
@SpringBootApplication
@RefreshScope
public class NuwaMonitorApplication {
    
    public static void main(String[] args)
    {
        SpringApplication.run(NuwaMonitorApplication.class, args);
    }
    
}
