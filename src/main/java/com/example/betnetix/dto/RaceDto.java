package com.example.betnetix.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class RaceDto {

    private Long raceNumber;

    private Map<Integer, Long> positions;
}
