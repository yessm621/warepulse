package com.me.warepulse.user;

import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.user.dto.SignupRequest;
import com.me.warepulse.user.dto.SignupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new WarePulseException(ErrorCode.USER_NOT_FOUND));

        return new CustomUserDetails(user);
    }

    @Transactional
    @Override
    public SignupResponse signup(SignupRequest request) {
        User user = User.createUser(request.getUsername(), bCryptPasswordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return SignupResponse.from(user);
    }
}
