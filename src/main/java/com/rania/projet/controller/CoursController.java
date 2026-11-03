package com.rania.projet.controller;

import com.rania.projet.model.Cours;
import com.rania.projet.service.CoursService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cours")
@CrossOrigin(origins = "*")
public class CoursController {

    private final CoursService coursService;

    public CoursController(CoursService coursService) {
        this.coursService = coursService;
    }

    @GetMapping
    public ResponseEntity<List<Cours>> getAllCours() {
        return ResponseEntity.ok(coursService.getAllCours());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cours> getCoursById(@PathVariable Long id) {
        return ResponseEntity.ok(coursService.getCoursById(id));
    }

    /**
     * Endpoint Requête Personnalisée 1 :
     * GET /api/cours/departement/{departementId}
     * Retourne les cours d'un département donné.
     */
    @GetMapping("/departement/{departementId}")
    public ResponseEntity<List<Cours>> getCoursByDepartement(@PathVariable Long departementId) {
        return ResponseEntity.ok(coursService.getCoursByDepartement(departementId));
    }

    /**
     * Endpoint Requête Personnalisée 2 :
     * GET /api/cours/salle/{salleId}
     * Retourne les cours programmés dans une salle donnée.
     */
    @GetMapping("/salle/{salleId}")
    public ResponseEntity<List<Cours>> getCoursBySalle(@PathVariable Long salleId) {
        return ResponseEntity.ok(coursService.getCoursBySalle(salleId));
    }

    @PostMapping
    public ResponseEntity<Cours> createCours(@Valid @RequestBody Cours cours) {
        Cours cree = coursService.createCours(cours);
        return new ResponseEntity<>(cree, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cours> updateCours(
            @PathVariable Long id,
            @Valid @RequestBody Cours cours) {
        Cours misAJour = coursService.updateCours(id, cours);
        return ResponseEntity.ok(misAJour);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCours(@PathVariable Long id) {
        coursService.deleteCours(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint d'association ManyToMany : Affecter une salle à un cours.
     */
    @PostMapping("/{coursId}/salles/{salleId}")
    public ResponseEntity<Cours> addSalleToCours(@PathVariable Long coursId, @PathVariable Long salleId) {
        Cours cours = coursService.addSalleToCours(coursId, salleId);
        return ResponseEntity.ok(cours);
    }

    /**
     * Endpoint d'association ManyToMany : Retirer une salle d'un cours.
     */
    @DeleteMapping("/{coursId}/salles/{salleId}")
    public ResponseEntity<Cours> removeSalleFromCours(@PathVariable Long coursId, @PathVariable Long salleId) {
        Cours cours = coursService.removeSalleFromCours(coursId, salleId);
        return ResponseEntity.ok(cours);
    }
}
