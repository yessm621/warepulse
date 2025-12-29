package com.me.warepulse.user.dto;

import com.me.warepulse.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserListResponse {

    private Long userId;
    private String username;
    private String userRole;

    public static UserListResponse from(User user) {
        return new UserListResponse(user.getId(), user.getUsername(), user.getRole().getName());
    }
}
