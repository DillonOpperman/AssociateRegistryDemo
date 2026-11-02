package com.associateregistry.configuration;

import com.associateregistry.entity.Associate;
import com.associateregistry.entity.AuditAction;
import com.associateregistry.entity.AuditLog;
import com.associateregistry.entity.EmploymentStatus;
import com.associateregistry.repository.AssociateRepository;
import com.associateregistry.repository.AuditLogRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AssociateRepository associateRepository;
    private final AuditLogRepository auditLogRepository;

    public DataSeeder(AssociateRepository associateRepository, AuditLogRepository auditLogRepository) {
        this.associateRepository = associateRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (associateRepository.count() > 0) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        List<Associate> associates = new ArrayList<>();
        List<AuditLog> audits = new ArrayList<>();

        for (Seed seed : seeds()) {
            LocalDateTime registeredAt = today.minusDays(seed.registeredDaysAgo()).atTime(9, 0);
            // Keep seeded updates in the past so a record created during a demo or test is newer.
            LocalDateTime updatedAt = seed.updatedDaysAgo() == null
                    ? registeredAt
                    : now.minusMinutes(seed.updatedDaysAgo() * 24L * 60L + (24 - seed.updatedHour()) * 60L);
            associates.add(new Associate(
                    seed.associateId(),
                    seed.firstName(),
                    seed.lastName(),
                    seed.email(),
                    seed.phone(),
                    seed.department(),
                    seed.jobTitle(),
                    seed.office(),
                    seed.status(),
                    today.minusDays(seed.registeredDaysAgo()),
                    updatedAt));
            audits.add(new AuditLog(
                    registeredAt,
                    AuditAction.CREATED,
                    seed.associateId(),
                    seed.firstName() + " " + seed.lastName(),
                    "New associate registered",
                    "System Seed"));
            if (seed.updatedDaysAgo() != null) {
                audits.add(new AuditLog(
                        updatedAt,
                        AuditAction.UPDATED,
                        seed.associateId(),
                        seed.firstName() + " " + seed.lastName(),
                        seed.fieldChanges(),
                        "Jordan Hale (Administrator)"));
            }
        }

        associateRepository.saveAll(associates);
        auditLogRepository.saveAll(audits);
    }

    private List<Seed> seeds() {
        return List.of(
                seed("AR-10001", "Elena", "Vasquez", "elena.vasquez@example.com", "(206) 555-0148",
                        "Claims Operations", "Claims Analyst", "North Harbor Office, Seattle",
                        EmploymentStatus.ACTIVE, 420, null, 0, null),
                seed("AR-10002", "Marcus", "Hale", "marcus.hale@example.com", "(206) 555-0172",
                        "Claims Operations", "Senior Claims Analyst", "North Harbor Office, Seattle",
                        EmploymentStatus.ACTIVE, 400, 6, 11,
                        "Job title: \"Claims Analyst\" -> \"Senior Claims Analyst\""),
                seed("AR-10003", "Priya", "Nandakumar", "priya.nandakumar@example.com", "(612) 555-0134",
                        "Underwriting", "Underwriting Specialist", "Lakeside Center, Minneapolis",
                        EmploymentStatus.ACTIVE, 380, null, 0, null),
                seed("AR-10004", "Jonah", "Ellis", "jonah.ellis@example.com", "(612) 555-0188",
                        "Underwriting", "Underwriting Specialist", "Lakeside Center, Minneapolis",
                        EmploymentStatus.ON_LEAVE, 360, 2, 9,
                        "Status: \"Active\" -> \"On Leave\""),
                seed("AR-10005", "Amira", "Hassan", "amira.hassan@example.com", "(312) 555-0160",
                        "Customer Experience", "Service Consultant", "Midtown Plaza, Chicago",
                        EmploymentStatus.ACTIVE, 340, null, 0, null),
                seed("AR-10006", "Caleb", "Nguyen", "caleb.nguyen@example.com", "(312) 555-0104",
                        "Customer Experience", "Service Consultant", "Midtown Plaza, Chicago",
                        EmploymentStatus.ACTIVE, 320, null, 0, null),
                seed("AR-10007", "Sophie", "Laurent", "sophie.laurent@example.com", "(312) 555-0191",
                        "Information Technology", "Software Engineer", "Midtown Plaza, Chicago",
                        EmploymentStatus.INACTIVE, 300, 20, 13,
                        "Status: \"Active\" -> \"Inactive\""),
                seed("AR-10008", "Diego", "Morales", "diego.morales@example.com", "(303) 555-0144",
                        "Information Technology", "Software Engineer", "Capitol Square, Denver",
                        EmploymentStatus.ACTIVE, 280, null, 0, null),
                seed("AR-10009", "Hannah", "Brooks", "hannah.brooks@example.com", "(303) 555-0177",
                        "Finance", "Financial Analyst", "Capitol Square, Denver",
                        EmploymentStatus.ACTIVE, 260, 4, 10,
                        "Department: \"Enterprise Operations\" -> \"Finance\"; Job title: \"Operations Coordinator\" -> \"Financial Analyst\""),
                seed("AR-10010", "Omar", "Farouk", "omar.farouk@example.com", "(512) 555-0126",
                        "Finance", "Financial Analyst", "Riverside Campus, Austin",
                        EmploymentStatus.ACTIVE, 240, null, 0, null),
                seed("AR-10011", "Grace", "Kim", "grace.kim@example.com", "(512) 555-0182",
                        "Human Resources", "HR Coordinator", "Riverside Campus, Austin",
                        EmploymentStatus.ON_LEAVE, 220, 9, 8,
                        "Status: \"Active\" -> \"On Leave\""),
                seed("AR-10012", "Liam", "Okafor", "liam.okafor@example.com", "(404) 555-0155",
                        "Human Resources", "HR Coordinator", "Peachtree Office, Atlanta",
                        EmploymentStatus.ACTIVE, 200, null, 0, null),
                seed("AR-10013", "Nora", "Petrov", "nora.petrov@example.com", "(404) 555-0119",
                        "Legal and Compliance", "Compliance Analyst", "Peachtree Office, Atlanta",
                        EmploymentStatus.ACTIVE, 190, null, 0, null),
                seed("AR-10014", "Ethan", "Walsh", "ethan.walsh@example.com", "(480) 555-0166",
                        "Legal and Compliance", "Compliance Analyst", "Desert Ridge Center, Phoenix",
                        EmploymentStatus.INACTIVE, 180, 15, 14,
                        "Status: \"Active\" -> \"Inactive\""),
                seed("AR-10015", "Mei", "Chen", "mei.chen@example.com", "(617) 555-0138",
                        "Marketing", "Communications Specialist", "Harbor Point, Boston",
                        EmploymentStatus.ACTIVE, 170, null, 0, null),
                seed("AR-10016", "Andre", "Silva", "andre.silva@example.com", "(617) 555-0194",
                        "Enterprise Operations", "Operations Coordinator", "Harbor Point, Boston",
                        EmploymentStatus.ACTIVE, 160, null, 0, null),
                seed("AR-10017", "Fiona", "Gallagher", "fiona.gallagher@example.com", "(206) 555-0108",
                        "Claims Operations", "Claims Analyst", "North Harbor Office, Seattle",
                        EmploymentStatus.ACTIVE, 150, null, 0, null),
                seed("AR-10018", "Jamal", "Rivers", "jamal.rivers@example.com", "(612) 555-0141",
                        "Underwriting", "Senior Underwriter", "Lakeside Center, Minneapolis",
                        EmploymentStatus.ON_LEAVE, 140, 1, 11,
                        "Status: \"Active\" -> \"On Leave\"; Job title: \"Underwriting Specialist\" -> \"Senior Underwriter\""),
                seed("AR-10019", "Clara", "Svensson", "clara.svensson@example.com", "(312) 555-0175",
                        "Customer Experience", "Team Lead", "Midtown Plaza, Chicago",
                        EmploymentStatus.ACTIVE, 130, 8, 16,
                        "Job title: \"Service Consultant\" -> \"Team Lead\""),
                seed("AR-10020", "Victor", "Almeida", "victor.almeida@example.com", "(303) 555-0122",
                        "Information Technology", "Business Analyst", "Capitol Square, Denver",
                        EmploymentStatus.ACTIVE, 120, null, 0, null),
                seed("AR-10021", "Aisha", "Rahman", "aisha.rahman@example.com", "(512) 555-0186",
                        "Information Technology", "Software Engineer", "Riverside Campus, Austin",
                        EmploymentStatus.INACTIVE, 110, 12, 9,
                        "Status: \"Active\" -> \"Inactive\""),
                seed("AR-10022", "Benjamin", "Crowe", "benjamin.crowe@example.com", "(404) 555-0102",
                        "Finance", "Accountant", "Peachtree Office, Atlanta",
                        EmploymentStatus.ACTIVE, 100, null, 0, null),
                seed("AR-10023", "Yuki", "Tanaka", "yuki.tanaka@example.com", "(480) 555-0158",
                        "Finance", "Financial Analyst", "Desert Ridge Center, Phoenix",
                        EmploymentStatus.ACTIVE, 90, null, 0, null),
                seed("AR-10024", "Rosa", "Delgado", "rosa.delgado@example.com", "(617) 555-0147",
                        "Human Resources", "Recruiter", "Harbor Point, Boston",
                        EmploymentStatus.ACTIVE, 80, null, 0, null),
                seed("AR-10025", "Patrick", "Byrne", "patrick.byrne@example.com", "(206) 555-0198",
                        "Legal and Compliance", "Paralegal", "North Harbor Office, Seattle",
                        EmploymentStatus.INACTIVE, 70, 18, 15,
                        "Status: \"Active\" -> \"Inactive\""),
                seed("AR-10026", "Leila", "Abboud", "leila.abboud@example.com", "(612) 555-0163",
                        "Marketing", "Communications Specialist", "Lakeside Center, Minneapolis",
                        EmploymentStatus.ACTIVE, 60, null, 0, null),
                seed("AR-10027", "Samuel", "Ortega", "samuel.ortega@example.com", "(312) 555-0114",
                        "Enterprise Operations", "Operations Coordinator", "Midtown Plaza, Chicago",
                        EmploymentStatus.ON_LEAVE, 45, 3, 14,
                        "Status: \"Active\" -> \"On Leave\""),
                seed("AR-10028", "Ingrid", "Solberg", "ingrid.solberg@example.com", "(303) 555-0180",
                        "Customer Experience", "Service Consultant", "Capitol Square, Denver",
                        EmploymentStatus.ACTIVE, 30, null, 0, null),
                seed("AR-10029", "Theo", "Marin", "theo.marin@example.com", "(512) 555-0133",
                        "Information Technology", "Systems Analyst", "Riverside Campus, Austin",
                        EmploymentStatus.INACTIVE, 21, 11, 10,
                        "Office location: \"Capitol Square, Denver\" -> \"Riverside Campus, Austin\"; Status: \"Active\" -> \"Inactive\""),
                seed("AR-10030", "Camille", "Dubois", "camille.dubois@example.com", "(404) 555-0171",
                        "Underwriting", "Underwriting Specialist", "Peachtree Office, Atlanta",
                        EmploymentStatus.ACTIVE, 14, null, 0, null),
                seed("AR-10031", "Wesley", "Grant", "wesley.grant@example.com", "(480) 555-0129",
                        "Claims Operations", "Claims Analyst", "Desert Ridge Center, Phoenix",
                        EmploymentStatus.ON_LEAVE, 7, 1, 15,
                        "Status: \"Active\" -> \"On Leave\""),
                seed("AR-10032", "Nadia", "Kowalski", "nadia.kowalski@example.com", "(617) 555-0150",
                        "Enterprise Operations", "Operations Coordinator", "Harbor Point, Boston",
                        EmploymentStatus.INACTIVE, 3, 0, 16,
                        "Status: \"Active\" -> \"Inactive\""));
    }

    private Seed seed(
            String associateId,
            String firstName,
            String lastName,
            String email,
            String phone,
            String department,
            String jobTitle,
            String office,
            EmploymentStatus status,
            int registeredDaysAgo,
            Integer updatedDaysAgo,
            int updatedHour,
            String fieldChanges) {
        return new Seed(
                associateId,
                firstName,
                lastName,
                email,
                phone,
                department,
                jobTitle,
                office,
                status,
                registeredDaysAgo,
                updatedDaysAgo,
                updatedHour,
                fieldChanges);
    }

    private record Seed(
            String associateId,
            String firstName,
            String lastName,
            String email,
            String phone,
            String department,
            String jobTitle,
            String office,
            EmploymentStatus status,
            int registeredDaysAgo,
            Integer updatedDaysAgo,
            int updatedHour,
            String fieldChanges) {
    }
}
