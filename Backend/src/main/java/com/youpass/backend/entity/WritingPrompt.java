package com.youpass.backend.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.weaver.ast.Test;

@Entity
@Table(name = "writing_prompts")
@Getter
@Setter
public class WritingPrompt {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn( name = "test_group_id")
    private TestGroup testGroup;

    @Column( name = "order_index")
    private Long orderIndex;

    @Column( name = "title")
    private String title;

    @Column( name = "content", columnDefinition = "TEXT")
    private String content;
}
