package com.me.warepulse.utils;

import com.me.warepulse.user.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserAuditorAware implements AuditorAware<Long> {

    private final HttpSession httpSession;

    @Override
    public Optional<Long> getCurrentAuditor() {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return null;
        }
        return Optional.ofNullable(user.getId());
    }
}
