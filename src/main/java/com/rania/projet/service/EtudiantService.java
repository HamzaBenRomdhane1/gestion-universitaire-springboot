package com.rania.projet.service;

import com.rania.projet.exception.BadRequestException;
import com.rania.projet.exception.ResourceNotFoundException;
import com.rania.projet.model.Etudiant;
import com.rania.projet.repository.CoursRepository;
import com.rania.projet.repository.EtudiantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final CoursRepository coursRepository;

    public EtudiantService(EtudiantRepository etudiantRepository, CoursRepository coursRepository) {
        this.etudiantRepository = etudiantRepository;
        this.coursRepository = coursRepository;
    }

    @Transactional(readOnly = true)
    public List<Etudiant> getAllEtudiants() {
        return etudiantRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Etudiant getEtudiantById(Long id) {
        return etudiantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant introuvable avec l'identifiant : " + id));
    }

    public Etudiant createEtudiant(Etudiant etudiant) {
        if (etudiantRepository.existsByMatricule(etudiant.getMatricule())) {
            throw new BadRequestException("Un étudiant avec le matricule '" + etudiant.getMatricule() + "' existe déjà.");
        }
        if (etudiantRepository.existsByEmail(etudiant.getEmail())) {
            throw new BadRequestException("Un étudiant avec l'email '" + etudiant.getEmail() + "' existe déjà.");
        }
        return etudiantRepository.save(etudiant);
    }

    public Etudiant updateEtudiant(Long id, Etudiant details) {
        Etudiant existant = getEtudiantById(id);

        if (!existant.getMatricule().equalsIgnoreCase(details.getMatricule()) &&
                etudiantRepository.existsByMatricule(details.getMatricule())) {
            throw new BadRequestException("Un étudiant avec le matricule '" + details.getMatricule() + "' existe déjà.");
        }

        if (!existant.getEmail().equalsIgnoreCase(details.getEmail()) &&
                etudiantRepository.existsByEmail(details.getEmail())) {
            throw new BadRequestException("Un étudiant avec l'email '" + details.getEmail() + "' existe déjà.");
        }

        existant.setMatricule(details.getMatricule());
        existant.setNom(details.getNom());
        existant.setPrenom(details.getPrenom());
        existant.setEmail(details.getEmail());
        existant.setDateNaissance(details.getDateNaissance());

        return etudiantRepository.save(existant);
    }

    public void deleteEtudiant(Long id) {
        Etudiant etudiant = getEtudiantById(id);
        etudiantRepository.delete(etudiant);
    }

    /**
     * Requête personnalisée 4 (Bonus) : Récupère les étudiants inscrits à un cours donné.
     */
    @Transactional(readOnly = true)
    public List<Etudiant> getEtudiantsByCours(Long coursId) {
        if (!coursRepository.existsById(coursId)) {
            throw new ResourceNotFoundException("Cours introuvable avec l'identifiant : " + coursId);
        }
        return etudiantRepository.findEtudiantsByCoursId(coursId);
    }
}
