package com.associateregistry;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AssociateApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void dashboardStatsComeFromTheDatabase() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAssociates").value(32))
                .andExpect(jsonPath("$.activeAssociates").value(21))
                .andExpect(jsonPath("$.inactiveAssociates").value(6))
                .andExpect(jsonPath("$.onLeaveAssociates").value(5))
                .andExpect(jsonPath("$.recentChanges", hasSize(6)))
                .andExpect(jsonPath("$.recentRegistrations", hasSize(5)))
                .andExpect(jsonPath("$.recentRegistrations[0].associateId").value("AR-10032"));
    }

    @Test
    void directorySupportsSearchFilterSortAndPagination() throws Exception {
        mockMvc.perform(get("/api/associates")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "associateId,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(32))
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.content[0].associateId").value("AR-10001"))
                .andExpect(jsonPath("$.content[9].associateId").value("AR-10010"));

        mockMvc.perform(get("/api/associates").param("search", "elena.vasquez@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].fullName").value("Elena Vasquez"))
                .andExpect(jsonPath("$.content[0].associateId").value("AR-10001"));

        mockMvc.perform(get("/api/associates").param("status", "ON_LEAVE").param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(5));

        mockMvc.perform(get("/api/associates").param("department", "Claims Operations").param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(4));

        mockMvc.perform(get("/api/associates/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(9)));

        mockMvc.perform(get("/api/associates").param("sort", "password,asc"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/associates").param("status", "RETIRED"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void profileCanBeReadById() throws Exception {
        MvcResult listed = mockMvc.perform(get("/api/associates").param("search", "AR-10001"))
                .andExpect(status().isOk())
                .andReturn();
        Number id = JsonPath.read(listed.getResponse().getContentAsString(), "$.content[0].id");

        mockMvc.perform(get("/api/associates/{id}", id.longValue()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("elena.vasquez@example.com"))
                .andExpect(jsonPath("$.department").value("Claims Operations"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        mockMvc.perform(get("/api/associates/{id}", 999999))
                .andExpect(status().isNotFound());
    }

    @Test
    void registrationValidatesPersistsAndWritesAnAuditRecord() throws Exception {
        mockMvc.perform(post("/api/associates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "associateId": "AR-20001",
                                  "lastName": "Adler",
                                  "email": "not-an-email",
                                  "department": "Finance",
                                  "jobTitle": "Payroll Analyst",
                                  "officeLocation": "Midtown Plaza, Chicago"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.firstName").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.status").exists());

        MvcResult created = mockMvc.perform(post("/api/associates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Demo-User", "Jordan Hale (Administrator)")
                        .content(validAssociate("AR-20001", "quinn.adler@example.com", "Payroll Analyst")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.associateId").value("AR-20001"))
                .andExpect(jsonPath("$.fullName").value("Quinn Adler"))
                .andReturn();
        Number id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(post("/api/associates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validAssociate("AR-20001", "someone.else@example.com", "Payroll Analyst")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.fieldErrors.associateId").exists());

        mockMvc.perform(post("/api/associates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validAssociate("AR-20002", "quinn.adler@example.com", "Payroll Analyst")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.fieldErrors.email").exists());

        mockMvc.perform(put("/api/associates/{id}", id.longValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Demo-User", "Jordan Hale (Administrator)")
                        .content(validAssociate("AR-20001", "quinn.adler@example.com", "Senior Payroll Analyst")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobTitle").value("Senior Payroll Analyst"));

        mockMvc.perform(get("/api/audit-logs").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.associateId == 'AR-20001' && @.action == 'UPDATED')].performedBy")
                        .value(hasItem("Jordan Hale (Administrator)")))
                .andExpect(jsonPath("$.content[?(@.associateId == 'AR-20001' && @.action == 'UPDATED')].fieldChanges")
                        .value(hasItem(containsString("Senior Payroll Analyst"))));
    }

    private String validAssociate(String associateId, String email, String jobTitle) {
        return """
                {
                  "associateId": "%s",
                  "firstName": "Quinn",
                  "lastName": "Adler",
                  "email": "%s",
                  "phoneNumber": "(312) 555-0199",
                  "department": "Finance",
                  "jobTitle": "%s",
                  "officeLocation": "Midtown Plaza, Chicago",
                  "status": "ACTIVE"
                }
                """.formatted(associateId, email, jobTitle);
    }
}
