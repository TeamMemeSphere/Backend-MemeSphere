package com.memesphere.domain.detail.converter;

import com.memesphere.domain.chartdata.entity.ChartData;
import com.memesphere.domain.detail.dto.response.PriceInfoResponse;
import com.memesphere.domain.memecoin.entity.MemeCoin;
import com.memesphere.domain.detail.dto.response.DetailGetResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DetailConverter {

    public static DetailGetResponse toDetailGetResponse(MemeCoin memeCoin) {

        return DetailGetResponse.builder()
                .id(memeCoin.getId())
                .name(memeCoin.getName())
                .symbol(memeCoin.getSymbol())
                .description(memeCoin.getDescription())
                .image(memeCoin.getImage())
                .keywords(memeCoin.getKeywords())
                .collectionActive(memeCoin.getCollectionList().isEmpty())
                .rank(memeCoin.getTrendRank())
                .build();
    }

    public static PriceInfoResponse toPriceInfoResponse(MemeCoin memeCoin, ChartData data) {
        return PriceInfoResponse.builder()
                .coinId(memeCoin.getId())
                .price(BigDecimal.valueOf(data.getPrice())
                        .setScale(8, RoundingMode.HALF_UP)
                        .toPlainString())
                .priceChange(BigDecimal.valueOf(data.getPriceChange())
                        .setScale(8, RoundingMode.HALF_UP)
                        .toPlainString())
                .priceChangeAbsolute(BigDecimal.valueOf(Math.abs(data.getPriceChange()))
                        .setScale(8, RoundingMode.HALF_UP)
                        .toPlainString())
                .priceChangeDirection(data.getPriceChangeRate() < 0 ? "down" : "up")
                .priceChangeRate(BigDecimal.valueOf(data.getPriceChangeRate())
                        .setScale(8, RoundingMode.HALF_UP)
                        .toPlainString())
                .weightedAveragePrice(BigDecimal.valueOf(data.getWeighted_average_price())
                        .setScale(8, RoundingMode.HALF_UP)
                        .toPlainString())
                .highPrice(BigDecimal.valueOf(data.getHigh_price())
                        .setScale(8, RoundingMode.HALF_UP)
                        .toPlainString())
                .lowPrice(BigDecimal.valueOf(data.getLow_price())
                        .setScale(8, RoundingMode.HALF_UP)
                        .toPlainString())
                .build();
    }
}
