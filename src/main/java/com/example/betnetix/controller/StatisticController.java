package com.example.betnetix.controller;

import com.example.betnetix.dto.RaceDto;
import com.example.betnetix.mapper.StatisticMapper;
import com.example.betnetix.model.Race;
import com.example.betnetix.service.RaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.betnetix.constant.EndpointConstants.API_STATISTIC_URL;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_STATISTIC_URL)
public class StatisticController {

    private final RaceService raceService;
    private final StatisticMapper statisticMapper;

    @GetMapping
    public List<RaceDto> getRaces(){

        var races = raceService.getRaces();

        return races.stream().map(statisticMapper::toRaceDto).toList();
    }
}
