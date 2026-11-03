package com.rania.projet.repository;

import com.rania.projet.model.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    Optional<Etudiant> findByMatricule(String matricule);

    Optional<Etudiant> findByEmail(String email);

    boolean existsByMatricule(String matricule);

    boolean existsByEmail(String email);

    /**
     * Requête personnalisée 4 (Bonus) :
     * Récupère la liste des étudiants inscrits à un cours donné via l'association Inscription.
     * Besoin métier : Génération de la feuille d'émargement et du registre d'un cours.
     */
    @Query("SELECT i.etudiant FROM Inscription i WHERE i.cours.id = :coursId")
    List<Etudiant> findEtudiantsByCoursId(@Param("coursId") Long coursId);
}
