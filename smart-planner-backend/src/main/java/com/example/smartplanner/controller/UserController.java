package com.example.smartplanner.controller;

import com.example.smartplanner.dto.user.MeResponse;
import com.example.smartplanner.dto.user.UpdateProfileImageRequest;
import com.example.smartplanner.security.UserPrincipal;
import com.example.smartplanner.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal UserPrincipal principal) {
        return users.me(principal.getId());
    }

    @PatchMapping("/me/profile-image")
    public MeResponse updateProfileImage(@AuthenticationPrincipal UserPrincipal principal,
                                         @Valid @RequestBody UpdateProfileImageRequest req) {
        return users.updateProfileImage(principal.getId(), req);
    }
}
