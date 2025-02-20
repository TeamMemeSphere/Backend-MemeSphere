package com.memesphere.domain.search.converter;

import com.memesphere.domain.chartdata.entity.ChartData;
import com.memesphere.domain.memecoin.entity.MemeCoin;
import com.memesphere.domain.search.entity.ViewType;
import com.memesphere.domain.search.dto.response.SearchGridPreviewResponse;
import com.memesphere.domain.search.dto.response.SearchListPreviewResponse;
import com.memesphere.domain.search.dto.response.SearchPageResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

public class SearchConverter {
    public static SearchPageResponse toSearchPageDTO(Page<MemeCoin> searchPage, ViewType viewType, List<Long> userCollectionIds) {
        List<SearchGridPreviewResponse> gridItems = null;
        List<SearchListPreviewResponse> listItems = null;

        if (viewType == ViewType.GRID) {
            gridItems = searchPage.stream()
                    .map(memeCoin -> toSearchGridPreviewDTO(memeCoin, userCollectionIds.contains(memeCoin.getId())))
                    .collect(Collectors.toList());
        } else if (viewType == ViewType.LIST) {
            listItems = searchPage.stream()
                    .map(memeCoin -> toSearchListPreviewDTO(memeCoin, userCollectionIds.contains(memeCoin.getId())))
                    .collect(Collectors.toList());
        }

        return SearchPageResponse.builder()
                .gridItems(gridItems)
                .listItems(listItems)
                .listSize(searchPage.getContent().size())
                .totalPage(searchPage.getTotalPages())
                .totalElements(searchPage.getTotalElements())
                .isFirst(searchPage.isFirst())
                .isLast(searchPage.isLast())
                .build();
    }

    public static SearchGridPreviewResponse toSearchGridPreviewDTO(MemeCoin memeCoin, boolean isCollected) {
        ChartData chartData = memeCoin.getChartDataList().get(0);

        return SearchGridPreviewResponse.builder()
                .coinId(memeCoin.getId())
                .name(memeCoin.getName())
                .symbol(memeCoin.getSymbol())
                .image(memeCoin.getImage())
                .currentPrice(BigDecimal.valueOf(chartData.getPrice())
                        .setScale(8, RoundingMode.HALF_UP)
                        .toPlainString())
                .highPrice(BigDecimal.valueOf(chartData.getHigh_price())
                        .setScale(8, RoundingMode.HALF_UP)
                        .toPlainString())
                .lowPrice(BigDecimal.valueOf(chartData.getLow_price())
                        .setScale(8, RoundingMode.HALF_UP)
                        .toPlainString())
                .priceChange(BigDecimal.valueOf(chartData.getPriceChange())
                        .setScale(8, RoundingMode.HALF_UP)
                        .toPlainString())
                .priceChangeRate(BigDecimal.valueOf(chartData.getPriceChangeRate())
                        .setScale(8, RoundingMode.HALF_UP)
                        .toPlainString())
                .isCollected(isCollected)
                .build();
    }

    public static SearchListPreviewResponse toSearchListPreviewDTO(MemeCoin memeCoin, boolean isCollected) {
        ChartData chartData = memeCoin.getChartDataList().get(0);

        return SearchListPreviewResponse.builder()
                .coinId(memeCoin.getId())
                .name(memeCoin.getName())
                .symbol(memeCoin.getSymbol())
                .currentPrice(BigDecimal.valueOf(chartData.getPrice())
                        .setScale(8, RoundingMode.HALF_UP)
                        .toPlainString())
                .priceChangeRate(BigDecimal.valueOf(chartData.getPriceChangeRate())
                        .setScale(8, RoundingMode.HALF_UP)
                        .toPlainString())
                .weightedAveragePrice(BigDecimal.valueOf(chartData.getWeighted_average_price())
                        .setScale(8, RoundingMode.HALF_UP)
                        .toPlainString())
                .volume(chartData.getVolume())
                .isCollected(isCollected)
                .build();
    }
}