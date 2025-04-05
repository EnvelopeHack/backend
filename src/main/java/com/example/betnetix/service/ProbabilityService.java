package com.example.betnetix.service;

import com.example.betnetix.model.Probability;

import java.util.List;

public interface ProbabilityService {

    List<Probability> getProbabilityPlaceTable();

    List<Double> getProbabilityTop2();

    List<Double> getProbabilityTop3();

    Double[][] getPareProbability();

    void recountProbabilities();

}
