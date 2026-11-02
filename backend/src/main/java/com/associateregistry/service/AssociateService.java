package com.associateregistry.service;

import com.associateregistry.dto.AssociateRequest;
import com.associateregistry.dto.AssociateResponse;
import com.associateregistry.dto.PageResponse;
import com.associateregistry.entity.Associate;
import com.associateregistry.entity.AuditAction;
import com.associateregistry.entity.EmploymentStatus;
import com.associateregistry.exception.AssociateNotFoundException;
import com.associateregistry.exception.DuplicateResourceException;
import com.associateregistry.repository.AssociateRepository;
import com.associateregistry.repository.AssociateSpecifications;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssociateService {

    private static final Set<String> SORT_FIELDS = Set.of(
            "associateId",
            "firstName",
            "lastName",
            "email",
            "department",
            "jobTitle",
            "officeLocation",
            "status",
            "registrationDate",
            "lastUpdatedAt");

    private final AssociateRepository associateRepository;
    private final AuditLogService auditLogService;

    public AssociateService(AssociateRepository associateRepository, AuditLogService auditLogService) {
        this.associateRepository = associateRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public PageResponse<AssociateResponse> search(
            String search, String department, EmploymentStatus status, int page, int size, String sort) {
        String normalizedSearch = search == null ? null : search.trim();
        if (normalizedSearch != null && normalizedSearch.length() > 100) {
            normalizedSearch = normalizedSearch.substring(0, 100);
        }
        var result = associateRepository.findAll(
                AssociateSpecifications.matching(normalizedSearch, department, status),
                PageRequest.of(AuditLogService.clampPage(page), AuditLogService.clampSize(size), parseSort(sort)));
        return PageResponse.from(result.map(AssociateResponse::from));
    }

    @Transactional(readOnly = true)
    public AssociateResponse get(Long id) {
        return AssociateResponse.from(findAssociate(id));
    }

    @Transactional(readOnly = true)
    public List<String> departments() {
        return associateRepository.findDistinctDepartments();
    }

    @Transactional
    public AssociateResponse create(AssociateRequest request, String demoUser) {
        NormalizedAssociate normalized = normalize(request);
        ensureUnique(normalized, null);
        LocalDateTime now = LocalDateTime.now();
        Associate associate = new Associate(
                normalized.associateId(),
                normalized.firstName(),
                normalized.lastName(),
                normalized.email(),
                normalized.phoneNumber(),
                normalized.department(),
                normalized.jobTitle(),
                normalized.officeLocation(),
                normalized.status(),
                LocalDate.now(),
                now);
        Associate saved = associateRepository.save(associate);
        auditLogService.record(
                AuditAction.CREATED,
                saved.getAssociateId(),
                saved.getFullName(),
                "New associate registered",
                demoUser);
        return AssociateResponse.from(saved);
    }

    @Transactional
    public AssociateResponse update(Long id, AssociateRequest request, String demoUser) {
        Associate associate = findAssociate(id);
        NormalizedAssociate normalized = normalize(request);
        ensureUnique(normalized, associate.getId());
        String changes = describeChanges(associate, normalized);
        apply(associate, normalized);
        associate.setLastUpdatedAt(LocalDateTime.now());
        Associate saved = associateRepository.save(associate);
        if (!changes.isBlank()) {
            auditLogService.record(
                    AuditAction.UPDATED,
                    saved.getAssociateId(),
                    saved.getFullName(),
                    changes,
                    demoUser);
        }
        return AssociateResponse.from(saved);
    }

    private Associate findAssociate(Long id) {
        return associateRepository.findById(id).orElseThrow(() -> new AssociateNotFoundException(id));
    }

    private void ensureUnique(NormalizedAssociate normalized, Long currentId) {
        boolean duplicateId = currentId == null
                ? associateRepository.existsByAssociateIdIgnoreCase(normalized.associateId())
                : associateRepository.existsByAssociateIdIgnoreCaseAndIdNot(normalized.associateId(), currentId);
        if (duplicateId) {
            throw new DuplicateResourceException("associateId", "An associate with this ID already exists.");
        }
        boolean duplicateEmail = currentId == null
                ? associateRepository.existsByEmailIgnoreCase(normalized.email())
                : associateRepository.existsByEmailIgnoreCaseAndIdNot(normalized.email(), currentId);
        if (duplicateEmail) {
            throw new DuplicateResourceException("email", "An associate with this email already exists.");
        }
    }

    private void apply(Associate associate, NormalizedAssociate normalized) {
        associate.setAssociateId(normalized.associateId());
        associate.setFirstName(normalized.firstName());
        associate.setLastName(normalized.lastName());
        associate.setEmail(normalized.email());
        associate.setPhoneNumber(normalized.phoneNumber());
        associate.setDepartment(normalized.department());
        associate.setJobTitle(normalized.jobTitle());
        associate.setOfficeLocation(normalized.officeLocation());
        associate.setStatus(normalized.status());
    }

    private String describeChanges(Associate current, NormalizedAssociate updated) {
        List<String> changes = new ArrayList<>();
        compare(changes, "Associate ID", current.getAssociateId(), updated.associateId());
        compare(changes, "First name", current.getFirstName(), updated.firstName());
        compare(changes, "Last name", current.getLastName(), updated.lastName());
        compare(changes, "Email", current.getEmail(), updated.email());
        compare(changes, "Phone number", current.getPhoneNumber(), updated.phoneNumber());
        compare(changes, "Department", current.getDepartment(), updated.department());
        compare(changes, "Job title", current.getJobTitle(), updated.jobTitle());
        compare(changes, "Office location", current.getOfficeLocation(), updated.officeLocation());
        compare(changes, "Status", current.getStatus().getLabel(), updated.status().getLabel());
        return String.join("; ", changes);
    }

    private void compare(List<String> changes, String label, String current, String updated) {
        String left = current == null ? "" : current;
        String right = updated == null ? "" : updated;
        if (!left.equals(right)) {
            changes.add(label + ": \"" + left + "\" -> \"" + right + "\"");
        }
    }

    private NormalizedAssociate normalize(AssociateRequest request) {
        return new NormalizedAssociate(
                request.associateId().trim().toUpperCase(Locale.ROOT),
                request.firstName().trim(),
                request.lastName().trim(),
                request.email().trim().toLowerCase(Locale.ROOT),
                blankToNull(request.phoneNumber()),
                request.department().trim(),
                request.jobTitle().trim(),
                request.officeLocation().trim(),
                request.status());
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Order.asc("lastName"), Sort.Order.asc("firstName"));
        }
        String[] parts = sort.split(",");
        String field = parts[0].trim();
        if (!SORT_FIELDS.contains(field)) {
            throw new IllegalArgumentException("Sort field is not supported.");
        }
        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())) {
            direction = Sort.Direction.DESC;
        }
        return Sort.by(new Sort.Order(direction, field));
    }

    private record NormalizedAssociate(
            String associateId,
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            String department,
            String jobTitle,
            String officeLocation,
            EmploymentStatus status) {
    }
}
