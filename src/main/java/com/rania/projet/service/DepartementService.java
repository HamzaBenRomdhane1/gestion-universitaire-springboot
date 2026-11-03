package com.rania.projet.service;

import com.rania.projet.exception.BadRequestException;
import com.rania.projet.exception.ResourceNotFoundException;
import com.rania.projet.model.Departement;
import com.rania.projet.repository.DepartementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DepartementService {

    private final DepartementRepository departementRepository;

    public DepartementService(DepartementRepository departementRepository) {
        this.departementRepository = departementRepository;
    }

    @Transactional(readOnly = true)
    public List<Departement> getAllDepartements() {
        return departementRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Departement getDepartementById(Long id) {
        return departementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département introuvable avec l'identifiant : " + id));
    }

    public Departement createDepartement(Departement departement) {
        if (departementRepository.existsByCode(departement.getCode())) {
            throw new BadRequestException("Un département avec le code '" + departement.getCode() + "' existe déjà.");
        }
        return departementRepository.save(departement);
    }

    public Departement updateDepartement(Long id, Departement details) {
        Departement existant = getDepartementById(id);

        if (!existant.getCode().equals(details.getCode()) && departementRepository.existsByCode(details.getCode())) {
            throw new BadRequestException("Un département avec le code '" + details.getCode() + "' existe déjà.");
        }

        existant.setNom(details.getNom());
        existant.setCode(details.getCode());
        existant.setLocalisation(details.getLocalisation());

        return departementRepository.save(existant);
    }

    public void deleteDepartement(Long id) {
        Departement departement = getDepartementById(id);
        departementRepository.delete(departement);
    }
}
