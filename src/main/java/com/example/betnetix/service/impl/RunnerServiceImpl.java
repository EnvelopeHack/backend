package com.example.betnetix.service.impl;

import com.example.betnetix.model.Probability;
import com.example.betnetix.model.Runner;
import com.example.betnetix.repository.ProbabilityRepository;
import com.example.betnetix.repository.RunnerRepository;
import com.example.betnetix.service.RunnerService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RunnerServiceImpl implements RunnerService {

    private final RunnerRepository runnerRepository;
    private final ProbabilityRepository probabilityRepository;

    @Override
    public Runner findById(Long id) {
        return runnerRepository.findById(id).orElse(null);
    }

    @Override
    public Runner changeRunnerProbability(Long id, Double[] probability) {

        Runner runner = runnerRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Runner with id " + id + " does not exist"));

        var exProbs = probabilityRepository.getProbabilitiesByRunnerId(runner.getId()).getFirst();

        exProbs.setProbabilityForFirst(probability[0]);
        exProbs.setProbabilityForSecond(probability[1]);
        exProbs.setProbabilityForThird(probability[2]);
        exProbs.setProbabilityForFourth(probability[3]);
        exProbs.setProbabilityForFifth(probability[4]);
        exProbs.setProbabilityForSixth(probability[5]);

        probabilityRepository.save(exProbs);

        return runner;
    }

    @Override
    public List<Runner> getRunners() {
        return runnerRepository.findAll();
    }
}
