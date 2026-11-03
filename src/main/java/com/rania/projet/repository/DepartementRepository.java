package com.rania.projet.repository;

import com.rania.projet.model.Departement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartementRepository extends JpaRepository<Departement, Long> {

    Optional<Departement> findByCode(String code);

    boolean existsByCode(String code);
}
