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
    private long id;

    @OneToOne
    @JoinColumn( name = "submission_id", nullable = false)
    private Submission submission;

    @Column( name = "task_response_score")
    private long taskResponseScore;

    @Column( name= "coherence_score")
    private long coherenceScore;

    @Column( name = "lexical_score")
    private long lexicalScore;

    @Column( name = "grammar_score")
    private float overallBand;

    @Column( name = "errors_json", columnDefinition = "TEXT")
    private String errorsJson;

}
