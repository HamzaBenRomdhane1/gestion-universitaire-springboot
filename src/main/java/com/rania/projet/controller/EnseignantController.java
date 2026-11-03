package com.rania.projet.controller;

import com.rania.projet.model.Enseignant;
import com.rania.projet.service.EnseignantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enseignants")
@CrossOrigin(origins = "*")
public class EnseignantController {

    private final EnseignantService enseignantService;

    public EnseignantController(EnseignantService enseignantService) {
        this.enseignantService = enseignantService;
    }

    @GetMapping
    public ResponseEntity<List<Enseignant>> getAllEnseignants() {
        return ResponseEntity.ok(enseignantService.getAllEnseignants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Enseignant> getEnseignantById(@PathVariable Long id) {
        return ResponseEntity.ok(enseignantService.getEnseignantById(id));
    }

    @GetMapping("/departement/{departementId}")
    public ResponseEntity<List<Enseignant>> getEnseignantsByDepartement(@PathVariable Long departementId) {
        return ResponseEntity.ok(enseignantService.getEnseignantsByDepartement(departementId));
    }

    @PostMapping
    public ResponseEntity<Enseignant> createEnseignant(@Valid @RequestBody Enseignant enseignant) {
        Enseignant cree = enseignantService.createEnseignant(enseignant);
        return new ResponseEntity<>(cree, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Enseignant> updateEnseignant(
            @PathVariable Long id,
            @Valid @RequestBody Enseignant enseignant) {
        Enseignant misAJour = enseignantService.updateEnseignant(id, enseignant);
        return ResponseEntity.ok(misAJour);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnseignant(@PathVariable Long id) {
        enseignantService.deleteEnseignant(id);
        return ResponseEntity.noContent().build();
    }
}
