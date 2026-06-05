package com.compuslink.messaging;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
@SpringBootApplication(scanBasePackages = {"com.compuslink.messaging", "com.compuslink.common"}) @EnableFeignClients
public class MessagingServiceApplication { public static void main(String[] args) { SpringApplication.run(MessagingServiceApplication.class, args); } }
