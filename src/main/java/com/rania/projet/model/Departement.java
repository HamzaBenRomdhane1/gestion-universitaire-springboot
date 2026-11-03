package com.rania.projet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un Département universitaire (ex: Informatique, Mathématiques).
 */
@Entity
@Table(name = "departements")
public class Departement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du département est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit comporter entre 2 et 100 caractères")
    @Column(nullable = false)
    private String nom;

    @NotBlank(message = "Le code du département est obligatoire")
    @Size(min = 2, max = 20, message = "Le code doit comporter entre 2 et 20 caractères")
    @Column(nullable = false, unique = true)
    private String code;

    @NotBlank(message = "La localisation est obligatoire")
    @Column(nullable = false)
    private String localisation;

    @OneToMany(mappedBy = "departement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"departement", "cours"})
    private List<Enseignant> enseignants = new ArrayList<>();

    @OneToMany(mappedBy = "departement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"departement", "enseignant", "salles", "inscriptions"})
    private List<Cours> cours = new ArrayList<>();

    public Departement() {
    }

    public Departement(Long id, String nom, String code, String localisation) {
        this.id = id;
        this.nom = nom;
        this.code = code;
        this.localisation = localisation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public List<Enseignant> getEnseignants() {
        return enseignants;
    }

    public void setEnseignants(List<Enseignant> enseignants) {
        this.enseignants = enseignants;
    }

    public List<Cours> getCours() {
        return cours;
    }

    public void setCours(List<Cours> cours) {
        this.cours = cours;
    }
}
