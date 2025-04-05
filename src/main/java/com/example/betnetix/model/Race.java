package com.example.betnetix.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Race {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "first_runner_id")
    private Runner first;

    @ManyToOne
    @JoinColumn(name = "second_runner_id")
    private Runner second;

    @ManyToOne
    @JoinColumn(name = "third_runner_id")
    private Runner third;

    @ManyToOne
    @JoinColumn(name = "fourth_runner_id")
    private Runner fourth;

    @ManyToOne
    @JoinColumn(name = "fifth_runner_id")
    private Runner fifth;

    @ManyToOne
    @JoinColumn(name = "sixth_runner_id")
    private Runner sixth;
}
