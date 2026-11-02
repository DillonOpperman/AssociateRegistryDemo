package com.associateregistry.dto;

import com.associateregistry.entity.AuditAction;
import com.associateregistry.entity.AuditLog;
import java.time.LocalDateTime;

public record AuditLogResponse(
        Long id,
        LocalDateTime timestamp,
        AuditAction action,
        String associateId,
        String associateName,
        String fieldChanges,
        String performedBy) {

    public static AuditLogResponse from(AuditLog auditLog) {
        return new AuditLogResponse(
                auditLog.getId(),
                auditLog.getTimestamp(),
                auditLog.getAction(),
                auditLog.getAssociateId(),
                auditLog.getAssociateName(),
                auditLog.getFieldChanges(),
                auditLog.getPerformedBy());
    }
}
