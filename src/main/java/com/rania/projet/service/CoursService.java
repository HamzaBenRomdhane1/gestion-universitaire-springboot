package com.rania.projet.service;

import com.rania.projet.exception.BadRequestException;
import com.rania.projet.exception.ResourceNotFoundException;
import com.rania.projet.model.Cours;
import com.rania.projet.model.Departement;
import com.rania.projet.model.Enseignant;
import com.rania.projet.model.Salle;
import com.rania.projet.repository.CoursRepository;
import com.rania.projet.repository.DepartementRepository;
import com.rania.projet.repository.EnseignantRepository;
import com.rania.projet.repository.SalleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CoursService {

    private final CoursRepository coursRepository;
    private final DepartementRepository departementRepository;
    private final EnseignantRepository enseignantRepository;
    private final SalleRepository salleRepository;

    public CoursService(CoursRepository coursRepository,
                        DepartementRepository departementRepository,
                        EnseignantRepository enseignantRepository,
                        SalleRepository salleRepository) {
        this.coursRepository = coursRepository;
        this.departementRepository = departementRepository;
        this.enseignantRepository = enseignantRepository;
        this.salleRepository = salleRepository;
    }

    @Transactional(readOnly = true)
    public List<Cours> getAllCours() {
        return coursRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cours getCoursById(Long id) {
        return coursRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable avec l'identifiant : " + id));
    }

    public Cours createCours(Cours cours) {
        if (coursRepository.existsByCode(cours.getCode())) {
            throw new BadRequestException("Un cours avec le code '" + cours.getCode() + "' existe déjà.");
        }

        if (cours.getDepartement() != null && cours.getDepartement().getId() != null) {
            Departement dep = departementRepository.findById(cours.getDepartement().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Département spécifié introuvable avec l'id : " + cours.getDepartement().getId()));
            cours.setDepartement(dep);
        }

        if (cours.getEnseignant() != null && cours.getEnseignant().getId() != null) {
            Enseignant ens = enseignantRepository.findById(cours.getEnseignant().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Enseignant spécifié introuvable avec l'id : " + cours.getEnseignant().getId()));
            cours.setEnseignant(ens);
        }

        return coursRepository.save(cours);
    }

    public Cours updateCours(Long id, Cours details) {
        Cours existant = getCoursById(id);

        if (!existant.getCode().equalsIgnoreCase(details.getCode()) && coursRepository.existsByCode(details.getCode())) {
            throw new BadRequestException("Un cours avec le code '" + details.getCode() + "' existe déjà.");
        }

        existant.setTitre(details.getTitre());
        existant.setCode(details.getCode());
        existant.setVolumeHoraire(details.getVolumeHoraire());
        existant.setCredits(details.getCredits());

        if (details.getDepartement() != null && details.getDepartement().getId() != null) {
            Departement dep = departementRepository.findById(details.getDepartement().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Département introuvable avec l'id : " + details.getDepartement().getId()));
            existant.setDepartement(dep);
        } else if (details.getDepartement() == null) {
            existant.setDepartement(null);
        }

        if (details.getEnseignant() != null && details.getEnseignant().getId() != null) {
            Enseignant ens = enseignantRepository.findById(details.getEnseignant().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Enseignant introuvable avec l'id : " + details.getEnseignant().getId()));
            existant.setEnseignant(ens);
        } else if (details.getEnseignant() == null) {
            existant.setEnseignant(null);
        }

        return coursRepository.save(existant);
    }

    public void deleteCours(Long id) {
        Cours cours = getCoursById(id);
        coursRepository.delete(cours);
    }

    public Cours addSalleToCours(Long coursId, Long salleId) {
        Cours cours = getCoursById(coursId);
        Salle salle = salleRepository.findById(salleId)
                .orElseThrow(() -> new ResourceNotFoundException("Salle introuvable avec l'identifiant : " + salleId));

        if (!cours.getSalles().contains(salle)) {
            cours.getSalles().add(salle);
        }
        return coursRepository.save(cours);
    }

    public Cours removeSalleFromCours(Long coursId, Long salleId) {
        Cours cours = getCoursById(coursId);
        Salle salle = salleRepository.findById(salleId)
                .orElseThrow(() -> new ResourceNotFoundException("Salle introuvable avec l'identifiant : " + salleId));

        cours.getSalles().remove(salle);
        return coursRepository.save(cours);
    }

    /**
     * Requête personnalisée 1 : Cours par département
     */
    @Transactional(readOnly = true)
    public List<Cours> getCoursByDepartement(Long departementId) {
        if (!departementRepository.existsById(departementId)) {
            throw new ResourceNotFoundException("Département introuvable avec l'identifiant : " + departementId);
        }
        return coursRepository.findByDepartementId(departementId);
    }

    /**
     * Requête personnalisée 2 : Cours par salle
     */
    @Transactional(readOnly = true)
    public List<Cours> getCoursBySalle(Long salleId) {
        if (!salleRepository.existsById(salleId)) {
            throw new ResourceNotFoundException("Salle introuvable avec l'identifiant : " + salleId);
        }
        return coursRepository.findBySalleId(salleId);
    }
}
