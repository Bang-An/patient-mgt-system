package com.pm.authservice.service.password;

import com.pm.authservice.exception.UserNotFoundException;
import com.pm.authservice.model.User;
import com.pm.authservice.service.AuthService;
import com.pm.authservice.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PasswordValidationContextFactory {
    private final UserService userService;

    public PasswordValidationContextFactory(UserService userService) {
        this.userService = userService;
    }

    public PasswordValidationContext fromUserID(UUID userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id : " + userId + "not found"));
        return new PasswordValidationContext(user.getPassword(), user.getName(), user.getDateOfBirth());
    }
}
