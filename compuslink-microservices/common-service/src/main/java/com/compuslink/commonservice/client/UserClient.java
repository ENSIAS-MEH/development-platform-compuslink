package com.compuslink.commonservice.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

@FeignClient(name = "user-service") public interface UserClient { @GetMapping("/internal/users/{id}/exists") boolean exists(@PathVariable UUID id); }
