package com.rania.projet;

import com.rania.projet.model.*;
import com.rania.projet.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class ProjetApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjetApplication.class, args);
    }

    /**
     * Initialisation d'un jeu de données initial cohérent pour démonstration et tests immédiats.
     */
    @Bean
    public CommandLineRunner initDatabase(
            DepartementRepository departementRepository,
            EnseignantRepository enseignantRepository,
            SalleRepository salleRepository,
            CoursRepository coursRepository,
            EtudiantRepository etudiantRepository,
            InscriptionRepository inscriptionRepository) {
        return args -> {
            if (departementRepository.count() == 0) {
                // 1. Départements
                Departement depInfo = new Departement(null, "Informatique & Multimédia", "INFO", "Bâtiment A - 2ème étage");
                Departement depMath = new Departement(null, "Mathématiques & Modélisation", "MATH", "Bâtiment B - 1er étage");
                departementRepository.saveAll(List.of(depInfo, depMath));

                // 2. Enseignants
                Enseignant ens1 = new Enseignant(null, "Ben Salah", "Mohamed", "mohamed.bensalah@ecole.tn", "Génie Logiciel", "+216 22 111 222", depInfo);
                Enseignant ens2 = new Enseignant(null, "Trabelsi", "Amira", "amira.trabelsi@ecole.tn", "Bases de Données & IA", "+216 98 333 444", depInfo);
                Enseignant ens3 = new Enseignant(null, "Gharbi", "Karim", "karim.gharbi@ecole.tn", "Analyse Numérique", "+216 55 555 666", depMath);
                enseignantRepository.saveAll(List.of(ens1, ens2, ens3));

                // 3. Salles
                Salle s1 = new Salle(null, "Amphi-A", 150, "Amphithéâtre");
                Salle s2 = new Salle(null, "Lab-101", 30, "Laboratoire TP");
                Salle s3 = new Salle(null, "Salle-204", 40, "Salle de cours TD");
                salleRepository.saveAll(List.of(s1, s2, s3));

                // 4. Cours
                Cours c1 = new Cours(null, "Architecture JEE et Spring Boot", "CS-JEE-301", 42, 5, depInfo, ens1);
                Cours c2 = new Cours(null, "Bases de Données Avancées", "CS-BDD-202", 36, 4, depInfo, ens2);
                Cours c3 = new Cours(null, "Probabilités & Statistiques", "MATH-STAT-101", 30, 3, depMath, ens3);

                c1.getSalles().add(s1);
                c1.getSalles().add(s2);
                c2.getSalles().add(s2);
                c3.getSalles().add(s3);
                coursRepository.saveAll(List.of(c1, c2, c3));

                // 5. Étudiants
                Etudiant et1 = new Etudiant(null, "ETU-2026-001", "Soltani", "Hamza", "hamza.soltani@etudiant.tn", LocalDate.of(2001, 5, 14));
                Etudiant et2 = new Etudiant(null, "ETU-2026-002", "Mejri", "Rania", "rania.mejri@etudiant.tn", LocalDate.of(2002, 9, 21));
                Etudiant et3 = new Etudiant(null, "ETU-2026-003", "Kefi", "Yassine", "yassine.kefi@etudiant.tn", LocalDate.of(2001, 12, 3));
                etudiantRepository.saveAll(List.of(et1, et2, et3));

                // 6. Inscriptions
                Inscription i1 = new Inscription(null, et1, c1, LocalDate.of(2026, 9, 15), 17.5, StatutInscription.VALIDE);
                Inscription i2 = new Inscription(null, et2, c1, LocalDate.of(2026, 9, 15), 18.0, StatutInscription.VALIDE);
                Inscription i3 = new Inscription(null, et3, c1, LocalDate.of(2026, 9, 16), 11.0, StatutInscription.VALIDE);
                Inscription i4 = new Inscription(null, et1, c2, LocalDate.of(2026, 9, 15), 16.0, StatutInscription.VALIDE);
                Inscription i5 = new Inscription(null, et2, c3, LocalDate.of(2026, 9, 18), 9.5, StatutInscription.EN_ATTENTE);
                inscriptionRepository.saveAll(List.of(i1, i2, i3, i4, i5));

                System.out.println(">>> Initialisation automatique des données de démonstration terminée avec succès !");
            }
        };
    }
}
