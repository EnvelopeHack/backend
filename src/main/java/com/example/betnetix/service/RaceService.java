package com.example.betnetix.service;

import com.example.betnetix.model.Race;

import java.util.List;

public interface RaceService {

    Race addRaceStat(List<Long> positions);

    List<Race> getRaces();

    void startRace();

    void startRaces(int count);
}
