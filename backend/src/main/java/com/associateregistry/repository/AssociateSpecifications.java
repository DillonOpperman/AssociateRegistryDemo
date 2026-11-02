package com.associateregistry.repository;

import com.associateregistry.entity.Associate;
import com.associateregistry.entity.EmploymentStatus;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

public final class AssociateSpecifications {

    private AssociateSpecifications() {
    }

    public static Specification<Associate> matching(String search, String department, EmploymentStatus status) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
                var fullName = builder.lower(
                        builder.concat(builder.concat(root.get("firstName"), " "), root.get("lastName")));
                predicates.add(builder.or(
                        builder.like(builder.lower(root.get("firstName")), pattern),
                        builder.like(builder.lower(root.get("lastName")), pattern),
                        builder.like(fullName, pattern),
                        builder.like(builder.lower(root.get("email")), pattern),
                        builder.like(builder.lower(root.get("associateId")), pattern)));
            }
            if (department != null && !department.isBlank()) {
                predicates.add(builder.equal(root.get("department"), department.trim()));
            }
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (predicates.isEmpty()) {
                return builder.conjunction();
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
