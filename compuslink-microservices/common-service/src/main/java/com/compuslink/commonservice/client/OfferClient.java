package com.compuslink.commonservice.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;
@FeignClient(name = "offer-service") public interface OfferClient { @GetMapping("/internal/offers/{id}/exists") boolean exists(@PathVariable UUID id); }
