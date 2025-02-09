package com.memesphere.domain.chat.repository;

import com.memesphere.domain.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long>, ChatCustomRepository {

    Chat findFirstByMemeCoin_IdOrderByCreatedAtDesc(Long coin_id);
    List<Chat> findAllByMemeCoin_Id(Long coin_id);
}
