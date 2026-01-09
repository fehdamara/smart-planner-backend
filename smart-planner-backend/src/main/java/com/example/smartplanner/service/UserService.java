package com.example.smartplanner.service;

import com.example.smartplanner.dto.user.MeResponse;
import com.example.smartplanner.dto.user.UpdateProfileImageRequest;
import com.example.smartplanner.entity.User;
import com.example.smartplanner.exception.NotFoundException;
import com.example.smartplanner.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository users;

    public UserService(UserRepository users) {
        this.users = users;
    }

    public MeResponse me(Long userId) {
        User u = users.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        return new MeResponse(
                u.getId(),
                u.getEmail(),
                u.getName(),
                u.getSurname(),
                u.getRole().getName(),
                u.getProfileImageUrl(),
                u.getRegisteredAt()
        );
    }

    public MeResponse updateProfileImage(Long userId, UpdateProfileImageRequest req) {
        User u = users.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        u.setProfileImageUrl(req.profileImageUrl());
        u = users.save(u);
        return new MeResponse(
                u.getId(),
                u.getEmail(),
                u.getName(),
                u.getSurname(),
                u.getRole().getName(),
                u.getProfileImageUrl(),
                u.getRegisteredAt()
        );
    }
}
