package com.memesphere.domain.chat.controller;

import com.memesphere.domain.chat.dto.request.ChatRequest;
import com.memesphere.domain.chat.dto.response.ChatResponse;
import com.memesphere.domain.chat.service.ChatService;
import com.memesphere.domain.user.entity.User;
import com.memesphere.global.apipayload.ApiResponse;
import com.memesphere.global.jwt.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "실시간 채팅", description = "실시간 채팅 관련 API")
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat/{coin_id}") // 클라이언트가 pub/chat/{coin_id}로 STOMP 메시지를 전송하면 실행됨
    @SendTo("/sub/{coin_id}")  // 메서드가 반환한 데이터를 구독 중인 클라이언트에게 전송
    public ChatResponse chat(@DestinationVariable("coin_id") Long coin_id,
                             @Payload ChatRequest chatRequest) {

        return chatService.saveMessage(coin_id, chatRequest);
    }

    @GetMapping("/chat/{coin_id}/list")
    @Operation(summary = "코인별 채팅 전체 메시지 조회 API",
            description = "특정 코인의 채팅방의 전체 메시지를 보여줍니다.")
    public ApiResponse<Page<ChatResponse>> getChatList(@PathVariable("coin_id") Long coin_id,
                                                           Pageable pageable,
                                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = (customUserDetails != null) ? customUserDetails.getUser() : null;

        return ApiResponse.onSuccess(chatService.getChatList(coin_id, pageable, user));
    }

    //최신 댓글 조회 Api
    @GetMapping("/chat/{coin_id}/latest")
    @Operation(summary = "코인별 최신 댓글 조회 API",
            description = "특정 코인에 대한 최신 댓글을 반환합니다. 요청 시 최신 댓글 하나만 가져옵니다.")
    public ApiResponse<ChatResponse> getLatestMessages(@PathVariable("coin_id") Long coin_id,
                                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = (customUserDetails != null) ? customUserDetails.getUser() : null;
        // 최신 댓글을 가져오는 서비스 메서드 호출
        ChatResponse latestMessage = chatService.getLatestMessages(coin_id, user);

        return ApiResponse.onSuccess(latestMessage);
    }

    @PostMapping("/chat/{coin_id}/like")
    @Operation(summary = "채팅 좋아요 관련 API",
            description = "채팅에서 하트를 클릭 시 하트가 채워지고, 좋아요 누른 사람의 숫자에 반영됩니다.")
    public ApiResponse<String> postChatLike(@PathVariable("coin_id") Long coin_id,
                                            @RequestParam(value = "chat_id", required = true) Long chat_id,
                                            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Long userId = customUserDetails.getUser().getId();

        return ApiResponse.onSuccess(chatService.postChatLike(chat_id, userId));
    }
}
