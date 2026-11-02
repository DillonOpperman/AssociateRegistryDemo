package com.associateregistry.service;

import com.associateregistry.dto.AuditLogResponse;
import com.associateregistry.dto.PageResponse;
import com.associateregistry.entity.AuditAction;
import com.associateregistry.entity.AuditLog;
import com.associateregistry.repository.AuditLogRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void record(
            AuditAction action,
            String associateId,
            String associateName,
            String fieldChanges,
            String demoUser) {
        auditLogRepository.save(new AuditLog(
                LocalDateTime.now(),
                action,
                associateId,
                associateName,
                fieldChanges,
                sanitizeDemoUser(demoUser)));
    }

    @Transactional
    public void recordAt(
            LocalDateTime timestamp,
            AuditAction action,
            String associateId,
            String associateName,
            String fieldChanges,
            String demoUser) {
        auditLogRepository.save(new AuditLog(
                timestamp,
                action,
                associateId,
                associateName,
                fieldChanges,
                sanitizeDemoUser(demoUser)));
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditLogResponse> list(int page, int size) {
        var result = auditLogRepository.findAllByOrderByTimestampDesc(
                PageRequest.of(clampPage(page), clampSize(size)));
        return PageResponse.from(result.map(AuditLogResponse::from));
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> recent() {
        return auditLogRepository.findTop6ByOrderByTimestampDescIdDesc().stream()
                .map(AuditLogResponse::from)
                .toList();
    }

    public static String sanitizeDemoUser(String demoUser) {
        if (demoUser == null || demoUser.isBlank()) {
            return "Demo User";
        }
        String cleaned = demoUser.replaceAll("\\p{Cntrl}", "").trim();
        if (cleaned.isEmpty()) {
            return "Demo User";
        }
        return cleaned.length() > 120 ? cleaned.substring(0, 120) : cleaned;
    }

    static int clampPage(int page) {
        return Math.max(page, 0);
    }

    static int clampSize(int size) {
        return Math.min(Math.max(size, 1), 100);
    }
}
