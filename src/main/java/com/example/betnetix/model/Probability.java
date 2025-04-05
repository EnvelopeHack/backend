package com.example.betnetix.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class Probability {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "runner_id")
    private Runner runner;

    private Double probabilityForFirst;
    private Double probabilityForSecond;
    private Double probabilityForThird;
    private Double probabilityForFourth;
    private Double probabilityForFifth;
    private Double probabilityForSixth;

}
