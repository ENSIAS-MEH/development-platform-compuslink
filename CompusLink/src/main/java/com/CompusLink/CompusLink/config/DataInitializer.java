package com.CompusLink.CompusLink.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.CompusLink.CompusLink.domain.marketplace.model.Item;
import com.CompusLink.CompusLink.domain.marketplace.model.ItemCondition;
import com.CompusLink.CompusLink.domain.marketplace.model.ItemStatus;
import com.CompusLink.CompusLink.domain.marketplace.repository.ItemRepository;
import com.CompusLink.CompusLink.domain.user.model.Users;
import com.CompusLink.CompusLink.domain.user.model.UserRole;
import com.CompusLink.CompusLink.domain.user.repository.UserRepository;
import java.math.BigDecimal;

@Slf4j
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeData(UserRepository userRepository, ItemRepository itemRepository) {
        return args -> {
            if (userRepository.existsByEmail("seller@example.com")) {
                log.info("✅ Mock data already exists, skipping initialization");
                return;
            }
            // Create test user
            Users testUser = Users.builder()
                    .email("seller@example.com")
                    .passwordHash("hashed_password")
                    .fullName("Anas B.")
                    .city("Rabat")
                    .phoneNumber("+212123456789")
                    .role(UserRole.STUDENT)
                    .isActive(true)
                    .isVerified(true)
                    .build();
            testUser = userRepository.save(testUser);

            // Create test items
            itemRepository.save(Item.builder()
                    .title("MacBook Pro M1 2020 - 8GB RAM 256GB SSD")
                    .description("MacBook Pro M1 en excellent état, utilisé pendant 2 ans pour le développement. Batterie à 89% de capacité. Livré avec chargeur original et housse de protection. Aucune rayure sur l'écran.")
                    .price(new BigDecimal("8500.00"))
                    .city("Rabat")
                    .condition(ItemCondition.LIKE_NEW)
                    .category("Electronics")
                    .status(ItemStatus.OPEN)
                    .sellerId(testUser.getId())
                    .build());

            itemRepository.save(Item.builder()
                    .title("Calculatrice TI-84 Plus CE Python")
                    .description("Calculatrice graphique neuve, jamais utilisée. Parfait pour les étudiants en mathématiques et sciences. Inclus manuel d'utilisation.")
                    .price(new BigDecimal("950.00"))
                    .city("Casablanca")
                    .condition(ItemCondition.NEW)
                    .category("Electronics")
                    .status(ItemStatus.OPEN)
                    .sellerId(testUser.getId())
                    .build());

            itemRepository.save(Item.builder()
                    .title("Bureau IKEA MICKE Blanc")
                    .description("Bureau blanc IKEA MICKE, 105cm de large. En bon état, légers signes d'usure. Idéal pour une chambre d'étudiant.")
                    .price(new BigDecimal("400.00"))
                    .city("Tanger")
                    .condition(ItemCondition.GOOD)
                    .category("Furniture")
                    .status(ItemStatus.OPEN)
                    .sellerId(testUser.getId())
                    .build());

            itemRepository.save(Item.builder()
                    .title("Sony WH-1000XM4 Noise Cancelling")
                    .description("Casque audio haut de gamme avec suppression de bruit active. Son cristallin, très confortable. Batterie 30h. Très bon état.")
                    .price(new BigDecimal("2200.00"))
                    .city("Fès")
                    .condition(ItemCondition.LIKE_NEW)
                    .category("Electronics")
                    .status(ItemStatus.OPEN)
                    .sellerId(testUser.getId())
                    .build());

            itemRepository.save(Item.builder()
                    .title("Livre: Introduction aux Algorithmes (CLRS)")
                    .description("Manuel de référence en informatique. Edition française. Très utile pour les étudiants en CS.")
                    .price(new BigDecimal("250.00"))
                    .city("Casablanca")
                    .condition(ItemCondition.GOOD)
                    .category("Books")
                    .status(ItemStatus.OPEN)
                    .sellerId(testUser.getId())
                    .build());

            itemRepository.save(Item.builder()
                    .title("Chaise de bureau ergonomique")
                    .description("Chaise de travail avec support lombaire ajustable. Tissu respirant. Très confortable pour les longues sessions de travail.")
                    .price(new BigDecimal("1500.00"))
                    .city("Rabat")
                    .condition(ItemCondition.LIKE_NEW)
                    .category("Furniture")
                    .status(ItemStatus.OPEN)
                    .sellerId(testUser.getId())
                    .build());

            itemRepository.save(Item.builder()
                    .title("Logitech MX Master 3 Mouse")
                    .description("Souris sans fil haute performance. Précision exceptionnelle. Compatible Windows et Mac.")
                    .price(new BigDecimal("650.00"))
                    .city("Marrakech")
                    .condition(ItemCondition.NEW)
                    .category("Electronics")
                    .status(ItemStatus.OPEN)
                    .sellerId(testUser.getId())
                    .build());

            itemRepository.save(Item.builder()
                    .title("Lampe de Bureau LED")
                    .description("Lampe LED réglable avec 3 niveaux de luminosité. Économe en énergie. Design moderne.")
                    .price(new BigDecimal("180.00"))
                    .city("Tanger")
                    .condition(ItemCondition.NEW)
                    .category("Furniture")
                    .status(ItemStatus.OPEN)
                    .sellerId(testUser.getId())
                    .build());

            log.info("✅ Mock data initialized: 8 marketplace items created successfully");
        };
    }
}
