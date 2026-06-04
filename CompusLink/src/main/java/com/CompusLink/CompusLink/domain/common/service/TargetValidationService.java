package com.CompusLink.CompusLink.domain.common.service;

import com.CompusLink.CompusLink.domain.colocation.repository.ColocPostRepository;
import com.CompusLink.CompusLink.domain.common.model.TargetType;
import com.CompusLink.CompusLink.domain.event.repository.EventRepository;
import com.CompusLink.CompusLink.domain.marketplace.repository.ItemRepository;
import com.CompusLink.CompusLink.domain.offer.repository.OfferRepository;
import com.CompusLink.CompusLink.domain.user.repository.UserRepository;
import com.CompusLink.CompusLink.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TargetValidationService {

    private final ItemRepository itemRepo;
    private final ColocPostRepository colocPostRepo;
    private final OfferRepository offerRepo;
    private final UserRepository userRepo;
    private final EventRepository eventRepo;

    public TargetValidationService(ItemRepository itemRepo,
                                   ColocPostRepository colocPostRepo,
                                   OfferRepository offerRepo,
                                   UserRepository userRepo,
                                   EventRepository eventRepo) {
        this.itemRepo = itemRepo;
        this.colocPostRepo = colocPostRepo;
        this.offerRepo = offerRepo;
        this.userRepo = userRepo;
        this.eventRepo = eventRepo;
    }

    public void validate(TargetType type, UUID targetId) {
        boolean exists = switch (type) {
            case ITEM -> itemRepo.existsById(targetId);
            case COLOC -> colocPostRepo.existsById(targetId);
            case OFFER -> offerRepo.existsById(targetId);
            case EVENT -> eventRepo.existsById(targetId);
            case USER -> userRepo.existsById(targetId);
        };

        if (!exists) {
            throw new EntityNotFoundException(type.name() + " not found with id: " + targetId);
        }
    }
}
