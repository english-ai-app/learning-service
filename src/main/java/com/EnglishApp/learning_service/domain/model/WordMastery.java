package com.EnglishApp.learning_service.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "word_mastery")
public class WordMastery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_vocabulary_id", nullable = false)
    private UserVocabulary userVocabulary;

    @Column(name = "overall_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal overallScore;

    @Column(name = "recognition_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal recognitionScore;

    @Column(name = "meaning_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal meaningScore;

    @Column(name = "recall_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal recallScore;

    @Column(name = "listening_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal listeningScore;

    @Column(name = "context_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal contextScore;

    @Column(name = "speaking_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal speakingScore;

    @Column(name = "correct_count", nullable = false)
    private Integer correctCount;

    @Column(name = "wrong_count", nullable = false)
    private Integer wrongCount;

    @Column(name = "consecutive_correct", nullable = false)
    private Integer consecutiveCorrect;

    @Column(name = "last_practiced_at")
    private LocalDateTime lastPracticedAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
