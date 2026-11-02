package com.associateregistry.dto;

import java.util.List;

public record DashboardStatsResponse(
        long totalAssociates,
        long activeAssociates,
        long inactiveAssociates,
        long onLeaveAssociates,
        List<AuditLogResponse> recentChanges,
        List<AssociateResponse> recentRegistrations) {
}
