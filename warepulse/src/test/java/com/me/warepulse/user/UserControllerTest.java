package com.me.warepulse.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.security.TestSecurityConfig;
import com.me.warepulse.security.WithMockCustomUser;
import com.me.warepulse.user.dto.SignupRequest;
import com.me.warepulse.user.dto.SignupResponse;
import com.me.warepulse.user.dto.UserListResponse;
import com.me.warepulse.user.dto.UserRoleRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
@DisplayName("사용자: 컨트롤러 통합 테스트")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserService userService;

    private static final String BASE_URL = "/users";

    @Test
    @DisplayName("회원가입: 성공")
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
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.username").value("username"))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }

    @Test
    @DisplayName("회원가입: 실패")
    void signup_fail_duplication_username() throws Exception {
        // given
        SignupRequest request = new SignupRequest("username", "password");

        given(userService.signup(any(SignupRequest.class)))
                .willThrow(new WarePulseException(ErrorCode.DUPLICATE_USERNAME));

        // when & then
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.errorMessage.code").value("U002"))
                .andExpect(jsonPath("$.errorMessage.message").value(ErrorCode.DUPLICATE_USERNAME.getMessage()));
    }

    @Test
    @DisplayName("사용자 리스트 조회: 성공 - ADMIN 권한은 조회 가능")
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void getUsers_success_with_admin() throws Exception {
        // given
        List<UserListResponse> users = List.of(
                new UserListResponse(1L, "user1", "OPERATOR"),
                new UserListResponse(2L, "user2", "ADMIN")
        );

        given(userService.findUsers()).willReturn(users);

        // when & then
        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("사용자 리스트 조회: 실패 - OPERATOR 권한은 조회 불가")
    @WithMockCustomUser
    void getUsers_fail_with_operator() throws Exception {
        // when & then
        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isUnauthorized());
        then(userService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("사용자 권한 변경: 성공 - ADMIN 권한은 변경 가능")
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void modifyUserRole_success_with_admin() throws Exception {
        UserRoleRequest request = new UserRoleRequest(UserRole.ADMIN);

        mockMvc.perform(patch(BASE_URL + "/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @DisplayName("사용자 권한 변경: 실패 - OPERATOR 권한은 변경 불가")
    @WithMockCustomUser
    void modifyUserRole_fail_with_operator() throws Exception {
        UserRoleRequest request = new UserRoleRequest(UserRole.ADMIN);

        mockMvc.perform(patch(BASE_URL + "/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("fail"));
        then(userService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("사용자 삭제: 성공 - ADMIN 권한은 삭제 가능")
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void deleteUser_success_with_admin() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{userId}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @DisplayName("사용자 삭제: 실패 - ADMIN 권한은 삭제 불가")
    @WithMockCustomUser
    void deleteUser_fail_with_operator() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{userId}", 1L))
                .andDo(print())
                .andExpect(status().isUnauthorized());
        then(userService).shouldHaveNoInteractions();
    }
}