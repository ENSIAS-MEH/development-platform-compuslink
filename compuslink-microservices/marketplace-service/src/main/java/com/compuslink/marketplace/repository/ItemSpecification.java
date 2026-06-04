package com.compuslink.marketplace.repository;

import com.compuslink.marketplace.model.Item;
import com.compuslink.marketplace.model.ItemCondition;
import com.compuslink.marketplace.model.ItemStatus;
import org.springframework.data.jpa.domain.Specification;

public class ItemSpecification {

    public static Specification<Item> containsSearch(String search) {
        if (search == null || search.isBlank()) return null;
        String pattern = "%" + search.toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), pattern),
                cb.like(cb.lower(root.get("description")), pattern));
    }

    public static Specification<Item> hasCity(String city) {
        return city == null ? null : (root, query, cb) -> cb.equal(root.get("city"), city);
    }

    public static Specification<Item> hasCategory(String category) {
        return category == null ? null : (root, query, cb) -> cb.equal(root.get("category"), category);
    }

    public static Specification<Item> hasCondition(ItemCondition condition) {
        return condition == null ? null : (root, query, cb) -> cb.equal(root.get("condition"), condition);
    }

    public static Specification<Item> hasStatus(ItemStatus status) {
        return status == null ? null : (root, query, cb) -> cb.equal(root.get("status"), status);
    }
}
