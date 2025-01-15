package com.memesphere.domain;

import com.memesphere.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trading extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="trading_id")
    private Long id;

    @Column
    private LocalDateTime trade_time;

    @Column
    private Integer volume;

    @Column
    private BigDecimal price;

    @Column
    private BigDecimal open_price;

    @Column
    private BigDecimal close_price;

    @Column
    private BigDecimal low_price;

    @Column
    private BigDecimal high_price;

    @Column
    private BigDecimal variation;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="coin_id")
    private MemeCoin memeCoin;
}
