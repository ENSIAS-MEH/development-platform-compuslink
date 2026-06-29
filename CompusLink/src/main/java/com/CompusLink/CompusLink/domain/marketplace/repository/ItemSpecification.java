package com.CompusLink.CompusLink.domain.marketplace.repository;

import com.CompusLink.CompusLink.domain.marketplace.model.Item;
import com.CompusLink.CompusLink.domain.marketplace.model.ItemCondition;
import com.CompusLink.CompusLink.domain.marketplace.model.ItemStatus;
import org.springframework.data.jpa.domain.Specification;

public class ItemSpecification {

    public static Specification<Item> hasCity(String city) {
        return (root, query, cb) ->
                city == null ? null : cb.equal(cb.lower(root.get("city")), city.toLowerCase());
    }

    public static Specification<Item> hasCategory(String category) {
        return (root, query, cb) ->
                category == null ? null : cb.equal(cb.lower(root.get("category")), category.toLowerCase());
    }

    public static Specification<Item> hasCondition(ItemCondition condition) {
        return (root, query, cb) ->
                condition == null ? null : cb.equal(root.get("condition"), condition);
    }

    public static Specification<Item> hasStatus(ItemStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Item> containsSearch(String search) {
        return (root, query, cb) ->
                search == null || search.isBlank() ? null : cb.or(
                    cb.like(cb.lower(root.get("title")), "%" + search.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + search.toLowerCase() + "%")
                );
    }
}
