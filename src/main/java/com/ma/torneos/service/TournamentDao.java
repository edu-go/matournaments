package com.ma.torneos.service;

import com.ma.torneos.domain.Tournament;

import java.util.List;
import java.util.Optional;

public interface TournamentDao {
    List<Tournament> findAll();
    Optional<Tournament> findById(Long id);
    Tournament insert(Tournament t);
    void update(Tournament t);
    void deleteById(Long id);
    boolean existsByNameAndDate(String name, java.time.LocalDate date, Long excludeId);
}
