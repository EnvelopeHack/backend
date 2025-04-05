package com.example.betnetix.service;

import com.example.betnetix.model.Probability;
import com.example.betnetix.model.Runner;
import com.example.betnetix.model.RunnerParams;
import com.example.betnetix.repository.ProbabilityRepository;
import com.example.betnetix.repository.RunnerRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class InitService {
    private final RunnerRepository runnerRepository;
    private final ProbabilityRepository probabilityRepository;
    private final RaceService raceService;

    private final Map<Long, RunnerParams> runnerParams = new HashMap<>(); // переименовал, чтобы не путать
    private final Map<Long, Double> positions = new ConcurrentHashMap<>();
    private final List<Long> finalPositions = Collections.synchronizedList(new ArrayList<>());
    private static final double TOTAL_DISTANCE = 100.0;

    @PostConstruct
    public void initRunners() {
        if (runnerRepository.count() == 0) {

            List<String> colors = List.of("red", "blue", "yellow", "green", "purple", "black");
            for (String color : colors) {
                Runner runner = new Runner();
                runner.setColor(color);
                runner = runnerRepository.save(runner); // сохраняем и получаем объект с ID

                Probability probability = new Probability();
                probability.setRunner(runner);
                probabilityRepository.save(probability);

                // Заполняем параметры бегунов (mu = 10, sigma = 2)
                runnerParams.put(runner.getId(), new RunnerParams(10, 2));
                positions.put(runner.getId(), 0.0); // начальная позиция = 0
            }


            for (int i = 0; i < 20; i++) {
                simulateRace();
            }
        }
    }

    private void simulateRace() {
        Random random = new Random();

        while (positions.values().stream().anyMatch(pos -> pos < TOTAL_DISTANCE)) {
            for (Long runnerId : runnerParams.keySet()) {
                if (positions.get(runnerId) < TOTAL_DISTANCE) {
                    RunnerParams params = runnerParams.get(runnerId);
                    double speed = params.mu() + params.sigma() * random.nextGaussian();
                    double newPosition = positions.get(runnerId) + speed;
                    newPosition = Math.min(newPosition, TOTAL_DISTANCE);
                    newPosition = Math.round(newPosition * 100.0) / 100.0; // округление до 2 знаков
                    positions.put(runnerId, newPosition);

                    if (newPosition >= TOTAL_DISTANCE && !finalPositions.contains(runnerId)) {
                        finalPositions.add(runnerId);
                    }
                }
            }
        }

        raceService.addRaceStat(finalPositions);

        positions.replaceAll((runnerId, pos) -> 0.0);
        finalPositions.clear();
    }
}