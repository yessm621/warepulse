package com.me.warepulse.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.inventory.dto.InventoryAvailableResponse;
import com.me.warepulse.inventory.dto.InventoryRequest;
import com.me.warepulse.inventory.dto.InventoryResponse;
import com.me.warepulse.inventory.dto.SkuInventoryResponse;
import com.me.warepulse.inventory.service.InventoryService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
@Import(TestSecurityConfig.class)
class InventoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    InventoryService inventoryService;

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void getInventory_success() throws Exception {
        InventoryResponse response = new InventoryResponse(
                1L, 1L, 1L,
                100,
                0,
                100,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        given(inventoryService.getInventory(any(), any())).willReturn(response);

        mockMvc.perform(get("/inventories/skus/{skuId}/locations/{locationId}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void getInventoriesBySku_success() throws Exception {
        List<InventoryResponse> inventories = List.of(
                new InventoryResponse(1L, 1L, 1L, 100, 0, 100, LocalDateTime.now(), LocalDateTime.now()),
                new InventoryResponse(2L, 2L, 2L, 100, 0, 100, LocalDateTime.now(), LocalDateTime.now())
        );
        SkuInventoryResponse response = new SkuInventoryResponse(1L, 200, inventories);

        given(inventoryService.getInventoriesBySku(any())).willReturn(response);

        mockMvc.perform(get("/inventories/skus/{skuId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void getInventoriesByLocation_success() throws Exception {
        List<InventoryResponse> response = List.of(
                new InventoryResponse(1L, 1L, 1L, 100, 0, 100, LocalDateTime.now(), LocalDateTime.now()),
                new InventoryResponse(2L, 2L, 2L, 100, 0, 100, LocalDateTime.now(), LocalDateTime.now())
        );

        given(inventoryService.getInventoriesByLocation(any())).willReturn(response);

        mockMvc.perform(get("/inventories/locations/{locationId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void getAvailableInventory_success() throws Exception {
        InventoryAvailableResponse response = new InventoryAvailableResponse(100, 0, 100);

        given(inventoryService.inventoryAvailable(any(), any())).willReturn(response);

        mockMvc.perform(get("/skus/{skuId}/locations/{locationId}/availability", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    // todo:: 조회 실패 관련 테스트 코드 작성
    // todo:: 생성 관련 성공/실패 테스트 코드 작성
    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void createInventory_success() throws Exception {
        InventoryRequest request = new InventoryRequest(1L, 1L, 100);
        InventoryResponse response = new InventoryResponse(1L, 1L, 1L, 100, 10, 90, LocalDateTime.now(), LocalDateTime.now());

        given(inventoryService.createInventory(any(InventoryRequest.class))).willReturn(response);

        mockMvc.perform(post("/inventories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void createInventory_fail_exist_sku_location() throws Exception {
        InventoryRequest request = new InventoryRequest(1L, 1L, 100);

        given(inventoryService.createInventory(any(InventoryRequest.class)))
                .willThrow(new WarePulseException(ErrorCode.DUPLICATE_INVENTORY));

        mockMvc.perform(post("/inventories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage.code").value("I007"))
                .andExpect(jsonPath("$.errorMessage.message").value(ErrorCode.DUPLICATE_INVENTORY.getMessage()));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void createInventory_fail_sku_not_found() throws Exception {
        InventoryRequest request = new InventoryRequest(100L, 1L, 100);

        given(inventoryService.createInventory(any(InventoryRequest.class)))
                .willThrow(new WarePulseException(ErrorCode.SKU_NOT_FOUND));

        mockMvc.perform(post("/inventories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage.code").value("S001"))
                .andExpect(jsonPath("$.errorMessage.message").value(ErrorCode.SKU_NOT_FOUND.getMessage()));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void createInventory_fail_location_not_found() throws Exception {
        InventoryRequest request = new InventoryRequest(1L, 100L, 100);

        given(inventoryService.createInventory(any(InventoryRequest.class)))
                .willThrow(new WarePulseException(ErrorCode.LOCATION_NOT_FOUND));

        mockMvc.perform(post("/inventories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage.code").value("L001"))
                .andExpect(jsonPath("$.errorMessage.message").value(ErrorCode.LOCATION_NOT_FOUND.getMessage()));
    }

    @Test
    @WithMockCustomUser(username = "username1", roles = "OPERATOR")
    void createInventory_fail_with_operation() throws Exception {
        InventoryRequest request = new InventoryRequest(1L, 1L, 100);

        mockMvc.perform(post("/inventories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
        then(inventoryService).shouldHaveNoInteractions();
    }
}