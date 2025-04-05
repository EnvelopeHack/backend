package com.example.betnetix.controller;

import com.example.betnetix.model.Probability;
import com.example.betnetix.service.ProbabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.betnetix.constant.EndpointConstants.API_PROBABILITY_URL;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_PROBABILITY_URL)
public class ProbabilityController {

    private final ProbabilityService probabilityService;

    @GetMapping("place-table")
    public List<Probability> getProbabilityPlaceTable() {
        return probabilityService.getProbabilityPlaceTable();
    }

    @GetMapping("top2")
    public List<Double> getProbabilityTop2() {
        return probabilityService.getProbabilityTop2();
    }

    @GetMapping("top3")
    public List<Double> getProbabilityTop3(){
        return probabilityService.getProbabilityTop3();
    }

    @GetMapping("pares")
    public Double[][] getPareProbability(){
        return probabilityService.getPareProbability();
    }
}
