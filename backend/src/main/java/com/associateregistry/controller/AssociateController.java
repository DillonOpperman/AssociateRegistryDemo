package com.associateregistry.controller;

import com.associateregistry.dto.AssociateRequest;
import com.associateregistry.dto.AssociateResponse;
import com.associateregistry.dto.PageResponse;
import com.associateregistry.entity.EmploymentStatus;
import com.associateregistry.service.AssociateService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/associates")
public class AssociateController {

    private final AssociateService associateService;

    public AssociateController(AssociateService associateService) {
        this.associateService = associateService;
    }

    @GetMapping
    public PageResponse<AssociateResponse> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) EmploymentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        return associateService.search(search, department, status, page, size, sort);
    }

    @GetMapping("/departments")
    public List<String> departments() {
        return associateService.departments();
    }

    @GetMapping("/{id}")
    public AssociateResponse get(@PathVariable Long id) {
        return associateService.get(id);
    }

    @PostMapping
    public ResponseEntity<AssociateResponse> create(
            @Valid @RequestBody AssociateRequest request,
            @RequestHeader(value = "X-Demo-User", required = false) String demoUser) {
        AssociateResponse created = associateService.create(request, demoUser);
        return ResponseEntity.created(URI.create("/api/associates/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public AssociateResponse update(
            @PathVariable Long id,
            @Valid @RequestBody AssociateRequest request,
            @RequestHeader(value = "X-Demo-User", required = false) String demoUser) {
        return associateService.update(id, request, demoUser);
    }
}
