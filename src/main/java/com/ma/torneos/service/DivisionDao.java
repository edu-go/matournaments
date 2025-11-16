package com.ma.torneos.service;

import com.ma.torneos.domain.Division;

import java.util.List;

public interface DivisionDao {
    List<Division> findByTournamentId(Long tournamentId);
    Division insert(Division d);
    void update(Division d);
    void deleteById(Long id);
}
