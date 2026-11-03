package com.rania.projet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un Cours / Module d'enseignement.
 */
@Entity
@Table(name = "cours")
public class Cours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre du cours est obligatoire")
    @Column(nullable = false, length = 150)
    private String titre;

    @NotBlank(message = "Le code du cours est obligatoire")
    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @NotNull(message = "Le volume horaire est obligatoire")
    @Positive(message = "Le volume horaire doit être positif")
    @Column(name = "volume_horaire", nullable = false)
    private Integer volumeHoraire;

    @NotNull(message = "Le nombre de crédits est obligatoire")
    @Min(value = 1, message = "Le cours doit attribuer au moins 1 crédit")
    @Column(nullable = false)
    private Integer credits;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "departement_id")
    @JsonIgnoreProperties({"cours", "enseignants"})
    private Departement departement;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "enseignant_id")
    @JsonIgnoreProperties({"cours", "departement"})
    private Enseignant enseignant;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "cours_salles",
            joinColumns = @JoinColumn(name = "cours_id"),
            inverseJoinColumns = @JoinColumn(name = "salle_id")
    )
    @JsonIgnoreProperties("cours")
    private List<Salle> salles = new ArrayList<>();

    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"cours"})
    private List<Inscription> inscriptions = new ArrayList<>();

    public Cours() {
    }

    public Cours(Long id, String titre, String code, Integer volumeHoraire, Integer credits, Departement departement, Enseignant enseignant) {
        this.id = id;
        this.titre = titre;
        this.code = code;
        this.volumeHoraire = volumeHoraire;
        this.credits = credits;
        this.departement = departement;
        this.enseignant = enseignant;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getVolumeHoraire() {
        return volumeHoraire;
    }

    public void setVolumeHoraire(Integer volumeHoraire) {
        this.volumeHoraire = volumeHoraire;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    public Enseignant getEnseignant() {
        return enseignant;
    }

    public void setEnseignant(Enseignant enseignant) {
        this.enseignant = enseignant;
    }

    public List<Salle> getSalles() {
        return salles;
    }

    public void setSalles(List<Salle> salles) {
        this.salles = salles;
    }

    public List<Inscription> getInscriptions() {
        return inscriptions;
    }

    public void setInscriptions(List<Inscription> inscriptions) {
        this.inscriptions = inscriptions;
    }
}
