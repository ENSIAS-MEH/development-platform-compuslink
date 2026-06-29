package com.compuslink.offer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.compuslink.offer", "com.compuslink.common"})
@EnableFeignClients
public class OfferServiceApplication {
    public static void main(String[] args) { SpringApplication.run(OfferServiceApplication.class, args); }
}
