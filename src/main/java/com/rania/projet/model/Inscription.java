package com.rania.projet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Entité représentant l'Inscription d'un étudiant à un cours (avec statut et note).
 */
@Entity
@Table(name = "inscriptions")
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "L'étudiant est obligatoire")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "etudiant_id", nullable = false)
    @JsonIgnoreProperties({"inscriptions"})
    private Etudiant etudiant;

    @NotNull(message = "Le cours est obligatoire")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cours_id", nullable = false)
    @JsonIgnoreProperties({"inscriptions", "salles"})
    private Cours cours;

    @NotNull(message = "La date d'inscription est obligatoire")
    @Column(name = "date_inscription", nullable = false)
    private LocalDate dateInscription;

    @Min(value = 0, message = "La note minimale est 0")
    @Max(value = 20, message = "La note maximale est 20")
    @Column
    private Double note;

    @NotNull(message = "Le statut de l'inscription est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutInscription statut;

    public Inscription() {
    }

    public Inscription(Long id, Etudiant etudiant, Cours cours, LocalDate dateInscription, Double note, StatutInscription statut) {
        this.id = id;
        this.etudiant = etudiant;
        this.cours = cours;
        this.dateInscription = dateInscription;
        this.note = note;
        this.statut = statut;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Etudiant getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(Etudiant etudiant) {
        this.etudiant = etudiant;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    public Double getNote() {
        return note;
    }

    public void setNote(Double note) {
        this.note = note;
    }

    public StatutInscription getStatut() {
        return statut;
    }

    public void setStatut(StatutInscription statut) {
        this.statut = statut;
    }
}
