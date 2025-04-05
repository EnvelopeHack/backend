package com.example.betnetix.service;

import com.example.betnetix.model.Runner;

import java.util.List;

public interface RunnerService {

    Runner findById(Long id);

    Runner changeRunnerProbability(Long id, Double[] probability);

    List<Runner> getRunners();
}
