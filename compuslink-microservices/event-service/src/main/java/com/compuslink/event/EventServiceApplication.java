package com.compuslink.event;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
@SpringBootApplication(scanBasePackages = {"com.compuslink.event", "com.compuslink.common"}) @EnableFeignClients
public class EventServiceApplication { public static void main(String[] args) { SpringApplication.run(EventServiceApplication.class, args); } }
