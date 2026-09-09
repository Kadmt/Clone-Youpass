package com.youpass.backend.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "listening_questions")
@Getter
@Setter
public class ListeningQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "track_id", nullable = false)
    private ListeningTrack track;

    @Column(name = "question_text", columnDefinition = "TEXT")
    private String questionText;

    @Column(name = "question_type")
    private String questionType;

    @Column(name = "correct_answer")
    private String correctAnswer;

    @Column(name = "options_json", columnDefinition = "TEXT")
    private String optionsJson;


}
