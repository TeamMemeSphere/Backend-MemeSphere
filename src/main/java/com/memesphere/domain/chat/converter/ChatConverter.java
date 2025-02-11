package com.memesphere.domain.chat.converter;

import com.memesphere.domain.chat.entity.Chat;
import com.memesphere.domain.chat.entity.ChatLike;
import com.memesphere.domain.memecoin.entity.MemeCoin;
import com.memesphere.domain.chat.dto.request.ChatRequest;
import com.memesphere.domain.chat.dto.response.ChatResponse;
import com.memesphere.domain.user.entity.User;

public class ChatConverter {

    public static Chat toChat(MemeCoin memeCoin, ChatRequest chatRequest, User user) {

        return Chat.builder()
                .user(user)
                .memeCoin(memeCoin)
                .message(chatRequest.getMessage())
                .build();
    }

    public static ChatResponse toChatResponse(Chat chat) {

        return ChatResponse.builder()
                .id(chat.getId())
                .message(chat.getMessage())
                .memeCoin(chat.getMemeCoin().getName())
                .likes(chat.getChatLikeList().size())
                .createdAt(chat.getCreatedAt())
                .nickname(chat.getUser().getNickname())
                .build();
    }

    public static ChatLike toChatLike(Chat chat, User user) {
        return ChatLike.builder()
                .user(user)
                .chat(chat)
                .build();
    }
}
