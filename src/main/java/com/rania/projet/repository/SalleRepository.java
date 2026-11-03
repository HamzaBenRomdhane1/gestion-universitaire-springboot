package com.rania.projet.repository;

import com.rania.projet.model.Salle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalleRepository extends JpaRepository<Salle, Long> {

    Optional<Salle> findByNumero(String numero);

    boolean existsByNumero(String numero);

    List<Salle> findByType(String type);
}
