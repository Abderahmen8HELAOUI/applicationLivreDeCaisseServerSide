package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

@Entity
@Table(name = "organisms")
@Data
public class Organism {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "type")
    private String type;

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "organism_id")
    private List<User> users = new ArrayList<>();

    public Organism() {
    }

    public Organism(String type, String name, String code) {
        this.type = type;
        this.name = name;
        this.code = code;
    }

}
