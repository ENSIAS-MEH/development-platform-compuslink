package com.compuslink.colocation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.compuslink.colocation", "com.compuslink.common"})
@EnableFeignClients
public class ColocationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ColocationServiceApplication.class, args);
    }
}
