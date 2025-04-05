package com.example.betnetix.repository;

import com.example.betnetix.model.Probability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProbabilityRepository extends JpaRepository<Probability, UUID> {

    @Query(value = "SELECT * FROM Probability WHERE runner_id = :id", nativeQuery = true)
    List<Probability> getProbabilitiesByRunnerId(Long id);
}
