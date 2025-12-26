package com.me.warepulse.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.security.TestSecurityConfig;
import com.me.warepulse.security.WithMockCustomUser;
import com.me.warepulse.user.dto.SignupRequest;
import com.me.warepulse.user.dto.SignupResponse;
import com.me.warepulse.user.dto.UserListResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void signup_success() throws Exception {
        // given
        SignupRequest request = new SignupRequest("username", "password");
        SignupResponse response = new SignupResponse(1L, "username");

        given(userService.signup(any(SignupRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.username").value("username"))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }

    @Test
    void signup_fail_duplication_username() throws Exception {
        // given
        SignupRequest request = new SignupRequest("username", "password");

        given(userService.signup(any(SignupRequest.class)))
                .willThrow(new WarePulseException(ErrorCode.DUPLICATE_USER_NAME));

        // when & then
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.errorMessage.code").value("U002"))
                .andExpect(jsonPath("$.errorMessage.message").value(ErrorCode.DUPLICATE_USER_NAME.getMessage()));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void getUsers_success_with_admin() throws Exception {
        // given
        List<UserListResponse> users = List.of(
                new UserListResponse(1L, "user1", "OPERATOR"),
                new UserListResponse(2L, "user2", "ADMIN")
        );

        given(userService.findUsers(any())).willReturn(users);

        // when & then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @WithMockCustomUser(username = "username1", roles = "OPERATOR")
    void getUsers_fail_with_operator() throws Exception {
        // when & then
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());
        then(userService).shouldHaveNoInteractions();
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void deleteUser_success_with_admin() throws Exception {
        mockMvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @WithMockCustomUser(username = "username1", roles = "OPERATOR")
    void deleteUser_fail_with_operator() throws Exception {
        mockMvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().isUnauthorized());
        then(userService).shouldHaveNoInteractions();
    }
}