package com.me.warepulse.sku;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.warepulse.exception.ErrorCode;
import com.me.warepulse.exception.WarePulseException;
import com.me.warepulse.security.TestSecurityConfig;
import com.me.warepulse.security.WithMockCustomUser;
import com.me.warepulse.sku.dto.SkuRequest;
import com.me.warepulse.sku.dto.SkuResponse;
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
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SkuController.class)
@Import(TestSecurityConfig.class)
@DisplayName("상품: 컨트롤러 통합 테스트")
class SkuControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    SkuService skuService;

    private static final String BASE_URL = "/skus";

    @Test
    @DisplayName("상품 생성: 성공 - ADMIN 권한은 생성 가능")
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void createSku_success() throws Exception {
        SkuRequest request = new SkuRequest("HOOD-BLK-L", "블랙 후드 L", SkuType.EA);
        SkuResponse response = new SkuResponse(1L, "HOOD-BLK-L", "블랙 후드 L", "단품", LocalDateTime.now());

        given(skuService.createSku(any(SkuRequest.class))).willReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.code").value("HOOD-BLK-L"))
                .andExpect(jsonPath("$.data.name").value("블랙 후드 L"))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }

    @Test
    @DisplayName("상품 생성: 실패 - OPERATOR 권한은 생성 불가")
    @WithMockCustomUser
    void createSku_fail_with_operator() throws Exception {
        SkuRequest request = new SkuRequest("HOOD-BLK-L", "블랙 후드 L", SkuType.EA);

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
    @DisplayName("상품 리스트 조회: 성공")
    @WithMockCustomUser
    void findSkus_success() throws Exception {
        List<SkuResponse> responses = List.of(
                new SkuResponse(1L, "HOOD-BLK-XL", "블랙 후드 XL", "단품", LocalDateTime.now()),
                new SkuResponse(2L, "HOOD-BLK-L", "블랙 후드 L", "단품", LocalDateTime.now())
        );

        given(skuService.findSkus()).willReturn(responses);

        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("상품 단건 조회: 성공")
    @WithMockCustomUser
    void findSkuById_success() throws Exception {
        Long skuId = 1L;
        SkuResponse response = new SkuResponse(skuId, "HOOD-BLK-L", "블랙 후드 L", "단품", LocalDateTime.now());

        given(skuService.findSkuById(skuId)).willReturn(response);

        mockMvc.perform(get(BASE_URL + "/{skuId}", skuId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.skuId").value(skuId));
    }

    @Test
    @DisplayName("상품 단건 조회: 실패 - skuId를 찾을 수 없음")
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void findSkuById_fail_not_found_skuId() throws Exception {
        Long invalidId = 999L;
        given(skuService.findSkuById(invalidId))
                .willThrow(new WarePulseException(ErrorCode.SKU_NOT_FOUND));

        mockMvc.perform(get(BASE_URL + "/{skuId}", invalidId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.errorMessage.code").value("S001"));
    }

    @Test
    @DisplayName("상품 삭제: 성공 - ADMIN 권한은 삭제 가능")
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void deleteSku_success() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{skuId}", 1L)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @DisplayName("상품 삭제: 실패 - OPERATOR 권한은 삭제 불가")
    @WithMockCustomUser
    void deleteSku_with_operator() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{skuId}", 1L)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.errorMessage.code").value("E002"));
        then(skuService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("상품 삭제: 실패 - skuId를 찾을 수 없음")
    @WithMockCustomUser(username = "admin", roles = "ADMIN")
    void deleteSku_fail_not_found_skuId() throws Exception {
        Long invalidId = 999L;
        doThrow(new WarePulseException(ErrorCode.SKU_NOT_FOUND))
                .when(skuService)
                .deleteSku(invalidId);

        mockMvc.perform(delete(BASE_URL + "/{skuId}", invalidId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.errorMessage.code").value("S001"));
    }
}