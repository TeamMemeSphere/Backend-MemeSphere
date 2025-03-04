package com.memesphere.domain.search.controller;

import com.memesphere.global.apipayload.ApiResponse;
import com.memesphere.domain.memecoin.entity.MemeCoin;
import com.memesphere.domain.search.entity.SortType;
import com.memesphere.domain.search.entity.ViewType;
import com.memesphere.domain.search.dto.response.SearchPageResponse;
import com.memesphere.domain.collection.service.CollectionQueryService;
import com.memesphere.domain.search.service.SearchQueryService;
import com.memesphere.domain.search.converter.SearchConverter;
import com.memesphere.global.jwt.CustomUserDetails;
import com.memesphere.global.validation.annotation.CheckPage;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchRestController {
    private final SearchQueryService searchQueryService;
    private final CollectionQueryService collectionQueryService;

    @GetMapping("/search")
    @Operation(summary = "검색 결과 조회 API", description = "검색어와 페이지 번호를 기준으로 검색 결과를 반환합니다.")
    public ApiResponse<SearchPageResponse> getSearchPage(
            @RequestParam(name = "searchWord") String searchWord, // 검색어
            @RequestParam(name = "viewType", defaultValue = "GRID") ViewType viewType, // 뷰 타입 (grid 또는 list)
            @RequestParam(name = "sortType", defaultValue = "PRICE_CHANGE") SortType sortType, // 정렬 기준 (MKTCap, 24h Volume, Price)
            @CheckPage @RequestParam(name = "page") Integer page, // 페이지 번호
            @AuthenticationPrincipal CustomUserDetails userDetails // 현재 로그인한 유저
    ) {
        Integer pageNumber = page - 1;
        Long userId = (userDetails == null) ? null : userDetails.getUser().getId();

        Page<MemeCoin> searchPage = searchQueryService.getSearchPage(searchWord, viewType, sortType, pageNumber);
        List<Long> userCollectionIds = collectionQueryService.getUserCollectionIds(userId);

        return ApiResponse.onSuccess(SearchConverter.toSearchPageDTO(searchPage, viewType, userCollectionIds));
    }
}
