package com.youpass.backend.entity;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn( name = "user_id", nullable = false)
    private User user;

    @Column(name = "skill_type")
    private String skill_type;

    @Column( name = "reference_id")
    private long referenceId;

    @Column( name = "answer_data", columnDefinition = "TEXT")
    private String answerData;

    @Column( name = "score")
    private long score;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


}
