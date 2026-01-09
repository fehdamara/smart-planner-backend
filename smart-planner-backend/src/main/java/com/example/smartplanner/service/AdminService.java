package com.example.smartplanner.service;

import com.example.smartplanner.dto.admin.SetUserRoleRequest;
import com.example.smartplanner.dto.admin.UserAdminRow;
import com.example.smartplanner.entity.Role;
import com.example.smartplanner.entity.User;
import com.example.smartplanner.exception.BadRequestException;
import com.example.smartplanner.exception.NotFoundException;
import com.example.smartplanner.repository.RoleRepository;
import com.example.smartplanner.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserRepository users;
    private final RoleRepository roles;

    public AdminService(UserRepository users, RoleRepository roles) {
        this.users = users;
        this.roles = roles;
    }

    public Page<UserAdminRow> listUsers(String search, int page, int size, String sort) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        int safePage = Math.max(page, 0);

        Sort sortObj = Sort.by("id").descending();
        if (sort != null && !sort.isBlank()) {
            // expected format: field,dir  (email,asc)
            String[] parts = sort.split(",");
            if (parts.length == 2) {
                String field = parts[0].trim();
                String dir = parts[1].trim().toLowerCase();
                sortObj = "asc".equals(dir) ? Sort.by(field).ascending() : Sort.by(field).descending();
            }
        }

        Pageable pageable = PageRequest.of(safePage, safeSize, sortObj);

        Page<User> result;
        if (search == null || search.isBlank()) {
            result = users.findAll(pageable);
        } else {
            String q = search.trim();
            result = users.findByEmailContainingIgnoreCaseOrNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(
                    q, q, q, pageable
            );
        }

        return result.map(u -> new UserAdminRow(
                u.getId(),
                u.getEmail(),
                u.getName(),
                u.getSurname(),
                u.getRole().getName(),
                u.getProfileImageUrl(),
                u.getRegisteredAt()
        ));
    }

    public UserAdminRow getUser(Long id) {
        User u = users.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        return new UserAdminRow(
                u.getId(), u.getEmail(), u.getName(), u.getSurname(),
                u.getRole().getName(), u.getProfileImageUrl(), u.getRegisteredAt()
        );
    }

    public UserAdminRow setRole(Long userId, SetUserRoleRequest req) {
        String roleName = req.role().trim().toUpperCase();

        if (!roleName.equals("ADMIN") && !roleName.equals("MANAGER") && !roleName.equals("USER")) {
            throw new BadRequestException("Role must be one of: ADMIN, MANAGER, USER");
        }

        User u = users.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        Role r = roles.findByName(roleName)
                .orElseThrow(() -> new BadRequestException("Role not found in DB, seed roles first"));

        u.setRole(r);
        u = users.save(u);

        return new UserAdminRow(
                u.getId(), u.getEmail(), u.getName(), u.getSurname(),
                u.getRole().getName(), u.getProfileImageUrl(), u.getRegisteredAt()
        );
    }

    public void deleteUser(Long userId) {
        if (!users.existsById(userId)) throw new NotFoundException("User not found");
        users.deleteById(userId);
    }
}
