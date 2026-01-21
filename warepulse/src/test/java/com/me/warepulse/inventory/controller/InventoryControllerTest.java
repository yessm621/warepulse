package com.me.warepulse.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.warepulse.inventory.controller.dto.InventoryAvailableResponse;
import com.me.warepulse.inventory.controller.dto.InventoryResponse;
import com.me.warepulse.inventory.controller.dto.SkuInventoryResponse;
import com.me.warepulse.inventory.service.InventoryService;
import com.me.warepulse.security.TestSecurityConfig;
import com.me.warepulse.security.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
@Import(TestSecurityConfig.class)
@DisplayName("Inventory: 컨트롤러 통합 테스트")
class InventoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    InventoryService inventoryService;

    private static final String BASE_URL = "/inventories";

    @Test
    @DisplayName("Inventory 단건 조회: 성공")
    @WithMockCustomUser
    void getInventory_success() throws Exception {
        Long skuId = 1L;
        Long locationId = 1L;
        InventoryResponse response = new InventoryResponse(
                1L, skuId, locationId,
                100,
                0,
                100,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        given(inventoryService.getInventory(skuId, locationId)).willReturn(response);

        mockMvc.perform(get(BASE_URL + "/skus/{skuId}/locations/{locationId}", skuId, locationId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.skuId").value(skuId))
                .andExpect(jsonPath("$.data.locationId").value(locationId));
    }

    @Test
    @DisplayName("Inventory 리스트 조회: 성공 - skuId로 Inventory 리스트 조회")
    @WithMockCustomUser
    void getInventoriesBySku_success() throws Exception {
        Long skuId = 1L;
        List<InventoryResponse> inventories = List.of(
                new InventoryResponse(1L, skuId, 1L, 100, 0, 100, LocalDateTime.now(), LocalDateTime.now()),
                new InventoryResponse(2L, skuId, 2L, 100, 0, 100, LocalDateTime.now(), LocalDateTime.now())
        );
        SkuInventoryResponse response = new SkuInventoryResponse(1L, 200, inventories);

        given(inventoryService.getInventoriesBySku(skuId)).willReturn(response);

        mockMvc.perform(get(BASE_URL + "/skus/{skuId}", skuId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.locations.length()").value(2));
    }

    @Test
    @DisplayName("Inventory 리스트 조회: 성공 - locationId로 Inventory 리스트 조회")
    @WithMockCustomUser
    void getInventoriesByLocation_success() throws Exception {
        Long locationId = 1L;
        List<InventoryResponse> response = List.of(
                new InventoryResponse(1L, 1L, locationId, 100, 0, 100, LocalDateTime.now(), LocalDateTime.now()),
                new InventoryResponse(2L, 2L, locationId, 100, 0, 100, LocalDateTime.now(), LocalDateTime.now())
        );

        given(inventoryService.getInventoriesByLocation(locationId)).willReturn(response);

        mockMvc.perform(get(BASE_URL + "/locations/{locationId}", locationId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @DisplayName("Inventory 가용 재고 조회: 성공")
    @WithMockCustomUser
    void getAvailableInventory_success() throws Exception {
        Long skuId = 1L;
        Long locationId = 1L;
        InventoryAvailableResponse response = new InventoryAvailableResponse(100, 0, 100);

        given(inventoryService.inventoryAvailable(skuId, locationId)).willReturn(response);

        mockMvc.perform(get("/skus/{skuId}/locations/{locationId}/availability", skuId, locationId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }
}