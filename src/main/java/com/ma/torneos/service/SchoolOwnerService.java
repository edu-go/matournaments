package com.ma.torneos.service;

import com.ma.torneos.domain.Role;
import com.ma.torneos.domain.School;
import com.ma.torneos.domain.User;
import com.ma.torneos.service.exception.BusinessException;

import java.util.List;
import java.util.Optional;

public class SchoolOwnerService {

    private final SchoolDaoJdbcImpl schoolDao = new SchoolDaoJdbcImpl();
    private final UserDaoJdbcImpl userDao = new UserDaoJdbcImpl();

    public List<School> listAllSchools() {
        return schoolDao.findAll();
    }

    public List<User> listAllOwners() {
        return userDao.findOwners();
    }

    public void assignOwnerToSchool(Long schoolId, Long ownerUserId) {
        if (schoolId == null) {
            throw new BusinessException("Debe seleccionar una escuela.");
        }
        if (ownerUserId == null) {
            // Podrías permitir desasociar: ownerUserId = null
            // acá elijo no permitirlo (puedes cambiarlo):
            throw new BusinessException("Debe seleccionar un usuario dueño.");
        }

        // Validación adicional: el usuario realmente es OWNER
        Optional<User> ownerOpt = userDao.findOwners().stream()
                .filter(u -> u.getId().equals(ownerUserId))
                .findFirst();

        if (ownerOpt.isEmpty() || ownerOpt.get().getRole() != Role.OWNER) {
            throw new BusinessException("El usuario seleccionado no es un OWNER válido.");
        }

        schoolDao.updateOwner(schoolId, ownerUserId);
    }

    public School createSchool(Long ownerUserId, String name, String city, String contactPhone) {
        if (ownerUserId == null) {
            throw new BusinessException("Debe seleccionar un usuario dueño para la nueva escuela.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("El nombre de la escuela es obligatorio.");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new BusinessException("La ciudad es obligatoria.");
        }

        // Crear escuela ya asociada al OWNER
        School s = new School(
                null,
                ownerUserId,
                name.trim(),
                city.trim(),
                contactPhone != null ? contactPhone.trim() : null
        );

        return schoolDao.insert(s);
    }
}
