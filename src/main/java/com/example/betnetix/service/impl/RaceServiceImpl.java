package com.example.betnetix.service.impl;

import com.example.betnetix.model.Race;
import com.example.betnetix.repository.RaceRepository;
import com.example.betnetix.service.ProbabilityService;
import com.example.betnetix.service.RaceService;
import com.example.betnetix.service.RunnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RaceServiceImpl implements RaceService {

    private final RaceRepository raceRepository;
    private final RunnerService runnerService;
    private final ProbabilityService probabilityService;

    @Override
    public Race addRaceStat(List<Long> positions) {

        var runner1 = runnerService.findById(positions.get(0));
        var runner2 = runnerService.findById(positions.get(1));
        var runner3 = runnerService.findById(positions.get(2));
        var runner4 = runnerService.findById(positions.get(3));
        var runner5 = runnerService.findById(positions.get(4));
        var runner6 = runnerService.findById(positions.get(5));

        var race = Race.builder()
                .first(runner1)
                .second(runner2)
                .third(runner3)
                .fourth(runner4)
                .fifth(runner5)
                .sixth(runner6)
                .build();

        raceRepository.save(race);

        probabilityService.recountProbabilities();

        return race;
    }

    @Override
    public List<Race> getRaces() {
        return raceRepository.findTop10ByOrderByIdDesc();
    }

    @Override
    public void startRace() {
        // алгоритм

    }

    @Override
    public void startRaces(int count) {

        for (int i = 0; i < count; ++i) {
            startRace();
        }
    }
}
