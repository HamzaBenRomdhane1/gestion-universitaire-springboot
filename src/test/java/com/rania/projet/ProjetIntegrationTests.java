package com.rania.projet;

import com.rania.projet.exception.BadRequestException;
import com.rania.projet.exception.ResourceNotFoundException;
import com.rania.projet.model.*;
import com.rania.projet.repository.*;
import com.rania.projet.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProjetIntegrationTests {

    @Autowired
    private DepartementService departementService;

    @Autowired
    private EnseignantService enseignantService;

    @Autowired
    private SalleService salleService;

    @Autowired
    private CoursService coursService;

    @Autowired
    private EtudiantService etudiantService;

    @Autowired
    private InscriptionService inscriptionService;

    @Autowired
    private CoursRepository coursRepository;

    @Autowired
    private InscriptionRepository inscriptionRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Test
    @DisplayName("Test CRUD complet sur Departement")
    void testDepartementCrud() {
        Departement dep = new Departement(null, "Génie Civil", "CIVIL", "Bâtiment C");
        Departement saved = departementService.createDepartement(dep);
        assertNotNull(saved.getId());

        Departement fetched = departementService.getDepartementById(saved.getId());
        assertEquals("Génie Civil", fetched.getNom());

        fetched.setNom("Génie Civil & Environnement");
        Departement updated = departementService.updateDepartement(saved.getId(), fetched);
        assertEquals("Génie Civil & Environnement", updated.getNom());

        departementService.deleteDepartement(saved.getId());
        assertThrows(ResourceNotFoundException.class, () -> departementService.getDepartementById(saved.getId()));
    }

    @Test
    @DisplayName("Test CRUD complet sur Enseignant")
    void testEnseignantCrud() {
        Departement dep = departementService.getAllDepartements().get(0);
        Enseignant ens = new Enseignant(null, "Mansour", "Ali", "ali.mansour@ecole.tn", "Réseaux", "+216 20 000 111", dep);
        Enseignant saved = enseignantService.createEnseignant(ens);
        assertNotNull(saved.getId());

        Enseignant fetched = enseignantService.getEnseignantById(saved.getId());
        assertEquals("Ali", fetched.getPrenom());

        fetched.setSpecialite("Cybersécurité");
        Enseignant updated = enseignantService.updateEnseignant(saved.getId(), fetched);
        assertEquals("Cybersécurité", updated.getSpecialite());

        enseignantService.deleteEnseignant(saved.getId());
        assertThrows(ResourceNotFoundException.class, () -> enseignantService.getEnseignantById(saved.getId()));
    }

    @Test
    @DisplayName("Test CRUD complet sur Salle")
    void testSalleCrud() {
        Salle salle = new Salle(null, "Lab-Virtual-99", 25, "Salle Multimédia");
        Salle saved = salleService.createSalle(salle);
        assertNotNull(saved.getId());

        Salle fetched = salleService.getSalleById(saved.getId());
        assertEquals("Lab-Virtual-99", fetched.getNumero());

        fetched.setCapacite(35);
        Salle updated = salleService.updateSalle(saved.getId(), fetched);
        assertEquals(35, updated.getCapacite());

        salleService.deleteSalle(saved.getId());
        assertThrows(ResourceNotFoundException.class, () -> salleService.getSalleById(saved.getId()));
    }

    @Test
    @DisplayName("Test CRUD complet sur Cours et affectation ManyToMany Salle")
    void testCoursCrudEtRelationSalle() {
        Departement dep = departementService.getAllDepartements().get(0);
        Enseignant ens = enseignantService.getAllEnseignants().get(0);
        Salle salle = salleService.getAllSalles().get(0);

        Cours cours = new Cours(null, "DevOps & Cloud", "CS-CLOUD-401", 30, 4, dep, ens);
        Cours saved = coursService.createCours(cours);
        assertNotNull(saved.getId());

        // Test ManyToMany
        Cours withSalle = coursService.addSalleToCours(saved.getId(), salle.getId());
        assertTrue(withSalle.getSalles().stream().anyMatch(s -> s.getId().equals(salle.getId())));

        // Test Custom Query 2 : cours par salle
        List<Cours> coursInSalle = coursService.getCoursBySalle(salle.getId());
        assertFalse(coursInSalle.isEmpty());

        coursService.deleteCours(saved.getId());
        assertThrows(ResourceNotFoundException.class, () -> coursService.getCoursById(saved.getId()));
    }

    @Test
    @DisplayName("Test CRUD complet sur Etudiant")
    void testEtudiantCrud() {
        Etudiant etudiant = new Etudiant(null, "ETU-TEST-999", "TestNom", "TestPrenom", "test.etudiant@ecole.tn", LocalDate.of(2003, 1, 15));
        Etudiant saved = etudiantService.createEtudiant(etudiant);
        assertNotNull(saved.getId());

        Etudiant fetched = etudiantService.getEtudiantById(saved.getId());
        assertEquals("ETU-TEST-999", fetched.getMatricule());

        fetched.setNom("TestNomModifie");
        Etudiant updated = etudiantService.updateEtudiant(saved.getId(), fetched);
        assertEquals("TestNomModifie", updated.getNom());

        etudiantService.deleteEtudiant(saved.getId());
        assertThrows(ResourceNotFoundException.class, () -> etudiantService.getEtudiantById(saved.getId()));
    }

    @Test
    @DisplayName("Test CRUD complet sur Inscription")
    void testInscriptionCrud() {
        Etudiant etudiant = etudiantService.getAllEtudiants().get(0);
        Cours cours = coursService.getAllCours().get(0);

        Inscription inscription = new Inscription(null, etudiant, cours, LocalDate.now(), 15.5, StatutInscription.VALIDE);
        Inscription saved = inscriptionService.createInscription(inscription);
        assertNotNull(saved.getId());

        Inscription fetched = inscriptionService.getInscriptionById(saved.getId());
        assertEquals(15.5, fetched.getNote());

        fetched.setNote(18.0);
        Inscription updated = inscriptionService.updateInscription(saved.getId(), fetched);
        assertEquals(18.0, updated.getNote());

        inscriptionService.deleteInscription(saved.getId());
        assertThrows(ResourceNotFoundException.class, () -> inscriptionService.getInscriptionById(saved.getId()));
    }

    @Test
    @DisplayName("Test Requête Personnalisée 1 : Cours par Département")
    void testCustomQuery1_CoursParDepartement() {
        Departement dep = departementService.getAllDepartements().get(0);
        List<Cours> list = coursRepository.findByDepartementId(dep.getId());
        assertNotNull(list);
        assertFalse(list.isEmpty());
        for (Cours c : list) {
            assertEquals(dep.getId(), c.getDepartement().getId());
        }
    }

    @Test
    @DisplayName("Test Requête Personnalisée 2 : Cours par Salle")
    void testCustomQuery2_CoursParSalle() {
        List<Salle> salles = salleService.getAllSalles();
        assertFalse(salles.isEmpty());
        Salle salle = salles.get(0);
        List<Cours> list = coursRepository.findBySalleId(salle.getId());
        assertNotNull(list);
    }

    @Test
    @DisplayName("Test Requête Personnalisée 3 : Inscriptions par Statut et Note Minimale")
    void testCustomQuery3_InscriptionsParStatutEtNote() {
        List<Inscription> validesSup15 = inscriptionRepository.findByStatutAndNoteGreaterThanEqual(
                StatutInscription.VALIDE, 15.0);
        assertNotNull(validesSup15);
        assertFalse(validesSup15.isEmpty());
        for (Inscription i : validesSup15) {
            assertEquals(StatutInscription.VALIDE, i.getStatut());
            assertTrue(i.getNote() >= 15.0);
        }
    }

    @Test
    @DisplayName("Test Requête Personnalisée 4 (Bonus) : Étudiants par Cours")
    void testCustomQuery4_EtudiantsParCours() {
        Cours c = coursService.getAllCours().get(0);
        List<Etudiant> etudiants = etudiantRepository.findEtudiantsByCoursId(c.getId());
        assertNotNull(etudiants);
        assertFalse(etudiants.isEmpty());
    }

    @Test
    @DisplayName("Test Gestion des Erreurs : Unicité et Intégrité")
    void testValidationEtUnicite() {
        Departement dep = departementService.getAllDepartements().get(0);
        Departement duplicateDep = new Departement(null, "Autre", dep.getCode(), "Local");
        assertThrows(BadRequestException.class, () -> departementService.createDepartement(duplicateDep));
    }
}
