package com.EnglishApp.learning_service.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "review_logs")
public class ReviewLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "srs_card_id", nullable = false)
    private SrsCard srsCard;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "reviewed_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime reviewedAt;

    @Column(name = "quality_score", nullable = false)
    private Byte qualityScore;

    @Column(name = "response_time_ms")
    private Integer responseTimeMs;
}
