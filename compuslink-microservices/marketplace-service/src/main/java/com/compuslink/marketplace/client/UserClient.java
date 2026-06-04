package com.compuslink.marketplace.client;

import com.compuslink.common.dto.UserSummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/internal/users/{id}/summary")
    UserSummaryDTO getUserSummary(@PathVariable UUID id);

    @GetMapping("/internal/users/{id}/exists")
    boolean userExists(@PathVariable UUID id);
}
