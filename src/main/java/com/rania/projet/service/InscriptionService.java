package com.rania.projet.service;

import com.rania.projet.exception.BadRequestException;
import com.rania.projet.exception.ResourceNotFoundException;
import com.rania.projet.model.Cours;
import com.rania.projet.model.Etudiant;
import com.rania.projet.model.Inscription;
import com.rania.projet.model.StatutInscription;
import com.rania.projet.repository.CoursRepository;
import com.rania.projet.repository.EtudiantRepository;
import com.rania.projet.repository.InscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final EtudiantRepository etudiantRepository;
    private final CoursRepository coursRepository;

    public InscriptionService(InscriptionRepository inscriptionRepository,
                              EtudiantRepository etudiantRepository,
                              CoursRepository coursRepository) {
        this.inscriptionRepository = inscriptionRepository;
        this.etudiantRepository = etudiantRepository;
        this.coursRepository = coursRepository;
    }

    @Transactional(readOnly = true)
    public List<Inscription> getAllInscriptions() {
        return inscriptionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Inscription getInscriptionById(Long id) {
        return inscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription introuvable avec l'identifiant : " + id));
    }

    public Inscription createInscription(Inscription inscription) {
        if (inscription.getEtudiant() == null || inscription.getEtudiant().getId() == null) {
            throw new BadRequestException("L'identifiant de l'étudiant est obligatoire pour créer une inscription.");
        }
        if (inscription.getCours() == null || inscription.getCours().getId() == null) {
            throw new BadRequestException("L'identifiant du cours est obligatoire pour créer une inscription.");
        }

        Etudiant etudiant = etudiantRepository.findById(inscription.getEtudiant().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant introuvable avec l'identifiant : " + inscription.getEtudiant().getId()));

        Cours cours = coursRepository.findById(inscription.getCours().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable avec l'identifiant : " + inscription.getCours().getId()));

        inscription.setEtudiant(etudiant);
        inscription.setCours(cours);

        return inscriptionRepository.save(inscription);
    }

    public Inscription updateInscription(Long id, Inscription details) {
        Inscription existante = getInscriptionById(id);

        if (details.getEtudiant() != null && details.getEtudiant().getId() != null) {
            Etudiant etudiant = etudiantRepository.findById(details.getEtudiant().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Étudiant introuvable avec l'identifiant : " + details.getEtudiant().getId()));
            existante.setEtudiant(etudiant);
        }

        if (details.getCours() != null && details.getCours().getId() != null) {
            Cours cours = coursRepository.findById(details.getCours().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable avec l'identifiant : " + details.getCours().getId()));
            existante.setCours(cours);
        }

        existante.setDateInscription(details.getDateInscription());
        existante.setNote(details.getNote());
        existante.setStatut(details.getStatut());

        return inscriptionRepository.save(existante);
    }

    public void deleteInscription(Long id) {
        Inscription inscription = getInscriptionById(id);
        inscriptionRepository.delete(inscription);
    }

    @Transactional(readOnly = true)
    public List<Inscription> getInscriptionsByEtudiant(Long etudiantId) {
        if (!etudiantRepository.existsById(etudiantId)) {
            throw new ResourceNotFoundException("Étudiant introuvable avec l'identifiant : " + etudiantId);
        }
        return inscriptionRepository.findByEtudiantId(etudiantId);
    }

    @Transactional(readOnly = true)
    public List<Inscription> getInscriptionsByCours(Long coursId) {
        if (!coursRepository.existsById(coursId)) {
            throw new ResourceNotFoundException("Cours introuvable avec l'identifiant : " + coursId);
        }
        return inscriptionRepository.findByCoursId(coursId);
    }

    /**
     * Requête personnalisée 3 : Recherche par statut et note minimale.
     */
    @Transactional(readOnly = true)
    public List<Inscription> getInscriptionsByStatutAndMinNote(StatutInscription statut, Double minNote) {
        if (statut == null) {
            throw new BadRequestException("Le paramètre de statut est obligatoire.");
        }
        if (minNote == null) {
            minNote = 0.0;
        }
        return inscriptionRepository.findByStatutAndNoteGreaterThanEqual(statut, minNote);
    }
}
