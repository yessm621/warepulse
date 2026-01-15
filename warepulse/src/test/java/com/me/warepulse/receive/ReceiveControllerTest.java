package com.me.warepulse.receive;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.warepulse.receive.dto.ReceiveInspectedRequest;
import com.me.warepulse.receive.dto.ReceiveRequest;
import com.me.warepulse.receive.dto.ReceiveResponse;
import com.me.warepulse.security.WithMockCustomUser;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReceiveController.class)
class ReceiveControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ReceiveService receiveService;

    @Test
    @WithMockCustomUser
    void findReceives_success() throws Exception {
        List<ReceiveResponse> receives = List.of(
                new ReceiveResponse(1L, 1L, 1L, 10, 0, ReceiveStatus.CREATED.name(), null, "createdBy", "", "", LocalDateTime.now(), LocalDateTime.now()),
                new ReceiveResponse(2L, 1L, 1L, 10, 0, ReceiveStatus.CREATED.name(), null, "createdBy", "", "", LocalDateTime.now(), LocalDateTime.now())
        );

        given(receiveService.findReceives()).willReturn(receives);

        mockMvc.perform(get("/receives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @WithMockCustomUser
    void findReceive_success() throws Exception {
        ReceiveResponse response = new ReceiveResponse(1L, 1L, 1L, 10, 0, ReceiveStatus.CREATED.name(), null, "createdBy", "", "", LocalDateTime.now(), LocalDateTime.now());

        given(receiveService.findReceive(any())).willReturn(response);

        mockMvc.perform(get("/receives/{receiveId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @WithMockCustomUser
    void createReceive() throws Exception {
        ReceiveRequest request = new ReceiveRequest(1L, 1L, 10);
        ReceiveResponse response = new ReceiveResponse(1L, 1L, 1L, 10, 0, ReceiveStatus.CREATED.name(), null, "createdBy", "", "", LocalDateTime.now(), LocalDateTime.now());

        given(receiveService.createReceive(any(ReceiveRequest.class))).willReturn(response);

        mockMvc.perform(post("/receives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.receiveStatus").value("CREATED"))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }

    @Test
    @WithMockCustomUser
    void inspectedReceive() throws Exception {
        ReceiveInspectedRequest request = new ReceiveInspectedRequest(10);
        ReceiveResponse response = new ReceiveResponse(1L, 1L, 1L, 10, 0, ReceiveStatus.INSPECTED.name(), null, "createdBy", "inspectedBy", "", LocalDateTime.now(), LocalDateTime.now());

        given(receiveService.inspectedReceive(anyLong(), anyString(), anyInt())).willReturn(response);

        mockMvc.perform(patch("/receives/{receiveId}/inspected", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.receiveStatus").value("INSPECTED"))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }

    @Test
    @WithMockCustomUser
    void completedReceive() throws Exception {
        ReceiveResponse response = new ReceiveResponse(1L, 1L, 1L, 10, 0, ReceiveStatus.COMPLETED.name(), 1L, "createdBy", "inspectedBy", "completedBy", LocalDateTime.now(), LocalDateTime.now());

        given(receiveService.completedReceive(any(), any())).willReturn(response);

        mockMvc.perform(patch("/receives/{receiveId}/completed", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.receiveStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }

    @Test
    @WithMockCustomUser
    void canceledReceive() throws Exception {
        ReceiveResponse response = new ReceiveResponse(1L, 1L, 1L, 10, 0, ReceiveStatus.INSPECTED.name(), 1L, "createdBy", "inspectedBy", "completedBy", LocalDateTime.now(), LocalDateTime.now());

        given(receiveService.completedReceive(any(), any())).willReturn(response);

        mockMvc.perform(patch("/receives/{receiveId}/canceled", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }
}