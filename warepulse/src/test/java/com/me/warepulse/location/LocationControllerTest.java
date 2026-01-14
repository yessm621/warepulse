package com.me.warepulse.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.location.dto.LocationRequest;
import com.me.warepulse.location.dto.LocationResponse;
import com.me.warepulse.security.TestSecurityConfig;
import com.me.warepulse.security.WithMockCustomUser;
import com.me.warepulse.sku.SkuType;
import com.me.warepulse.sku.dto.SkuRequest;
import com.me.warepulse.sku.dto.SkuResponse;
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
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LocationController.class)
@Import(TestSecurityConfig.class)
class LocationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    LocationService locationService;

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void createLocation_success() throws Exception {
        LocationRequest request = new LocationRequest(1L, "A-01-01", 100);
        LocationResponse response = new LocationResponse(1L, "고양-A",
                1L, "A-01-01", 100, LocalDateTime.now());

        given(locationService.createLocation(any(LocationRequest.class))).willReturn(response);

        mockMvc.perform(post("/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }

    @Test
    @WithMockCustomUser(username = "username1", roles = "OPERATOR")
    void createLocation_fail_with_operator() throws Exception {
        LocationRequest request = new LocationRequest(1L, "A-01-01", 100);
        LocationResponse response = new LocationResponse(1L, "고양-A",
                1L, "A-01-01", 100, LocalDateTime.now());

        given(locationService.createLocation(any(LocationRequest.class))).willReturn(response);

        mockMvc.perform(post("/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.errorMessage.code").value("E002"));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void createLocation_fail_warehouse_not_found() throws Exception {
        LocationRequest request = new LocationRequest(100L, "A-01-01", 100);

        given(locationService.createLocation(any(LocationRequest.class)))
                .willThrow(new WarePulseException(ErrorCode.WAREHOUSE_NOT_FOUND));

        mockMvc.perform(post("/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void findLocations_success() throws Exception {
        List<LocationResponse> responses = List.of(
                new LocationResponse(1L, "고양-A", 1L, "A-01-01", 100, LocalDateTime.now()),
                new LocationResponse(1L, "고양-A", 2L, "A-01-02", 100, LocalDateTime.now())
        );

        given(locationService.findLocations()).willReturn(responses);

        mockMvc.perform(get("/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void findLocationById_success() throws Exception {
        LocationResponse response = new LocationResponse(1L, "고양-A", 1L, "A-01-01", 100, LocalDateTime.now());

        given(locationService.findLocationById(any())).willReturn(response);

        mockMvc.perform(get("/locations/{locationId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void findLocationById_fail_does_not_exist_locationId() throws Exception {
        given(locationService.findLocationById(any()))
                .willThrow(new WarePulseException(ErrorCode.LOCATION_NOT_FOUND));

        mockMvc.perform(get("/locations/{locationId}", 100L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void deleteLocation_success() throws Exception {
        mockMvc.perform(delete("/locations/{locationId}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void deleteLocation_fail_does_not_exist_locationId() throws Exception {
        doThrow(new WarePulseException(ErrorCode.LOCATION_NOT_FOUND))
                .when(locationService)
                .deleteLocation(any());

        mockMvc.perform(delete("/locations/{locationId}", 1L)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    @WithMockCustomUser(username = "username1", roles = "OPERATOR")
    void deleteLocation_with_operator() throws Exception {
        mockMvc.perform(delete("/locations/{locationId}", 1L)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
        then(locationService).shouldHaveNoInteractions();
    }
}