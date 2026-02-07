package com.me.warepulse.user;

import com.me.warepulse.user.dto.*;

import java.util.List;

public interface UserService {

    SignupResponse signup(SignupRequest request);

    List<UserListResponse> findUsers();

    UserResponse findUser(Long userId);

    void modifyUserRole(Long userId, UserRoleRequest request);

    void deleteUser(Long userId);
}
