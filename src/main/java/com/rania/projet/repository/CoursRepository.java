package com.rania.projet.repository;

import com.rania.projet.model.Cours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoursRepository extends JpaRepository<Cours, Long> {

    Optional<Cours> findByCode(String code);

    boolean existsByCode(String code);

    /**
     * Requête personnalisée 1 :
     * Sélectionne tous les cours rattachés à un département pédagogique spécifique.
     * Besoin métier : Consultation du catalogue de cours par département.
     */
    @Query("SELECT c FROM Cours c WHERE c.departement.id = :departementId")
    List<Cours> findByDepartementId(@Param("departementId") Long departementId);

    /**
     * Requête personnalisée 2 :
     * Sélectionne tous les cours programmés dans une salle donnée via la jointure @ManyToMany.
     * Besoin métier : Gestion des plannings d'occupation des salles de cours et laboratoires.
     */
    @Query("SELECT c FROM Cours c JOIN c.salles s WHERE s.id = :salleId")
    List<Cours> findBySalleId(@Param("salleId") Long salleId);
}
