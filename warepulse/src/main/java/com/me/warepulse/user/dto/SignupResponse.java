package com.me.warepulse.user.dto;

import com.me.warepulse.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupResponse {

    private Long userId;
    private String username;

    public static SignupResponse from(User user) {
        return new SignupResponse(user.getId(), user.getUsername());
    }
}
