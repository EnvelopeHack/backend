package com.example.betnetix.service;

import com.example.betnetix.model.Probability;

import java.util.List;

public interface ProbabilityService {

    List<Probability> getProbabilityPlaceTable();

    Double[] getProbabilityTop2();

    Double[] getProbabilityTop3();

    Double[][] getProbabilityTop2and3();

    Double[][] getPareProbability();

    void recountProbabilities();

}
