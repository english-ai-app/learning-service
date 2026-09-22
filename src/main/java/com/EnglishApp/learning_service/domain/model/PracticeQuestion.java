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
@Table(name = "practice_questions")
public class PracticeQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private PracticeSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_vocabulary_id")
    private UserVocabulary userVocabulary;

    @Column(name = "word_id")
    private Long wordId;

    @Column(name = "template_code", length = 64)
    private String templateCode;

    @Column(name = "exercise_type", nullable = false, length = 64)
    private String exerciseType;

    @Column(name = "skill_type", nullable = false, length = 32)
    private String skillType;

    @Column(name = "question_payload", nullable = false, columnDefinition = "json")
    private String questionPayload;

    @Column(name = "correct_answer_payload", nullable = false, columnDefinition = "json")
    private String correctAnswerPayload;

    @Column(name = "generation_source", nullable = false, length = 16)
    private String generationSource;

    @Column(name = "generation_version", length = 32)
    private String generationVersion;

    @Column(name = "source_exercise_id")
    private Long sourceExerciseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retry_of_question_id")
    private PracticeQuestion retryOfQuestion;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
