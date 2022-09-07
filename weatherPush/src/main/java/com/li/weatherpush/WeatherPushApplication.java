package com.li.weatherpush;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class WeatherPushApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherPushApplication.class, args);
    }

}
