package com.me.warepulse.user;

import com.me.warepulse.user.dto.SignupRequest;
import com.me.warepulse.user.dto.SignupResponse;

public interface UserService {
    SignupResponse signup(SignupRequest request);
}
