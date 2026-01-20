package com.me.warepulse.adjustment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.warepulse.adjustment.dto.AdjustmentRequest;
import com.me.warepulse.adjustment.dto.AdjustmentResponse;
import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.security.TestSecurityConfig;
import com.me.warepulse.security.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdjustmentController.class)
@Import(TestSecurityConfig.class)
@DisplayName("재고 조정: 컨트롤러 통합 테스트")
class AdjustmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AdjustmentService adjustmentService;

    private static final String BASE_URL = "/adjustments";

    @Test
    @DisplayName("재고 조정 리스트 조회: 성공")
    @WithMockCustomUser
    void findAdjustments() throws Exception {
        List<AdjustmentResponse> adjustments = List.of(
                new AdjustmentResponse(1L, 1L, 1L, "hello", -1, AdjustmentReason.MANUAL_CORRECTION.name(), 1L, LocalDateTime.now(), LocalDateTime.now()),
                new AdjustmentResponse(2L, 1L, 1L, "hello", 1, AdjustmentReason.MANUAL_CORRECTION.name(), 1L, LocalDateTime.now(), LocalDateTime.now())
        );

        given(adjustmentService.findAdjustments()).willReturn(adjustments);

        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("재고 조정 단건 조회: 성공")
    @WithMockCustomUser
    void findAdjustment_success() throws Exception {
        Long adjustmentId = 1L;
        AdjustmentResponse adjustment = new AdjustmentResponse(adjustmentId, 1L, 1L, "hello", -1, AdjustmentReason.MANUAL_CORRECTION.name(), 1L, LocalDateTime.now(), LocalDateTime.now());

        given(adjustmentService.findAdjustment(anyLong())).willReturn(adjustment);

        mockMvc.perform(get(BASE_URL + "/{adjustmentId}", adjustmentId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.adjustmentId").value(adjustmentId));
    }

    @Test
    @DisplayName("재고 조정 단건 조회: 실패 - adjustmentId를 찾을 수 없음")
    @WithMockCustomUser
    void findAdjustment_fail_not_found_adjustmentId() throws Exception {
        Long invalidId = 999L;
        given(adjustmentService.findAdjustment(invalidId))
                .willThrow(new WarePulseException(ErrorCode.ADJUSTMENT_NOT_FOUND));

        mockMvc.perform(get(BASE_URL + "/{adjustmentId}", invalidId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.errorMessage.code").value("A001"));
    }

    @Test
    @DisplayName("재고 조정 생성: 성공 - ADMIN 권한은 생성 가능")
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void createAdjustment_success() throws Exception {
        AdjustmentRequest request = new AdjustmentRequest(1L, 1L, 1L, -1, AdjustmentReason.MANUAL_CORRECTION);
        AdjustmentResponse response = new AdjustmentResponse(1L, 1L, 1L, "hello", -1, AdjustmentReason.MANUAL_CORRECTION.name(), 1L, LocalDateTime.now(), LocalDateTime.now());

        given(adjustmentService.create(any(AdjustmentRequest.class))).willReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.delta").value(-1))
                .andExpect(jsonPath("$.data.reason").value(AdjustmentReason.MANUAL_CORRECTION.name()))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }

    @Test
    @DisplayName("재고 조정 생성: 실패 - OPERATOR 권한은 생성 불가")
    @WithMockCustomUser
    void createAdjustment_fail_with_operator() throws Exception {
        AdjustmentRequest request = new AdjustmentRequest(1L, 1L, 1L, -1, AdjustmentReason.MANUAL_CORRECTION);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.errorMessage.code").value("E002"));
    }
}