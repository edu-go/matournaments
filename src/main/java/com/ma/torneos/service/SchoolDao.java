package com.ma.torneos.service;

import com.ma.torneos.domain.School;

import java.util.List;

public interface SchoolDao {
    List<School> findByOwnerUserId(Long ownerUserId);
}
