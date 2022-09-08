package com.li.gateway9010;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class Gateway9010Application {

    public static void main(String[] args) {
        SpringApplication.run(Gateway9010Application.class, args);
    }

}
