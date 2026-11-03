package com.rania.projet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un Étudiant inscrit à l'établissement.
 */
@Entity
@Table(name = "etudiants")
public class Etudiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le matricule est obligatoire")
    @Size(min = 3, max = 30, message = "Le matricule doit comporter entre 3 et 30 caractères")
    @Column(nullable = false, unique = true, length = 30)
    private String matricule;

    @NotBlank(message = "Le nom de l'étudiant est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit comporter entre 2 et 50 caractères")
    @Column(nullable = false)
    private String nom;

    @NotBlank(message = "Le prénom de l'étudiant est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit comporter entre 2 et 50 caractères")
    @Column(nullable = false)
    private String prenom;

    @NotBlank(message = "L'adresse email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Column(nullable = false, unique = true)
    private String email;

    @Past(message = "La date de naissance doit être dans le passé")
    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("etudiant")
    private List<Inscription> inscriptions = new ArrayList<>();

    public Etudiant() {
    }

    public Etudiant(Long id, String matricule, String nom, String prenom, String email, LocalDate dateNaissance) {
        this.id = id;
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.dateNaissance = dateNaissance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public List<Inscription> getInscriptions() {
        return inscriptions;
    }

    public void setInscriptions(List<Inscription> inscriptions) {
        this.inscriptions = inscriptions;
    }
}
