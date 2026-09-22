package com.youpass.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name = "listening_tracks")
@Getter
@Setter
public class ListeningTrack {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn( name = "test_group_id")
    private TestGroup testGroup;

    @Column( name = "order_index")
    private Long orderIndex;

    @OneToMany( mappedBy = "track")
    @OrderBy("id ASC")
    private List<ListeningQuestion> questions;

    @Column( name = "title")
    private String title;

    @Column( name = "audio_url")
    private String audioUrl;

    @Column( name = "transcript", columnDefinition = "TEXT")
    private String transcript;
}
