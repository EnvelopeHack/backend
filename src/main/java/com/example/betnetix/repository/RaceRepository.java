package com.example.betnetix.repository;

import com.example.betnetix.model.Race;
import com.example.betnetix.model.Runner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RaceRepository extends JpaRepository<Race, Long> {
    List<Race> findTop10ByOrderByIdDesc();

    Integer countAllByFirst(Runner runner);
    Integer countAllBySecond(Runner runner);
    Integer countAllByThird(Runner runner);
    Integer countAllByFourth(Runner runner);
    Integer countAllByFifth(Runner runner);
    Integer countAllBySixth(Runner runner);
}
