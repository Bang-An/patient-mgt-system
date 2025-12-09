package com.pm.authservice.service.password.rules;

import com.pm.authservice.service.AuthService;
import com.pm.authservice.service.password.PasswordValidationContext;
import com.pm.authservice.service.password.PasswordViolation;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Component
public class PersonalInfoAndReuseRule implements PasswordRule{
    private final AuthService authService;

    public PersonalInfoAndReuseRule(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public List<PasswordViolation> validate(String password, PasswordValidationContext context) {
        List<PasswordViolation> violations = new ArrayList<>();

        // 1) newPassword is different from current
        if (authService.matchesPassword(password, context.getCurrentPassword())) {
            violations.add(PasswordViolation.SAME_AS_CURRENT_PASSWORD);
        }

        // 2) Does not contain name
        String lowerPassword = password.toLowerCase();
        String name = context.getName();
        if (name != null && !name.isBlank()) {
            String normalizedName = name.replaceAll("\\s+", "").toLowerCase();
            String normalizedPassword = lowerPassword.replaceAll("\\s+", "");
            if (!normalizedName.isBlank() && normalizedPassword.contains(normalizedName)) {
                violations.add(PasswordViolation.CONTAINS_NAME);
            }
        }

        // 3) Does not contain date of birth (year or common formats)
        LocalDate dob = context.getDateOfBirth();
        if (dob != null) {
            String year = String.valueOf(dob.getYear());
            String mm = String.format("%02d", dob.getMonthValue());
            String dd = String.format("%02d", dob.getDayOfMonth());

            String iso = dob.toString();           // yyyy-MM-dd
            String yyyymmdd = year + mm + dd;      // yyyyMMdd
            String ddmmyyyy = dd + mm + year;      // ddMMyyyy

            if (lowerPassword.contains(year)
                    || lowerPassword.contains(iso.toLowerCase())
                    || lowerPassword.contains(yyyymmdd)
                    || lowerPassword.contains(ddmmyyyy)) {
                violations.add(PasswordViolation.CONTAINS_DATE_OF_BIRTH);
            }
        }
        return violations;
    }
}
