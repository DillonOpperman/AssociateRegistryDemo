package com.associateregistry.service;

import com.associateregistry.dto.AssociateResponse;
import com.associateregistry.dto.DashboardStatsResponse;
import com.associateregistry.entity.EmploymentStatus;
import com.associateregistry.repository.AssociateRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final AssociateRepository associateRepository;
    private final AuditLogService auditLogService;

    public DashboardService(AssociateRepository associateRepository, AuditLogService auditLogService) {
        this.associateRepository = associateRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public DashboardStatsResponse stats() {
        List<AssociateResponse> recentRegistrations = associateRepository
                .findTop5ByOrderByRegistrationDateDescIdDesc()
                .stream()
                .map(AssociateResponse::from)
                .toList();
        return new DashboardStatsResponse(
                associateRepository.count(),
                associateRepository.countByStatus(EmploymentStatus.ACTIVE),
                associateRepository.countByStatus(EmploymentStatus.INACTIVE),
                associateRepository.countByStatus(EmploymentStatus.ON_LEAVE),
                auditLogService.recent(),
                recentRegistrations);
    }
}
