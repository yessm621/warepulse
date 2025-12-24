package com.me.warepulse.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.warepulse.exception.ApiResponse;
import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.ErrorMessage;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.user.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtToken jwtToken;
    private final ObjectMapper objectMapper;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        LoginRequest loginRequest = null;
        try {
            loginRequest = objectMapper.readValue(
                    StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8),
                    LoginRequest.class
            );
        } catch (IOException e) {
            throw new WarePulseException(ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        return authenticationManager.authenticate(authToken);
    }

    /* 로그인 성공시 실행 - JWT 발급 */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String token = jwtToken.createJwt(username, role, 60 * 60 * 10L);
        response.addHeader("Authorization", "Bearer " + token);
    }

    /* 로그인 실패 */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(401);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        ErrorMessage errorMessage = ErrorMessage.create(ErrorCode.ACCESS_DENIED_EXCEPTION.getCode(), "로그인에 실패하였습니다.");
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail(errorMessage)));
    }

    @Data
    private static class LoginRequest {
        private String username;
        private String password;
    }
}
