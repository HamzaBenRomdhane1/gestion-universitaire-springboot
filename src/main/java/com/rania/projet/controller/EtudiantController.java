package com.rania.projet.controller;

import com.rania.projet.model.Etudiant;
import com.rania.projet.service.EtudiantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
@CrossOrigin(origins = "*")
public class EtudiantController {

    private final EtudiantService etudiantService;

    public EtudiantController(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }

    @GetMapping
    public ResponseEntity<List<Etudiant>> getAllEtudiants() {
        return ResponseEntity.ok(etudiantService.getAllEtudiants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Etudiant> getEtudiantById(@PathVariable Long id) {
        return ResponseEntity.ok(etudiantService.getEtudiantById(id));
    }

    /**
     * Endpoint Requête Personnalisée 4 (Bonus) :
     * GET /api/etudiants/cours/{coursId}
     * Retourne la liste des étudiants inscrits à un cours donné.
     */
    @GetMapping("/cours/{coursId}")
    public ResponseEntity<List<Etudiant>> getEtudiantsByCours(@PathVariable Long coursId) {
        return ResponseEntity.ok(etudiantService.getEtudiantsByCours(coursId));
    }

    @PostMapping
    public ResponseEntity<Etudiant> createEtudiant(@Valid @RequestBody Etudiant etudiant) {
        Etudiant cree = etudiantService.createEtudiant(etudiant);
        return new ResponseEntity<>(cree, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Etudiant> updateEtudiant(
            @PathVariable Long id,
            @Valid @RequestBody Etudiant etudiant) {
        Etudiant misAJour = etudiantService.updateEtudiant(id, etudiant);
        return ResponseEntity.ok(misAJour);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEtudiant(@PathVariable Long id) {
        etudiantService.deleteEtudiant(id);
        return ResponseEntity.noContent().build();
    }
}
