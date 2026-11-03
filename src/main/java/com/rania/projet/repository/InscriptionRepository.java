package com.rania.projet.repository;

import com.rania.projet.model.Inscription;
import com.rania.projet.model.StatutInscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

    List<Inscription> findByEtudiantId(Long etudiantId);

    List<Inscription> findByCoursId(Long coursId);

    /**
     * Requête personnalisée 3 :
     * Sélectionne toutes les inscriptions filtrées par leur statut et ayant une note supérieure ou égale au seuil donné.
     * Besoin métier : Attribution des mentions, bourses d'excellence et validation officielle des modules réussis.
     */
    @Query("SELECT i FROM Inscription i WHERE i.statut = :statut AND i.note >= :minNote")
    List<Inscription> findByStatutAndNoteGreaterThanEqual(
            @Param("statut") StatutInscription statut,
            @Param("minNote") Double minNote
    );
}
