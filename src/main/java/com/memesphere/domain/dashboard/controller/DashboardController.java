package com.memesphere.domain.dashboard.controller;

import com.memesphere.domain.dashboard.dto.response.DashboardOverviewResponse;
import com.memesphere.domain.dashboard.service.DashboardQueryService;
import com.memesphere.domain.user.entity.User;
import com.memesphere.global.apipayload.ApiResponse;
import com.memesphere.domain.dashboard.dto.response.DashboardTrendListResponse;
import com.memesphere.global.jwt.CustomUserDetails;
import com.memesphere.global.jwt.TokenProvider;
import com.memesphere.global.validation.annotation.CheckPage;
import com.memesphere.domain.search.dto.response.SearchPageResponse;
import com.memesphere.domain.search.entity.SortType;
import com.memesphere.domain.search.entity.ViewType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name="대시보드", description = "대시보드 관련  API")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardQueryService dashboardQueryService;
    private final TokenProvider tokenProvider;

    @GetMapping("/overview")
    @Operation(summary = "밈코인 총 거래량 및 총 개수 조회 API",
            description = """
                    밈스피어에 등록된 밈코인의 총 거래량 및 총 개수를 보여줍니다. \n

                    **요청 형식**: ```없음```
                    
                    **응답 형식**:
                    ```
                    - "totalVolume": 등록된 밈코인의 총 거래량
                    - "totalCoin": 등록된 밈코인의 총 개수
                    ```""")
    public ApiResponse<DashboardOverviewResponse> getOverview() {
        return ApiResponse.onSuccess(dashboardQueryService.getOverview());
    }

    @GetMapping("/trend")
    @Operation(summary = "트렌드 조회 API",
            description = """
                    밈스피어에 등록된 24시간 내 거래량이 가장 많은 밈코인을 5위까지 보여줍니다. \n
                    
                    **요청 형식**: ```없음```
                    
                    **응답 형식**:
                    ```
                    - "LocalDateTime": 기록된 시간 (확인용, 코인 아이디 1을 기준으로 함)
                    - "trendList": 등록된 밈코인의 트렌드 순위 리스트(5위 까지)
                    - "coinId": 밈코인 아이디
                    - "image": 밈코인 이미지
                    - "name": 밈코인 이름
                    - "symbol": 밈코인 심볼
                    - "volume": 밈코인 거래량(확인용)
                    - "price": 밈코인 현재가
                    - "priceChange": 가격 변화량
                    - "priceChangeAbsolute": 가격 변화량(절대값)
                    - "priceChangeDirection": 밈코인 상승(up, 0 이상)/하락(down)
                    - "priceChangeRate": 가격 변화율
                    - "rankChangeDirection": 순위 상승(up, 0 이상)/하락(down)
                    ```""")
    public ApiResponse<DashboardTrendListResponse> getTrendList() {
        return ApiResponse.onSuccess(dashboardQueryService.getTrendList());
    }

    @GetMapping("/chart")
    @Operation(summary = "차트 조회 API",
            description = """
                    밈스피어에 등록된 밈코인의 차트 데이터를 보기 방식과 정렬 기준에 따라 보여줍니다. \n
                    검색 결과 조회 api(/search) 참고 \n
                    
                    **요청 형식**:
                    ```
                    - "viewType": 보기 방식 (GRID(default) / LIST)
                    - "sortType": 정렬 기준 (PRICE_CHANGE(default) / VOLUME_24H / PRICE)
                    - "page": 페이지 번호
                    ```
                    
                    **응답 형식**:
                    ```
                    - "gridItems": 그리드형 응답 리스트
                    - "listItems": 리스트형 응답 리스트
                    - "coinId": 밈코인 아이디 (g/l)
                    - "name": 밈코인 이름 (g/l)
                    - "symbol": 밈코인 심볼 (g/l)
                    - "currentPrice": 밈코인 현재가 (g/l)
                    - "highPrice" : 최고가 (g)
                    - "variation": 가격 변화량 (g)
                    - "lowPrice" : 최저가 (l)
                    - "market_cap": 시가총액 (l)
                    - "volume": 거래량 (l)
                    - "isCollected": 콜랙션 유무(true / false) (g/l)
                    - "listSize": 한 페이지당 보여지는 코인 정보 수(그리드 - 9 / 리스트 - 20)
                    - "totalPage": 볼 수 있는 페이지 개수
                    - "totalElements": 총 코인 정보 수
                    - "isFirst": 첫 페이지인지(true) / 아닌지(false)
                    - "isLast": 마지막 페이지인지(true) / 아닌지(false)
                    ```""")
    public ApiResponse<SearchPageResponse> getChartList(@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                        @RequestParam(name = "viewType", defaultValue = "GRID") ViewType viewType,
                                                        @RequestParam(name = "sortType", defaultValue = "PRICE_CHANGE") SortType sortType,
                                                        @CheckPage @RequestParam(name = "page") Integer page) {
        Integer pageNumber = page - 1;

        // 로그인 여부 처리
        // 로그인 x -> null
        // 로그인 o -> 유저 id
        Long userId = (customUserDetails == null) ? null : customUserDetails.getUser().getId();
        return ApiResponse.onSuccess(dashboardQueryService.getChartPage(userId, viewType, sortType, pageNumber));
    }
}
