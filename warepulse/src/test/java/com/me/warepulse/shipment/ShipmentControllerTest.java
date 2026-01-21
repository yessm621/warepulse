package com.me.warepulse.shipment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.warepulse.security.WithMockCustomUser;
import com.me.warepulse.shipment.dto.ShipmentPickingRequest;
import com.me.warepulse.shipment.dto.ShipmentRequest;
import com.me.warepulse.shipment.dto.ShipmentResponse;
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

@WebMvcTest(ShipmentController.class)
@DisplayName("출고: 컨트롤러 통합 테스트")
class ShipmentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ShipmentService shipmentService;

    private static final String BASE_URL = "/shipments";

    @Test
    @DisplayName("출고 리스트 조회: 성공")
    @WithMockCustomUser
    void findShipments() throws Exception {
        List<ShipmentResponse> shipments = List.of(
                new ShipmentResponse(1L, 1L, 1L, 10, 10, ShipmentStatus.CREATED.name(), null, "createBy", null, null, LocalDateTime.now(), LocalDateTime.now()),
                new ShipmentResponse(2L, 1L, 1L, 20, 20, ShipmentStatus.CREATED.name(), null, "createBy", null, null, LocalDateTime.now(), LocalDateTime.now())
        );

        given(shipmentService.findShipments()).willReturn(shipments);

        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @DisplayName("출고 단건 조회: 성공")
    @WithMockCustomUser
    void findShipment() throws Exception {
        Long shipmentId = 1L;
        ShipmentResponse shipment = new ShipmentResponse(1L, 1L, 1L, 10, 10, ShipmentStatus.CREATED.name(), null, "createBy", null, null, LocalDateTime.now(), LocalDateTime.now());

        given(shipmentService.findShipment(shipmentId)).willReturn(shipment);

        mockMvc.perform(get(BASE_URL + "/{shipmentId}", shipmentId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @DisplayName("출고 생성: 성공")
    @WithMockCustomUser
    void createShipment() throws Exception {
        ShipmentRequest request = new ShipmentRequest(1L, 1L, 10);
        ShipmentResponse response = new ShipmentResponse(1L, 1L, 1L, 10, 10, ShipmentStatus.CREATED.name(), null, "createBy", null, null, LocalDateTime.now(), LocalDateTime.now());

        given(shipmentService.createShipment(any(ShipmentRequest.class))).willReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.shipmentStatus").value("CREATED"))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }

    @Test
    @DisplayName("출고 픽킹: 성공")
    @WithMockCustomUser
    void pickingShipment() throws Exception {
        Long shipmentId = 1L;
        ShipmentPickingRequest request = new ShipmentPickingRequest(10);
        ShipmentResponse response = new ShipmentResponse(shipmentId, 1L, 1L, 10, 10, ShipmentStatus.PICKING.name(), null, "createBy", "pickedBy", null, LocalDateTime.now(), LocalDateTime.now());

        given(shipmentService.pickingShipment(anyLong(), anyInt(), anyString())).willReturn(response);

        mockMvc.perform(patch(BASE_URL + "/{shipmentId}/picking", shipmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.shipmentStatus").value("PICKING"))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }

    @Test
    @DisplayName("출고 Shipped: 성공")
    @WithMockCustomUser
    void shippedShipment() throws Exception {
        Long shipmentId = 1L;
        ShipmentResponse response = new ShipmentResponse(shipmentId, 1L, 1L, 10, 10, ShipmentStatus.SHIPPED.name(), 1L, "createBy", "pickedBy", "shippedBy", LocalDateTime.now(), LocalDateTime.now());

        given(shipmentService.shippedShipment(anyLong(), anyString())).willReturn(response);

        mockMvc.perform(patch(BASE_URL + "/{shipmentId}/shipped", shipmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.shipmentStatus").value("SHIPPED"))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }

    @Test
    @DisplayName("출고 취소: 성공")
    @WithMockCustomUser
    void canceledShipment() throws Exception {
        Long shipmentId = 1L;
        ShipmentResponse response = new ShipmentResponse(shipmentId, 1L, 1L, 10, 10, ShipmentStatus.CANCELED.name(), null, "createBy", null, null, LocalDateTime.now(), LocalDateTime.now());

        given(shipmentService.canceledShipment(shipmentId)).willReturn(response);

        mockMvc.perform(patch(BASE_URL + "/{shipmentId}/canceled", shipmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.shipmentStatus").value("CANCELED"))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }
}