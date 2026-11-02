package com.associateregistry.dto;

import com.associateregistry.entity.Associate;
import com.associateregistry.entity.EmploymentStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AssociateResponse(
        Long id,
        String associateId,
        String fullName,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String department,
        String jobTitle,
        String officeLocation,
        EmploymentStatus status,
        LocalDate registrationDate,
        LocalDateTime lastUpdatedAt) {

    public static AssociateResponse from(Associate associate) {
        return new AssociateResponse(
                associate.getId(),
                associate.getAssociateId(),
                associate.getFullName(),
                associate.getFirstName(),
                associate.getLastName(),
                associate.getEmail(),
                associate.getPhoneNumber(),
                associate.getDepartment(),
                associate.getJobTitle(),
                associate.getOfficeLocation(),
                associate.getStatus(),
                associate.getRegistrationDate(),
                associate.getLastUpdatedAt());
    }
}
