package com.ma.torneos.service;

import com.ma.torneos.domain.Division;
import com.ma.torneos.service.exception.BusinessException;

import java.util.List;

public class DivisionService {

    private final DivisionDaoJdbcImpl dao = new DivisionDaoJdbcImpl();

    public List<Division> listByTournament(Long tournamentId) {
        return dao.findByTournamentId(tournamentId);
    }

    public Division create(Division d) {
        validate(d);
        return dao.insert(d);
    }

    public void update(Division d) {
        if (d.getId() == null) {
            throw new BusinessException("La división a actualizar no tiene ID.");
        }
        validate(d);
        dao.update(d);
    }

    public void delete(Long id) {
        dao.deleteById(id);
    }

    private void validate(Division d) {
        if (d.getName() == null || d.getName().trim().isEmpty()) {
            throw new BusinessException("El nombre de la división es obligatorio.");
        }
        if (d.getAgeMin() > d.getAgeMax()) {
            throw new BusinessException("El rango de edad es inválido (edad mínima mayor que máxima).");
        }
        if (d.getWeightMin() > d.getWeightMax()) {
            throw new BusinessException("El rango de peso es inválido (peso mínimo mayor que máximo).");
        }
        if (d.getGender() == null || d.getGender().isBlank()) {
            throw new BusinessException("El género de la división es obligatorio.");
        }
    }
}
