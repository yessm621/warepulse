package com.me.warepulse.user;

import com.me.warepulse.user.dto.SignupRequest;
import com.me.warepulse.user.dto.SignupResponse;
import com.me.warepulse.user.dto.UserListResponse;

import java.util.List;

public interface UserService {

    SignupResponse signup(SignupRequest request);

    List<UserListResponse> findUsers(String username);
}
