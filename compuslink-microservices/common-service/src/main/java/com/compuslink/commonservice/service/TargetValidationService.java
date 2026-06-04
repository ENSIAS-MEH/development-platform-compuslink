package com.compuslink.commonservice.service;

import com.compuslink.common.exception.EntityNotFoundException;
import com.compuslink.commonservice.client.*;
import com.compuslink.commonservice.model.TargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class TargetValidationService {
    private final MarketplaceClient marketplaceClient;
    private final ColocationClient colocationClient;
    private final OfferClient offerClient;
    private final UserClient userClient;

    public void validate(TargetType type, UUID targetId) {
        boolean exists = switch (type) {
            case ITEM -> marketplaceClient.exists(targetId);
            case COLOC -> colocationClient.exists(targetId);
            case OFFER -> offerClient.exists(targetId);
            case USER -> userClient.exists(targetId);
        };
        if (!exists) throw new EntityNotFoundException(type + " not found: " + targetId);
    }
}
