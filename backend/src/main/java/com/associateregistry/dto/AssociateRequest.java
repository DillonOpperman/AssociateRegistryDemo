package com.associateregistry.dto;

import com.associateregistry.entity.EmploymentStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AssociateRequest(
        @NotBlank(message = "Associate ID is required")
        @Size(max = 20, message = "Associate ID must be 20 characters or fewer")
        @Pattern(regexp = "(?i)AR-\\d{5}", message = "Associate ID must look like AR-10001")
        String associateId,

        @NotBlank(message = "First name is required")
        @Size(max = 60, message = "First name must be 60 characters or fewer")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 60, message = "Last name must be 60 characters or fewer")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email address")
        @Size(max = 120, message = "Email must be 120 characters or fewer")
        String email,

        @Size(max = 30, message = "Phone number must be 30 characters or fewer")
        @Pattern(
                regexp = "^$|^[+0-9().\\-\\s]{7,30}$",
                message = "Enter a valid phone number or leave it blank")
        String phoneNumber,

        @NotBlank(message = "Department is required")
        @Size(max = 80, message = "Department must be 80 characters or fewer")
        String department,

        @NotBlank(message = "Job title is required")
        @Size(max = 80, message = "Job title must be 80 characters or fewer")
        String jobTitle,

        @NotBlank(message = "Office location is required")
        @Size(max = 80, message = "Office location must be 80 characters or fewer")
        String officeLocation,

        @NotNull(message = "Status is required")
        EmploymentStatus status) {
}
