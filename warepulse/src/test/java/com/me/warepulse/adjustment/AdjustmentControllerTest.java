package com.me.warepulse.adjustment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.warepulse.adjustment.dto.AdjustmentRequest;
import com.me.warepulse.adjustment.dto.AdjustmentResponse;
import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.security.TestSecurityConfig;
import com.me.warepulse.security.WithMockCustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdjustmentController.class)
@Import(TestSecurityConfig.class)
class AdjustmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private AdjustmentService adjustmentService;

    @Test
    @WithMockCustomUser
    void findAdjustments() throws Exception {
        List<AdjustmentResponse> adjustments = List.of(
                new AdjustmentResponse(1L, 1L, 1L, "hello", -1, AdjustmentReason.MANUAL_CORRECTION.name(), 1L, LocalDateTime.now(), LocalDateTime.now()),
                new AdjustmentResponse(2L, 1L, 1L, "hello", 1, AdjustmentReason.MANUAL_CORRECTION.name(), 1L, LocalDateTime.now(), LocalDateTime.now())
        );

        given(adjustmentService.findAdjustments()).willReturn(adjustments);

        mockMvc.perform(get("/adjustments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @WithMockCustomUser
    void findAdjustment_success() throws Exception {
        AdjustmentResponse adjustment = new AdjustmentResponse(1L, 1L, 1L, "hello", -1, AdjustmentReason.MANUAL_CORRECTION.name(), 1L, LocalDateTime.now(), LocalDateTime.now());

        given(adjustmentService.findAdjustment(any())).willReturn(adjustment);

        mockMvc.perform(get("/adjustments/{adjustmentId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @WithMockCustomUser
    void findAdjustment_fail_not_found_adjustmentId() throws Exception {
        given(adjustmentService.findAdjustment(any()))
                .willThrow(new WarePulseException(ErrorCode.ADJUSTMENT_NOT_FOUND));

        mockMvc.perform(get("/adjustments/{adjustmentId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void createAdjustment_success() throws Exception {
        AdjustmentRequest request = new AdjustmentRequest(1L, 1L, 1L, -1, AdjustmentReason.MANUAL_CORRECTION);
        AdjustmentResponse response = new AdjustmentResponse(1L, 1L, 1L, "hello", -1, AdjustmentReason.MANUAL_CORRECTION.name(), 1L, LocalDateTime.now(), LocalDateTime.now());

        given(adjustmentService.create(request)).willReturn(response);

        mockMvc.perform(post("/adjustments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }

    @Test
    @WithMockCustomUser(username = "username1", roles = "OPERATOR")
    void createAdjustment_fail_with_operator() throws Exception {
        AdjustmentRequest request = new AdjustmentRequest(1L, 1L, 1L, -1, AdjustmentReason.MANUAL_CORRECTION);

        mockMvc.perform(post("/adjustments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("fail"));
    }
}