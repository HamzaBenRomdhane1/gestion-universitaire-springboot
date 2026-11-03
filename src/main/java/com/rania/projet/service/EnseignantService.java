package com.rania.projet.service;

import com.rania.projet.exception.BadRequestException;
import com.rania.projet.exception.ResourceNotFoundException;
import com.rania.projet.model.Departement;
import com.rania.projet.model.Enseignant;
import com.rania.projet.repository.DepartementRepository;
import com.rania.projet.repository.EnseignantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EnseignantService {

    private final EnseignantRepository enseignantRepository;
    private final DepartementRepository departementRepository;

    public EnseignantService(EnseignantRepository enseignantRepository, DepartementRepository departementRepository) {
        this.enseignantRepository = enseignantRepository;
        this.departementRepository = departementRepository;
    }

    @Transactional(readOnly = true)
    public List<Enseignant> getAllEnseignants() {
        return enseignantRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Enseignant getEnseignantById(Long id) {
        return enseignantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant introuvable avec l'identifiant : " + id));
    }

    public Enseignant createEnseignant(Enseignant enseignant) {
        if (enseignantRepository.existsByEmail(enseignant.getEmail())) {
            throw new BadRequestException("Un enseignant avec l'email '" + enseignant.getEmail() + "' existe déjà.");
        }

        if (enseignant.getDepartement() != null && enseignant.getDepartement().getId() != null) {
            Departement dep = departementRepository.findById(enseignant.getDepartement().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Département spécifié introuvable avec l'id : " + enseignant.getDepartement().getId()));
            enseignant.setDepartement(dep);
        }

        return enseignantRepository.save(enseignant);
    }

    public Enseignant updateEnseignant(Long id, Enseignant details) {
        Enseignant existant = getEnseignantById(id);

        if (!existant.getEmail().equalsIgnoreCase(details.getEmail()) && enseignantRepository.existsByEmail(details.getEmail())) {
            throw new BadRequestException("Un enseignant avec l'email '" + details.getEmail() + "' existe déjà.");
        }

        existant.setNom(details.getNom());
        existant.setPrenom(details.getPrenom());
        existant.setEmail(details.getEmail());
        existant.setSpecialite(details.getSpecialite());
        existant.setTelephone(details.getTelephone());

        if (details.getDepartement() != null && details.getDepartement().getId() != null) {
            Departement dep = departementRepository.findById(details.getDepartement().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Département spécifié introuvable avec l'id : " + details.getDepartement().getId()));
            existant.setDepartement(dep);
        } else if (details.getDepartement() == null) {
            existant.setDepartement(null);
        }

        return enseignantRepository.save(existant);
    }

    public void deleteEnseignant(Long id) {
        Enseignant enseignant = getEnseignantById(id);
        enseignantRepository.delete(enseignant);
    }

    @Transactional(readOnly = true)
    public List<Enseignant> getEnseignantsByDepartement(Long departementId) {
        if (!departementRepository.existsById(departementId)) {
            throw new ResourceNotFoundException("Département introuvable avec l'id : " + departementId);
        }
        return enseignantRepository.findByDepartementId(departementId);
    }
}
