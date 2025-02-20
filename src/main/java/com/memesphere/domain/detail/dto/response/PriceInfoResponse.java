package com.memesphere.domain.detail.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PriceInfoResponse {
    @Schema(description = "밈코인 아이디", example = "1")
    private Long coinId;

    @Schema(description = "현재가", example = "0.20")
    private String price;

    @Schema(description = "가격 변화량", example = "-0.03")
    private String priceChange;

    @Schema(description = "가격 변화량 절대값", example = "0.03")
    private String priceChangeAbsolute;

    @Schema(description = "가격 변화 방향", example = "down")
    private String priceChangeDirection;

    @Schema(description = "가격 변화율", example = "-6.35")
    private String priceChangeRate;

    @Schema(description = "거래량 가중 평균 가격", example = "-942.38")
    private String weightedAveragePrice;

    @Schema(description = "24h 최고가", example = "2500")
    private String highPrice;

    @Schema(description = "24h 최저가", example = "1500")
    private String lowPrice;
}
