package com.example.smartplanner.controller;

import com.example.smartplanner.dto.admin.SetUserRoleRequest;
import com.example.smartplanner.dto.admin.UserAdminRow;
import com.example.smartplanner.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
public class AdminController {

    private final AdminService admin;

    public AdminController(AdminService admin) {
        this.admin = admin;
    }

    // GET /admin/users?search=&page=0&size=20&sort=email,asc
    @GetMapping
    public Page<UserAdminRow> listUsers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort
    ) {
        return admin.listUsers(search, page, size, sort);
    }

    @GetMapping("/{id}")
    public UserAdminRow getUser(@PathVariable Long id) {
        return admin.getUser(id);
    }

    // PATCH /admin/users/{id}/role  body: { "role": "MANAGER" }
    @PatchMapping("/{id}/role")
    public UserAdminRow setRole(@PathVariable Long id, @Valid @RequestBody SetUserRoleRequest req) {
        return admin.setRole(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        admin.deleteUser(id);
    }
}
