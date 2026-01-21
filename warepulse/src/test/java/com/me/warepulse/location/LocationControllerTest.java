package com.me.warepulse.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.location.dto.LocationRequest;
import com.me.warepulse.location.dto.LocationResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LocationController.class)
@Import(TestSecurityConfig.class)
@DisplayName("Location: 컨트롤러 통합 테스트")
class LocationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    LocationService locationService;

    private static final String BASE_URL = "/locations";

    @Test
    @DisplayName("Location 생성: 성공 - ADMIN 권한은 생성 가능")
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void createLocation_success() throws Exception {
        LocationRequest request = new LocationRequest(1L, "A-01-01", 100);
        LocationResponse response = new LocationResponse(1L, "고양-A", 1L, "A-01-01", 100, LocalDateTime.now());

        given(locationService.createLocation(any(LocationRequest.class))).willReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.code").value("A-01-01"))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }

    @Test
    @DisplayName("Location 생성: 실패 - OPERATOR 권한은 생성 불가")
    @WithMockCustomUser
    void createLocation_fail_with_operator() throws Exception {
        LocationRequest request = new LocationRequest(1L, "A-01-01", 100);
        LocationResponse response = new LocationResponse(1L, "고양-A", 1L, "A-01-01", 100, LocalDateTime.now());

        given(locationService.createLocation(any(LocationRequest.class))).willReturn(response);

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
    @DisplayName("Location 생성: 실패 - warehouseId를 찾을 수 없음")
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void createLocation_fail_not_found_warehouseId() throws Exception {
        Long invalidId = 999L;
        LocationRequest request = new LocationRequest(invalidId, "A-01-01", 100);

        given(locationService.createLocation(any(LocationRequest.class)))
                .willThrow(new WarePulseException(ErrorCode.WAREHOUSE_NOT_FOUND));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.errorMessage.code").value("W001"));
    }

    @Test
    @DisplayName("Location 리스트 조회: 성공")
    @WithMockCustomUser
    void findLocations_success() throws Exception {
        List<LocationResponse> responses = List.of(
                new LocationResponse(1L, "고양-A", 1L, "A-01-01", 100, LocalDateTime.now()),
                new LocationResponse(1L, "고양-A", 2L, "A-01-02", 100, LocalDateTime.now())
        );

        given(locationService.findLocations()).willReturn(responses);

        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("Location 단건 조회: 성공")
    @WithMockCustomUser
    void findLocationById_success() throws Exception {
        Long locationId = 1L;
        LocationResponse response = new LocationResponse(1L, "고양-A", locationId, "A-01-01", 100, LocalDateTime.now());

        given(locationService.findLocationById(locationId)).willReturn(response);

        mockMvc.perform(get(BASE_URL + "/{locationId}", locationId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.locationId").value(locationId));
    }

    @Test
    @DisplayName("Location 단건 조회: 실패 - locationId를 찾을 수 없음")
    @WithMockCustomUser
    void findLocationById_fail_not_found_locationId() throws Exception {
        Long invalidId = 999L;
        given(locationService.findLocationById(invalidId))
                .willThrow(new WarePulseException(ErrorCode.LOCATION_NOT_FOUND));

        mockMvc.perform(get(BASE_URL + "/{locationId}", invalidId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.errorMessage.code").value("L001"));
    }

    @Test
    @DisplayName("Location 삭제: 성공 - ADMIN 권한은 삭제 가능")
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void deleteLocation_success() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{locationId}", 1L)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @DisplayName("Location 삭제: 실패 - locationId를 찾을 수 없음")
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void deleteLocation_fail_not_found_locationId() throws Exception {
        Long invalidId = 999L;
        doThrow(new WarePulseException(ErrorCode.LOCATION_NOT_FOUND))
                .when(locationService)
                .deleteLocation(invalidId);

        mockMvc.perform(delete(BASE_URL + "/{locationId}", invalidId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.errorMessage.code").value("L001"));
    }

    @Test
    @DisplayName("Location 삭제: 실패 - OPERATOR 권한은 삭제 불가")
    @WithMockCustomUser
    void deleteLocation_with_operator() throws Exception {
        mockMvc.perform(delete("/locations/{locationId}", 1L)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorMessage.code").value("E002"));
    }
}