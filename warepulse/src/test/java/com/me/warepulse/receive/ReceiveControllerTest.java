package com.me.warepulse.receive;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.warepulse.receive.dto.ReceiveInspectedRequest;
import com.me.warepulse.receive.dto.ReceiveRequest;
import com.me.warepulse.receive.dto.ReceiveResponse;
import com.me.warepulse.security.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReceiveController.class)
@DisplayName("입고: 컨트롤러 통합 테스트")
class ReceiveControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ReceiveService receiveService;

    private static final String BASE_URL = "/receives";

    @Test
    @DisplayName("입고 리스트 조회: 성공")
    @WithMockCustomUser
    void findReceives_success() throws Exception {
        List<ReceiveResponse> receives = List.of(
                new ReceiveResponse(1L, 1L, 1L, 10, 0, ReceiveStatus.CREATED.name(), null, "createdBy", "", "", LocalDateTime.now(), LocalDateTime.now()),
                new ReceiveResponse(2L, 1L, 1L, 10, 0, ReceiveStatus.CREATED.name(), null, "createdBy", "", "", LocalDateTime.now(), LocalDateTime.now())
        );

        given(receiveService.findReceives()).willReturn(receives);

        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @DisplayName("입고 단건 조회: 성공")
    @WithMockCustomUser
    void findReceive_success() throws Exception {
        Long receiveId = 1L;
        ReceiveResponse response = new ReceiveResponse(receiveId, 1L, 1L, 10, 0, ReceiveStatus.CREATED.name(), null, "createdBy", "", "", LocalDateTime.now(), LocalDateTime.now());

        given(receiveService.findReceive(receiveId)).willReturn(response);

        mockMvc.perform(get(BASE_URL + "/{receiveId}", receiveId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @DisplayName("입고 생성: 성공")
    @WithMockCustomUser
    void createReceive() throws Exception {
        ReceiveRequest request = new ReceiveRequest(1L, 1L, 10);
        ReceiveResponse response = new ReceiveResponse(1L, 1L, 1L, 10, 0, ReceiveStatus.CREATED.name(), null, "createdBy", "", "", LocalDateTime.now(), LocalDateTime.now());

        given(receiveService.createReceive(any(ReceiveRequest.class))).willReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.receiveStatus").value("CREATED"))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }

    @Test
    @DisplayName("입고 검수: 성공")
    @WithMockCustomUser
    void inspectedReceive() throws Exception {
        Long receiveId = 1L;
        ReceiveInspectedRequest request = new ReceiveInspectedRequest(10);
        ReceiveResponse response = new ReceiveResponse(receiveId, 1L, 1L, 10, 0, ReceiveStatus.INSPECTED.name(), null, "createdBy", "inspectedBy", "", LocalDateTime.now(), LocalDateTime.now());

        given(receiveService.inspectedReceive(anyLong(), anyString(), anyInt())).willReturn(response);

        mockMvc.perform(patch(BASE_URL + "/{receiveId}/inspected", receiveId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.receiveStatus").value("INSPECTED"))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }

    @Test
    @DisplayName("입고 완료: 성공")
    @WithMockCustomUser
    void completedReceive() throws Exception {
        Long receiveId = 1L;
        ReceiveResponse response = new ReceiveResponse(receiveId, 1L, 1L, 10, 0, ReceiveStatus.COMPLETED.name(), 1L, "createdBy", "inspectedBy", "completedBy", LocalDateTime.now(), LocalDateTime.now());

        given(receiveService.completedReceive(anyLong(), anyString())).willReturn(response);

        mockMvc.perform(patch(BASE_URL + "/{receiveId}/completed", receiveId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.receiveStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }

    @Test
    @DisplayName("입고 취소: 성공")
    @WithMockCustomUser
    void canceledReceive() throws Exception {
        Long receiveId = 1L;
        ReceiveResponse response = new ReceiveResponse(receiveId, 1L, 1L, 10, 0, ReceiveStatus.INSPECTED.name(), 1L, "createdBy", "inspectedBy", "completedBy", LocalDateTime.now(), LocalDateTime.now());

        given(receiveService.completedReceive(anyLong(), anyString())).willReturn(response);

        mockMvc.perform(patch(BASE_URL + "/{receiveId}/canceled", receiveId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }
}