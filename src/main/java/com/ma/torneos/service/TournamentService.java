package com.ma.torneos.service;

import com.ma.torneos.config.JdbcConfig;
import com.ma.torneos.domain.Tournament;
import com.ma.torneos.service.exception.BusinessException;

import java.time.LocalDate;
import java.util.List;

public class TournamentService {

    private final TournamentDaoJdbcImpl dao = new TournamentDaoJdbcImpl();

    public List<Tournament> listAll() {
        return dao.findAll();
    }

    public Tournament create(Tournament t) {
        validateBusinessRules(t, true);
        return dao.insert(t);
    }

    public void update(Tournament t) {
        if (t.getId() == null) {
            throw new BusinessException("El torneo a actualizar no tiene ID.");
        }
        validateBusinessRules(t, false);
        dao.update(t);
    }

    public void delete(Long id) {
        // acá podrías chequear si el torneo tiene inscripciones, etc.
        dao.deleteById(id);
    }

    private void validateBusinessRules(Tournament t, boolean isNew) {
        if (t.getName() == null || t.getName().trim().isEmpty()) {
            throw new BusinessException("El nombre del torneo es obligatorio.");
        }
        if (t.getLocation() == null || t.getLocation().trim().isEmpty()) {
            throw new BusinessException("El lugar del torneo es obligatorio.");
        }
        if (t.getStartDate() == null) {
            throw new BusinessException("La fecha del torneo es obligatoria.");
        }

        // No permitir fechas pasadas
        LocalDate today = LocalDate.now();
        if (t.getStartDate().isBefore(today)) {
            throw new BusinessException("La fecha del torneo no puede ser anterior a hoy.");
        }

        // Unicidad nombre + fecha
        Long excludeId = isNew ? null : t.getId();
        boolean exists = dao.existsByNameAndDate(t.getName(), t.getStartDate(), excludeId);
        if (exists) {
            throw new BusinessException("Ya existe un torneo con el mismo nombre y fecha.");
        }
    }
}
