package com.rania.projet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une Salle de cours / laboratoire / amphithéâtre.
 */
@Entity
@Table(name = "salles")
public class Salle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le numéro de salle est obligatoire")
    @Column(nullable = false, unique = true, length = 30)
    private String numero;

    @NotNull(message = "La capacité est obligatoire")
    @Min(value = 1, message = "La capacité minimale est de 1 place")
    @Column(nullable = false)
    private Integer capacite;

    @NotBlank(message = "Le type de salle est obligatoire (ex: TP, TD, Amphi)")
    @Column(nullable = false, length = 50)
    private String type;

    @ManyToMany(mappedBy = "salles", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"salles", "inscriptions"})
    private List<Cours> cours = new ArrayList<>();

    public Salle() {
    }

    public Salle(Long id, String numero, Integer capacite, String type) {
        this.id = id;
        this.numero = numero;
        this.capacite = capacite;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Integer getCapacite() {
        return capacite;
    }

    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Cours> getCours() {
        return cours;
    }

    public void setCours(List<Cours> cours) {
        this.cours = cours;
    }
}
