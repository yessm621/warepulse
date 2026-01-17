package com.me.warepulse.sku;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.security.TestSecurityConfig;
import com.me.warepulse.security.WithMockCustomUser;
import com.me.warepulse.sku.dto.SkuRequest;
import com.me.warepulse.sku.dto.SkuResponse;
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
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SkuController.class)
@Import(TestSecurityConfig.class)
class SkuControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    SkuService skuService;

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void createSku_success() throws Exception {
        SkuRequest request = new SkuRequest("HOOD-BLK-L", "블랙 후드 L", SkuType.EA);
        SkuResponse response = new SkuResponse(1L, "HOOD-BLK-L", "블랙 후드 L", "단품", LocalDateTime.now());

        given(skuService.createSku(any(SkuRequest.class))).willReturn(response);

        mockMvc.perform(post("/skus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }

    @Test
    @WithMockCustomUser(username = "username1", roles = "OPERATOR")
    void createSku_fail_with_operator() throws Exception {
        SkuRequest request = new SkuRequest("HOOD-BLK-L", "블랙 후드 L", SkuType.EA);
        SkuResponse response = new SkuResponse(1L, "HOOD-BLK-L", "블랙 후드 L", "단품", LocalDateTime.now());

        given(skuService.createSku(any(SkuRequest.class))).willReturn(response);

        mockMvc.perform(post("/skus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.errorMessage.code").value("E002"));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void findSkus_success() throws Exception {
        List<SkuResponse> responses = List.of(
                new SkuResponse(1L, "HOOD-BLK-XL", "블랙 후드 XL", "단품", LocalDateTime.now()),
                new SkuResponse(2L, "HOOD-BLK-L", "블랙 후드 L", "단품", LocalDateTime.now())
        );

        given(skuService.findSkus()).willReturn(responses);

        mockMvc.perform(get("/skus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void findSkuById_success() throws Exception {
        SkuResponse response = new SkuResponse(1L, "HOOD-BLK-L", "블랙 후드 L", "단품", LocalDateTime.now());

        given(skuService.findSkuById(any())).willReturn(response);

        mockMvc.perform(get("/skus/{skuId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void findSkuById_fail_does_not_exist_skuId() throws Exception {
        given(skuService.findSkuById(any()))
                .willThrow(new WarePulseException(ErrorCode.SKU_NOT_FOUND));

        mockMvc.perform(get("/skus/{skuId}", 100L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void deleteSku_success() throws Exception {
        mockMvc.perform(delete("/skus/{skuId}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void deleteSku_fail_does_not_exist_skuId() throws Exception {
        doThrow(new WarePulseException(ErrorCode.SKU_NOT_FOUND))
                .when(skuService)
                .deleteSku(any());

        mockMvc.perform(delete("/skus/{skuId}", 1L)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"));
    }

    @Test
    @WithMockCustomUser(username = "username1", roles = "OPERATOR")
    void deleteSku_with_operator() throws Exception {
        mockMvc.perform(delete("/skus/{skuId}", 1L)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
        then(skuService).shouldHaveNoInteractions();
    }
}