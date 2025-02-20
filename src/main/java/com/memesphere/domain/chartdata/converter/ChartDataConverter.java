package com.memesphere.domain.chartdata.converter;

import com.memesphere.domain.binance.dto.response.BinanceTickerResponse;
import com.memesphere.domain.chartdata.entity.ChartData;
import com.memesphere.domain.memecoin.entity.MemeCoin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ChartDataConverter {

    public static ChartData toChartData(MemeCoin memeCoin, BinanceTickerResponse response) {
        return ChartData.builder()
                .memeCoin(memeCoin)
                .recordedTime(LocalDateTime.now())
                .price(Double.parseDouble(response.getLastPrice()))
                .priceChange(Double.parseDouble(response.getPriceChange()))
                .priceChangeRate(Double.parseDouble(response.getPriceChangePercent()))
                .weighted_average_price(Double.parseDouble(response.getWeightedAvgPrice()))
                .volume(new BigDecimal(response.getVolume()))
                .low_price(Double.parseDouble(response.getLowPrice()))
                .high_price(Double.parseDouble(response.getHighPrice()))
                .build();
    }
}
