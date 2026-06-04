package com.compuslink.commonservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
@SpringBootApplication(scanBasePackages = {"com.compuslink.commonservice", "com.compuslink.common"}) @EnableFeignClients
public class CommonServiceApplication { public static void main(String[] args) { SpringApplication.run(CommonServiceApplication.class, args); } }
