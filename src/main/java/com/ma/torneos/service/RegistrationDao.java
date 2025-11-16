package com.ma.torneos.service;

import com.ma.torneos.ui.RegistrationView;

import java.util.List;

public interface RegistrationDao {
    int countActiveByDivisionId(Long divisionId); // PENDING + CONFIRMED
    List<RegistrationView> findByDivisionId(Long divisionId);
}
