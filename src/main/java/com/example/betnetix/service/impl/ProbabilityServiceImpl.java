package com.example.betnetix.service.impl;

import com.example.betnetix.model.Probability;
import com.example.betnetix.model.Runner;
import com.example.betnetix.repository.ProbabilityRepository;
import com.example.betnetix.repository.RaceRepository;
import com.example.betnetix.service.ProbabilityService;
import com.example.betnetix.service.RunnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProbabilityServiceImpl implements ProbabilityService {

    private final RunnerService runnerService;
    private final ProbabilityRepository probabilityRepository;
    private final RaceRepository raceRepository;

    @Override
    public List<Probability> getProbabilityPlaceTable() {

        List<Runner> runners = runnerService.getRunners();
        List<Probability> probabilities = new ArrayList<>();

        runners.forEach(runner -> {
            probabilities.add(probabilityRepository.getProbabilitiesByRunnerId(runner.getId()).getFirst());
        });
        return probabilities;
    }

    @Override
    public Double[] getProbabilityTop2() {

        List<Runner> runners = runnerService.getRunners();
        Double[] probabilities = new Double[runners.size()];

        for (int i = 0; i < runners.size(); i++) {
            var probs = probabilityRepository.getProbabilitiesByRunnerId(runners.get(i).getId()).getFirst();
            var probability = probs.getProbabilityForFirst() + probs.getProbabilityForSecond();
            probabilities[i] = probability;
        }

        return probabilities;
    }

    @Override
    public Double[] getProbabilityTop3() {

        List<Runner> runners = runnerService.getRunners();
        Double[] probabilities = new Double[runners.size()];

        for (int i = 0; i < runners.size(); i++) {
            var probs = probabilityRepository.getProbabilitiesByRunnerId(runners.get(i).getId()).getFirst();
            var probability = probs.getProbabilityForFirst()
                    + probs.getProbabilityForSecond() + probs.getProbabilityForThird();
            probabilities[i] = probability;
        }

        return probabilities;
    }

    @Override
    public Double[][] getProbabilityTop2and3() {
        return new Double[][]{getProbabilityTop2(), getProbabilityTop3()};
    }

    @Override
    public Double[][] getPareProbability() {

        List<Runner> runners = runnerService.getRunners();

        Double[][] probabilities = new Double[runners.size()][runners.size()];

        for (int i = 0; i < runners.size(); i++) {
            Double[] probs = new Double[runners.size()];

            for (int j = 0; j < runners.size(); j++) {
                if (!runners.get(i).getId().equals(runners.get(j).getId())) {
                    var probFirst = probabilityRepository.getProbabilitiesByRunnerId(runners.get(i).getId()).getFirst();
                    var probSecond = probabilityRepository.getProbabilitiesByRunnerId(runners.get(j).getId()).getFirst();
                    probs[j] = calculatePairProbability(probFirst, probSecond);
                } else {
                    probs[j] = 0.0;
                }
            }
            probabilities[i] = probs;
        }

        return probabilities;
    }

    @Override
    public void recountProbabilities() {

        List<Runner> runners = runnerService.getRunners();

        runners.forEach(runner -> {
            var probs = probabilityRepository.getProbabilitiesByRunnerId(runner.getId()).getFirst();
            probs.setProbabilityForFirst( (double) raceRepository.countAllByFirst(runner) / raceRepository.count());
            probs.setProbabilityForSecond( (double) raceRepository.countAllBySecond(runner) / raceRepository.count());
            probs.setProbabilityForThird( (double) raceRepository.countAllByThird(runner) / raceRepository.count());
            probs.setProbabilityForFourth( (double) raceRepository.countAllByFourth(runner) / raceRepository.count());
            probs.setProbabilityForFifth( (double) raceRepository.countAllByFifth(runner) / raceRepository.count());
            probs.setProbabilityForSixth( (double) raceRepository.countAllBySixth(runner) / raceRepository.count());
            probabilityRepository.save(probs);
        });
    }

    private double calculatePairProbability(Probability probA, Probability probB) {

        // P(A=1) * P(B=2 | A=1)
        return probA.getProbabilityForFirst()
                * (probB.getProbabilityForSecond() / (1 - probB.getProbabilityForFirst()));
    }
}
