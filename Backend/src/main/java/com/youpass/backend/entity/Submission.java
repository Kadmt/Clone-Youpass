package com.youpass.backend.entity;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Getter
@Setter
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "skill_type")
    private String skillType;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "answer_data", columnDefinition = "TEXT")
    private String answerData;

    @Column(name = "score")
    private Integer score;

    @Timestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "submission")   // lấy feedback qua submission - không cần tách riêng thành feedbackRepo
    private WritingFeedback feedback;
}