package com.memesphere.domain.dashboard.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class DashboardTrendResponse {
    @Schema(description = "밈코인 아이디", example = "1")
    private Long coinId;

    @Schema(description = "이미지", example = "https://bucketname.s3.region.amazonaws.com/image1.jpg")
    private String image;

    @Schema(description = "이름", example = "도지")
    private String name;

    @Schema(description = "심볼", example = "DOGE")
    private String symbol;

    @Schema(description = "거래량", example = "468216298627.20")
    private BigDecimal volume;

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

    @Schema(description = "순위 변화 방향", example = "down")
    private String rankChangeDirection;
}