package com.youpass.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "vocabulary_entries")
@Getter
@Setter
public class VocabularyEntry {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn( name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn( name = "source_passage_id")
    private ReadingPassage passage;

    private String word;

    @Column( name = "context_sentence", columnDefinition = "TEXT")
    private String contextSentence;

    @Column( name = "meaning")
    private String meaning;

    @Column( name = "next_review_date")
    private LocalDateTime nextReviewDate;
}
