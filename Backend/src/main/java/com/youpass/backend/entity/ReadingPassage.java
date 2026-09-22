package com.youpass.backend.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name = "reading_passages")
@Getter
@Setter
public class ReadingPassage {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn( name = "test_group_id", nullable = false)
    private TestGroup testGroup;

    @OneToMany(mappedBy = "passage")
    @OrderBy("id ASC")
    private List<ReadingQuestion> questions;


    @Column(name = "order_index")
    private Integer orderIndex;

    @Column( name = "title")
    private String title;

    @Column ( name = "content", columnDefinition = "TEXT")
    private String content;

}