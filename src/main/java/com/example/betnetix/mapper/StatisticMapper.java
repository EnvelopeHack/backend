package com.example.betnetix.mapper;

import com.example.betnetix.dto.RaceDto;
import com.example.betnetix.model.Race;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.TreeMap;

@Component
public class StatisticMapper {

    public RaceDto toRaceDto(Race race) {

        TreeMap<Integer, Long> map = new TreeMap<Integer, Long>(Map.of(
                1, race.getFirst().getId(),
                2, race.getSecond().getId(),
                3, race.getThird().getId(),
                4, race.getFourth().getId(),
                5, race.getFifth().getId(),
                6, race.getSixth().getId()));

        return RaceDto.builder()
                .raceNumber(race.getId())
                .positions(map)
                .build();
    }
}
