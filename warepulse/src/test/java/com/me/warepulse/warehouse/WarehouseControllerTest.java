package com.me.warepulse.warehouse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.location.LocationService;
import com.me.warepulse.location.dto.LocationResponse;
import com.me.warepulse.security.TestSecurityConfig;
import com.me.warepulse.security.WithMockCustomUser;
import com.me.warepulse.warehouse.dto.WarehouseRequest;
import com.me.warepulse.warehouse.dto.WarehouseResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WarehouseController.class)
@Import(TestSecurityConfig.class)
class WarehouseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private WarehouseService warehouseService;

    @MockitoBean
    private LocationService locationService;

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void createWarehouse_success() throws Exception {
        WarehouseRequest request = new WarehouseRequest("고양-A", "고양시");
        WarehouseResponse response = new WarehouseResponse(1L, "고양-A", "고양시", LocalDateTime.now());

        given(warehouseService.create(any(WarehouseRequest.class))).willReturn(response);

        mockMvc.perform(post("/warehouses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.name").value("고양-A"))
                .andExpect(jsonPath("$.data.address").value("고양시"))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }

    @Test
    @WithMockCustomUser(username = "username1", roles = "OPERATOR")
    void createWarehouse_fail_with_operator() throws Exception {
        WarehouseRequest request = new WarehouseRequest("고양-A", "고양시");
        WarehouseResponse response = new WarehouseResponse(1L, "고양-A", "고양시", LocalDateTime.now());

        given(warehouseService.create(any(WarehouseRequest.class))).willReturn(response);

        mockMvc.perform(post("/warehouses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.errorMessage.code").value("E002"));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void getWarehouseList_success() throws Exception {
        List<WarehouseResponse> warehouses = List.of(
                new WarehouseResponse(1L, "고양-A", "고양시", LocalDateTime.now()),
                new WarehouseResponse(2L, "고양-B", "고양시", LocalDateTime.now())
        );

        given(warehouseService.findWarehouses()).willReturn(warehouses);

        mockMvc.perform(get("/warehouses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void getWarehouseById_success() throws Exception {
        WarehouseResponse response = new WarehouseResponse(1L, "고양-A", "고양시", LocalDateTime.now());

        given(warehouseService.findWarehouse(any())).willReturn(response);

        mockMvc.perform(get("/warehouses/{warehouseId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void getWarehouseById_fail_does_not_exist_warehouseId() throws Exception {
        given(warehouseService.findWarehouse(any()))
                .willThrow(new WarePulseException(ErrorCode.WAREHOUSE_NOT_FOUND));

        mockMvc.perform(get("/warehouses/{warehouseId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void deleteWarehouse_success() throws Exception {
        mockMvc.perform(delete("/warehouses/{warehouseId}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @WithMockCustomUser(username = "username1", roles = "OPERATOR")
    void deleteWarehouse_fail_with_operator() throws Exception {
        mockMvc.perform(delete("/warehouses/{warehouseId}", 1L)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.errorMessage.code").value("E002"));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void deleteWarehouse_fail_does_not_exist_warehouseId() throws Exception {
        doThrow(new WarePulseException(ErrorCode.WAREHOUSE_NOT_FOUND))
                .when(warehouseService)
                .deleteWarehouse(any());

        mockMvc.perform(delete("/warehouses/{warehouseId}", 1L)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void findLocationByWarehouseId_success() throws Exception {
        List<LocationResponse> responses = List.of(
                new LocationResponse(1L, "고양-A", 1L, "A-01-01", 100, LocalDateTime.now()),
                new LocationResponse(1L, "고양-A", 2L, "A-01-02", 100, LocalDateTime.now())
        );

        given(locationService.findLocationByWarehouseId(any())).willReturn(responses);

        mockMvc.perform(get("/warehouses/{warehouseId}/locations", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void findLocationByWarehouseId_fail_does_not_exist_warehouseId() throws Exception {
        given(locationService.findLocationByWarehouseId(any()))
                .willThrow(new WarePulseException(ErrorCode.WAREHOUSE_NOT_FOUND));

        mockMvc.perform(get("/warehouses/{warehouseId}/locations", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"));
    }
}