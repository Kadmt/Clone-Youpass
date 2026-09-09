package com.youpass.backend.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "streaks")
@Getter
@Setter
public class Streak {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn( name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "current_streak")
    private Long currentStreak;

    @Column(name = "longest_streak")
    private Long longestStreak;

    @Column(name = "last_activity_date")
    private LocalDateTime lastActivityDate;


}
