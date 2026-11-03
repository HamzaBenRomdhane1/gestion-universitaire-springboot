package com.rania.projet.controller;

import com.rania.projet.model.Inscription;
import com.rania.projet.model.StatutInscription;
import com.rania.projet.service.InscriptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscriptions")
@CrossOrigin(origins = "*")
public class InscriptionController {

    private final InscriptionService inscriptionService;

    public InscriptionController(InscriptionService inscriptionService) {
        this.inscriptionService = inscriptionService;
    }

    @GetMapping
    public ResponseEntity<List<Inscription>> getAllInscriptions() {
        return ResponseEntity.ok(inscriptionService.getAllInscriptions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inscription> getInscriptionById(@PathVariable Long id) {
        return ResponseEntity.ok(inscriptionService.getInscriptionById(id));
    }

    @GetMapping("/etudiant/{etudiantId}")
    public ResponseEntity<List<Inscription>> getInscriptionsByEtudiant(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(inscriptionService.getInscriptionsByEtudiant(etudiantId));
    }

    @GetMapping("/cours/{coursId}")
    public ResponseEntity<List<Inscription>> getInscriptionsByCours(@PathVariable Long coursId) {
        return ResponseEntity.ok(inscriptionService.getInscriptionsByCours(coursId));
    }

    /**
     * Endpoint Requête Personnalisée 3 :
     * GET /api/inscriptions/recherche?statut=VALIDE&minNote=10.0
     * Retourne les inscriptions avec statut spécifique et note >= seuil.
     */
    @GetMapping("/recherche")
    public ResponseEntity<List<Inscription>> getInscriptionsByStatutAndMinNote(
            @RequestParam StatutInscription statut,
            @RequestParam(required = false, defaultValue = "0.0") Double minNote) {
        return ResponseEntity.ok(inscriptionService.getInscriptionsByStatutAndMinNote(statut, minNote));
    }

    @PostMapping
    public ResponseEntity<Inscription> createInscription(@Valid @RequestBody Inscription inscription) {
        Inscription cree = inscriptionService.createInscription(inscription);
        return new ResponseEntity<>(cree, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inscription> updateInscription(
            @PathVariable Long id,
            @Valid @RequestBody Inscription inscription) {
        Inscription misAJour = inscriptionService.updateInscription(id, inscription);
        return ResponseEntity.ok(misAJour);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInscription(@PathVariable Long id) {
        inscriptionService.deleteInscription(id);
        return ResponseEntity.noContent().build();
    }
}
