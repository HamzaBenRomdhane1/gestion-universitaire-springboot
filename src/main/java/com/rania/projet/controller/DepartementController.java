package com.rania.projet.controller;

import com.rania.projet.model.Departement;
import com.rania.projet.service.DepartementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departements")
@CrossOrigin(origins = "*")
public class DepartementController {

    private final DepartementService departementService;

    public DepartementController(DepartementService departementService) {
        this.departementService = departementService;
    }

    @GetMapping
    public ResponseEntity<List<Departement>> getAllDepartements() {
        return ResponseEntity.ok(departementService.getAllDepartements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Departement> getDepartementById(@PathVariable Long id) {
        return ResponseEntity.ok(departementService.getDepartementById(id));
    }

    @PostMapping
    public ResponseEntity<Departement> createDepartement(@Valid @RequestBody Departement departement) {
        Departement cree = departementService.createDepartement(departement);
        return new ResponseEntity<>(cree, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Departement> updateDepartement(
            @PathVariable Long id,
            @Valid @RequestBody Departement departement) {
        Departement misAJour = departementService.updateDepartement(id, departement);
        return ResponseEntity.ok(misAJour);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartement(@PathVariable Long id) {
        departementService.deleteDepartement(id);
        return ResponseEntity.noContent().build();
    }
}
