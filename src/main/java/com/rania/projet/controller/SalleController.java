package com.rania.projet.controller;

import com.rania.projet.model.Salle;
import com.rania.projet.service.SalleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salles")
@CrossOrigin(origins = "*")
public class SalleController {

    private final SalleService salleService;

    public SalleController(SalleService salleService) {
        this.salleService = salleService;
    }

    @GetMapping
    public ResponseEntity<List<Salle>> getAllSalles() {
        return ResponseEntity.ok(salleService.getAllSalles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Salle> getSalleById(@PathVariable Long id) {
        return ResponseEntity.ok(salleService.getSalleById(id));
    }

    @PostMapping
    public ResponseEntity<Salle> createSalle(@Valid @RequestBody Salle salle) {
        Salle cree = salleService.createSalle(salle);
        return new ResponseEntity<>(cree, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Salle> updateSalle(
            @PathVariable Long id,
            @Valid @RequestBody Salle salle) {
        Salle misAJour = salleService.updateSalle(id, salle);
        return ResponseEntity.ok(misAJour);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalle(@PathVariable Long id) {
        salleService.deleteSalle(id);
        return ResponseEntity.noContent().build();
    }
}
