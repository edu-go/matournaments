package com.ma.torneos.service;

import com.ma.torneos.domain.Competitor;
import com.ma.torneos.domain.School;
import com.ma.torneos.domain.Tournament;
import com.ma.torneos.service.exception.BusinessException;

import java.util.List;

public class RegistrationService {

    private final SchoolDaoJdbcImpl schoolDao = new SchoolDaoJdbcImpl();
    private final CompetitorDaoJdbcImpl competitorDao = new CompetitorDaoJdbcImpl();
    private final TournamentDaoJdbcImpl tournamentDao = new TournamentDaoJdbcImpl();
    private final RegistrationDaoJdbcImpl registrationDao = new RegistrationDaoJdbcImpl();

    public List<School> findSchoolsForOwner(Long ownerUserId) {
        return schoolDao.findByOwnerUserId(ownerUserId);
    }

    public List<Competitor> findCompetitorsBySchool(Long schoolId) {
        return competitorDao.findBySchoolId(schoolId);
    }

    public List<Tournament> findOpenTournaments() {
        return tournamentDao.findOpen();
    }

    public void register(Long ownerUserId, Long schoolId, Long competitorId, Long tournamentId) {
        // Validación 1: la escuela pertenece al owner
        boolean ownerHasSchool = schoolDao.findByOwnerUserId(ownerUserId)
                .stream().anyMatch(s -> s.getId().equals(schoolId));
        if (!ownerHasSchool) {
            throw new BusinessException("La escuela seleccionada no pertenece al usuario.");
        }

        // Validación 2: el competidor pertenece a esa escuela
        var competitorOpt = competitorDao.findById(competitorId);
        if (competitorOpt.isEmpty() || !competitorOpt.get().getSchoolId().equals(schoolId)) {
            throw new BusinessException("El competidor no pertenece a la escuela seleccionada.");
        }

        // Validación 3: torneo OPEN
        var tournamentOpt = tournamentDao.findById(tournamentId);
        if (tournamentOpt.isEmpty()) {
            throw new BusinessException("El torneo no existe.");
        }
        Tournament t = tournamentOpt.get();
        if (!"OPEN".equalsIgnoreCase(t.getStatus())) {
            throw new BusinessException("El torneo no admite nuevas inscripciones (no está en estado OPEN).");
        }

        // Validación 4: no duplicar inscripción
        if (registrationDao.existsRegistration(tournamentId, competitorId)) {
            throw new BusinessException("El competidor ya está inscripto en este torneo.");
        }

        // Alta
        registrationDao.insertRegistration(tournamentId, competitorId);
    }
}
