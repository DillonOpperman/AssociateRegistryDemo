package com.associateregistry.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuditAction action;

    @Column(name = "associate_id", nullable = false, length = 20)
    private String associateId;

    @Column(name = "associate_name", nullable = false, length = 140)
    private String associateName;

    @Column(name = "field_changes", length = 2000)
    private String fieldChanges;

    @Column(name = "performed_by", nullable = false, length = 120)
    private String performedBy;

    protected AuditLog() {
    }

    public AuditLog(
            LocalDateTime timestamp,
            AuditAction action,
            String associateId,
            String associateName,
            String fieldChanges,
            String performedBy) {
        this.timestamp = timestamp;
        this.action = action;
        this.associateId = associateId;
        this.associateName = associateName;
        this.fieldChanges = fieldChanges;
        this.performedBy = performedBy;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public AuditAction getAction() {
        return action;
    }

    public String getAssociateId() {
        return associateId;
    }

    public String getAssociateName() {
        return associateName;
    }

    public String getFieldChanges() {
        return fieldChanges;
    }

    public String getPerformedBy() {
        return performedBy;
    }
}
