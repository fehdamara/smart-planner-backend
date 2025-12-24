package com.example.smartplanner.controller;

import com.example.smartplanner.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/manager")
public class ManagerController {

    @GetMapping("/whoami")
    public Map<String, Object> whoami(@AuthenticationPrincipal UserPrincipal principal) {
        return Map.of(
                "id", principal.getId(),
                "email", principal.getUsername(),
                "role", principal.getRoleName()
        );
    }
}
