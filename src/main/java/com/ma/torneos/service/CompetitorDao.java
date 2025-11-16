package com.ma.torneos.service;

import com.ma.torneos.domain.Competitor;

import java.util.List;
import java.util.Optional;

public interface CompetitorDao {
    Optional<Competitor> findById(Long id);
    List<Competitor> findBySchoolId(Long schoolId);
}
