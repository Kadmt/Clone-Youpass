package com.youpass.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.EmbeddedColumnNaming;


@Entity
@Table(name = "listening_tracks")
@Getter
@Setter
public class ListeningTrack {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn( name = "test_group_id")
    private TestGroup testGroup;

    @Column( name = "order_index")
    private long orderIndex;

    @Column( name = "title")
    private String title;

    @Column( name = "audio_url")
    private String audioUrl;

    @Column( name = "transcript", columnDefinition = "TEXT")
    private String transcript;
}
