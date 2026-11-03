package com.rania.projet.service;

import com.rania.projet.exception.BadRequestException;
import com.rania.projet.exception.ResourceNotFoundException;
import com.rania.projet.model.Salle;
import com.rania.projet.repository.SalleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SalleService {

    private final SalleRepository salleRepository;

    public SalleService(SalleRepository salleRepository) {
        this.salleRepository = salleRepository;
    }

    @Transactional(readOnly = true)
    public List<Salle> getAllSalles() {
        return salleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Salle getSalleById(Long id) {
        return salleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salle introuvable avec l'identifiant : " + id));
    }

    public Salle createSalle(Salle salle) {
        if (salleRepository.existsByNumero(salle.getNumero())) {
            throw new BadRequestException("Une salle avec le numéro '" + salle.getNumero() + "' existe déjà.");
        }
        return salleRepository.save(salle);
    }

    public Salle updateSalle(Long id, Salle details) {
        Salle existante = getSalleById(id);

        if (!existante.getNumero().equalsIgnoreCase(details.getNumero()) && salleRepository.existsByNumero(details.getNumero())) {
            throw new BadRequestException("Une salle avec le numéro '" + details.getNumero() + "' existe déjà.");
        }

        existante.setNumero(details.getNumero());
        existante.setCapacite(details.getCapacite());
        existante.setType(details.getType());

        return salleRepository.save(existante);
    }

    public void deleteSalle(Long id) {
        Salle salle = getSalleById(id);
        salleRepository.delete(salle);
    }
}
