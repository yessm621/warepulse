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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WarehouseController.class)
@Import(TestSecurityConfig.class)
@DisplayName("창고: 컨트롤러 통합 테스트")
class WarehouseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    WarehouseService warehouseService;

    @MockitoBean
    LocationService locationService;

    private static final String BASE_URL = "/warehouses";

    @Test
    @DisplayName("창고 생성: 성공 - ADMIN 권한은 생성 가능")
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void createWarehouse_success() throws Exception {
        WarehouseRequest request = new WarehouseRequest("고양-A", "고양시");
        WarehouseResponse response = new WarehouseResponse(1L, "고양-A", "고양시", LocalDateTime.now());

        given(warehouseService.create(any(WarehouseRequest.class))).willReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.name").value("고양-A"))
                .andExpect(jsonPath("$.data.address").value("고양시"))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }

    @Test
    @DisplayName("창고 생성: 실패 - OPERATOR 권한은 생성 불가")
    @WithMockCustomUser
    void createWarehouse_fail_with_operator() throws Exception {
        WarehouseRequest request = new WarehouseRequest("고양-A", "고양시");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.errorMessage.code").value("E002"));
    }

    @Test
    @DisplayName("창고 리스트 조회: 성공")
    @WithMockCustomUser
    void getWarehouseList_success() throws Exception {
        List<WarehouseResponse> warehouses = List.of(
                new WarehouseResponse(1L, "고양-A", "고양시", LocalDateTime.now()),
                new WarehouseResponse(2L, "고양-B", "고양시", LocalDateTime.now())
        );

        given(warehouseService.findWarehouses()).willReturn(warehouses);

        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("창고 단건 조회: 성공")
    @WithMockCustomUser
    void getWarehouseById_success() throws Exception {
        Long warehouseId = 1L;
        WarehouseResponse response = new WarehouseResponse(warehouseId, "고양-A", "고양시", LocalDateTime.now());

        given(warehouseService.findWarehouse(anyLong())).willReturn(response);

        mockMvc.perform(get(BASE_URL + "/{warehouseId}", warehouseId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.warehouseId").value(warehouseId));
    }

    @Test
    @DisplayName("창고 단건 조회: 실패 - warehouseId를 찾을 수 없음")
    @WithMockCustomUser
    void getWarehouseById_fail_not_found_warehouseId() throws Exception {
        Long invalidId = 999L;
        given(warehouseService.findWarehouse(invalidId))
                .willThrow(new WarePulseException(ErrorCode.WAREHOUSE_NOT_FOUND));

        mockMvc.perform(get(BASE_URL + "/{warehouseId}", invalidId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    @DisplayName("창고 삭제: 성공 - ADMIN 권한은 삭제 가능")
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void deleteWarehouse_success() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{warehouseId}", 1L)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @DisplayName("창고 삭제: 실패 - OPERATOR 권한은 삭제 불가")
    @WithMockCustomUser
    void deleteWarehouse_fail_with_operator() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{warehouseId}", 1L)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.errorMessage.code").value("E002"));
    }

    @Test
    @DisplayName("창고 삭제: 실패 - warehouseId를 찾을 수 없음")
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void deleteWarehouse_fail_not_found_warehouseId() throws Exception {
        Long invalidId = 999L;
        doThrow(new WarePulseException(ErrorCode.WAREHOUSE_NOT_FOUND))
                .when(warehouseService)
                .deleteWarehouse(invalidId);

        mockMvc.perform(delete(BASE_URL + "/{warehouseId}", invalidId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.errorMessage.code").value("W001"));
    }

    @Test
    @DisplayName("특정 창고의 Location 조회: 성공 - warehouseId로 location 리스트 조회")
    @WithMockCustomUser
    void findLocationByWarehouseId_success() throws Exception {
        List<LocationResponse> responses = List.of(
                new LocationResponse(1L, "고양-A", 1L, "A-01-01", 100, LocalDateTime.now()),
                new LocationResponse(1L, "고양-A", 2L, "A-01-02", 100, LocalDateTime.now())
        );

        given(locationService.findLocationByWarehouseId(anyLong())).willReturn(responses);

        mockMvc.perform(get(BASE_URL + "/{warehouseId}/locations", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data[0].code").value("A-01-01"));
    }

    @Test
    @DisplayName("특정 창고의 Location 조회: 실패 - warehouseId를 찾을 수 없음")
    @WithMockCustomUser
    void findLocationByWarehouseId_fail_not_found_warehouseId() throws Exception {
        given(locationService.findLocationByWarehouseId(anyLong()))
                .willThrow(new WarePulseException(ErrorCode.WAREHOUSE_NOT_FOUND));

        mockMvc.perform(get(BASE_URL + "/{warehouseId}/locations", 1L))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.errorMessage.code").value("W001"));
    }
}