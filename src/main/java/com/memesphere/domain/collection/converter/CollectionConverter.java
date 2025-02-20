package com.memesphere.domain.collection.converter;

import com.memesphere.domain.chartdata.entity.ChartData;
import com.memesphere.domain.collection.dto.response.CollectionListPreviewResponse;
import com.memesphere.domain.collection.entity.Collection;
import com.memesphere.domain.memecoin.entity.MemeCoin;
import com.memesphere.domain.collection.dto.response.CollectionPageResponse;
import com.memesphere.domain.collection.dto.response.CollectionGridPreviewResponse;
import com.memesphere.domain.search.entity.ViewType;
import com.memesphere.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionConverter {

    public static Collection toCollection(User user, MemeCoin coin) {
        return Collection.builder()
                .user(user).memeCoin(coin).build();
    }

    public static CollectionPageResponse toCollectionPageDTO(Page<Collection> collectionPage, ViewType viewType) {
        List<CollectionGridPreviewResponse> gridItems = null;
        List<CollectionListPreviewResponse> listItems = null;

        if (viewType == ViewType.GRID) {
            gridItems = collectionPage.stream()
                    .map(collection -> toCollectionGridPreviewDTO(collection))
                    .collect(Collectors.toList());
        } else if (viewType == ViewType.LIST) {
            listItems = collectionPage.stream()
                    .map(collection -> toCollectionListPreviewDTO(collection))
                    .collect(Collectors.toList());
        }

        return CollectionPageResponse.builder()
                .gridItems(gridItems)
                .listItems(listItems)
                .listSize(collectionPage.getContent().size())
                .totalPage(collectionPage.getTotalPages())
                .totalElements(collectionPage.getTotalElements())
                .isFirst(collectionPage.isFirst())
                .isLast(collectionPage.isLast())
                .build();
    }

    private static CollectionGridPreviewResponse toCollectionGridPreviewDTO(Collection collection) {
        MemeCoin memeCoin = collection.getMemeCoin();
        ChartData chartData = memeCoin.getChartDataList().get(0);

        return CollectionGridPreviewResponse.builder()
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
                .build();
    }

    public static CollectionListPreviewResponse toCollectionListPreviewDTO(Collection collection) {
        MemeCoin memeCoin = collection.getMemeCoin();
        ChartData chartData = memeCoin.getChartDataList().get(0);

        return CollectionListPreviewResponse.builder()
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
                .build();
    }
}
