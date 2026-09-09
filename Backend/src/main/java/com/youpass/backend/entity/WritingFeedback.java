package com.youpass.backend.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "writing_feedback")
@Getter
@Setter
public class WritingFeedback {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn( name = "submission_id", nullable = false)
    private Submission submission;

    @Column( name = "task_response_score")
    private Float taskResponseScore;

    @Column( name= "coherence_score")
    private Float coherenceScore;

    @Column( name = "lexical_score")
    private Float lexicalScore;

    @Column( name = "grammar_score")
    private Float grammarScore;

    @Column( name = "errors_json", columnDefinition = "TEXT")
    private String errorsJson;

    @Column( name = "overall_band")
    private Float overallBand;

}
