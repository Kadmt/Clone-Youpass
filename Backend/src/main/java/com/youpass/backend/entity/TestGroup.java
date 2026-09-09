package com.youpass.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "test_groups")
@Getter
@Setter
public class TestGroup {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @Column( name = "skill_type")
    private String skillType;

    @Column(name = "title")
    private String title;
}
