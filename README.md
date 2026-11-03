# DS1 – Développement Web : Projet Spring Boot
## Système de Gestion Scolaire et Académique

---

## 1. Contexte Métier

Ce projet modélise le **système d'information académique** d'une école supérieure / université.  
Il permet de gérer les départements pédagogiques, les enseignants, les salles de cours, les modules d'enseignement (cours), les étudiants immatriculés et leurs inscriptions avec notation.

---

## 2. Entités du Domaine (6 entités)

| Entité | Rôle |
|---|---|
| **`Departement`** | Structure pédagogique (ex : Informatique, Mathématiques) |
| **`Enseignant`** | Professeur rattaché à un département |
| **`Salle`** | Local physique (Amphithéâtre, Laboratoire TP, Salle TD) |
| **`Cours`** | Module d'enseignement avec crédits et volume horaire |
| **`Etudiant`** | Étudiant immatriculé dans l'établissement |
| **`Inscription`** | Association Étudiant ↔ Cours, avec date, note (0-20) et statut |

---

## 3. Relations JPA

```
Departement ──(1..*)──> Enseignant    [OneToMany / ManyToOne]
Departement ──(1..*)──> Cours         [OneToMany / ManyToOne]
Enseignant  ──(1..*)──> Cours         [OneToMany / ManyToOne]
Cours       ──(*..*)──> Salle         [ManyToMany via cours_salles]
Etudiant    ──(1..*)──> Inscription   [OneToMany / ManyToOne]
Cours       ──(1..*)──> Inscription   [OneToMany / ManyToOne]
```

---

## 4. Technologies Utilisées

| Technologie | Version | Usage |
|---|---|---|
| **Java** | 17 (LTS) | Langage principal |
| **Spring Boot** | 3.2.5 | Framework principal |
| **Spring Data JPA** | Inclus | ORM et accès base de données |
| **Hibernate** | 6.4.4 | Implémentation JPA |
| **MySQL / MariaDB** | 8.0 / 10.4 | Base de données (XAMPP) |
| **Lombok** | Optionnel | Déclaré dans pom.xml |
| **Maven** | 3.9.x | Gestion du build |
| **Postman / Newman** | CLI | Tests des API REST |

---

## 5. Architecture du Projet

```
src/
├── main/
│   ├── java/com/rania/projet/
│   │   ├── ProjetApplication.java          ← Point d'entrée Spring Boot
│   │   ├── model/
│   │   │   ├── Departement.java            ← Entité JPA
│   │   │   ├── Enseignant.java             ← Entité JPA
│   │   │   ├── Salle.java                  ← Entité JPA
│   │   │   ├── Cours.java                  ← Entité JPA
│   │   │   ├── Etudiant.java               ← Entité JPA
│   │   │   ├── Inscription.java            ← Entité JPA
│   │   │   └── StatutInscription.java      ← Enum (INSCRIT, VALIDE, EN_ATTENTE, ABANDONNE)
│   │   ├── repository/
│   │   │   ├── DepartementRepository.java  ← JpaRepository + requêtes
│   │   │   ├── EnseignantRepository.java   ← JpaRepository + requêtes
│   │   │   ├── SalleRepository.java        ← JpaRepository + requêtes
│   │   │   ├── CoursRepository.java        ← JpaRepository + 2 requêtes @Query
│   │   │   ├── EtudiantRepository.java     ← JpaRepository + 1 requête @Query
│   │   │   └── InscriptionRepository.java  ← JpaRepository + 1 requête @Query
│   │   ├── service/
│   │   │   ├── DepartementService.java
│   │   │   ├── EnseignantService.java
│   │   │   ├── SalleService.java
│   │   │   ├── CoursService.java
│   │   │   ├── EtudiantService.java
│   │   │   └── InscriptionService.java
│   │   ├── controller/
│   │   │   ├── DepartementController.java
│   │   │   ├── EnseignantController.java
│   │   │   ├── SalleController.java
│   │   │   ├── CoursController.java
│   │   │   ├── EtudiantController.java
│   │   │   └── InscriptionController.java
│   │   └── exception/
│   │       ├── ResourceNotFoundException.java
│   │       ├── BadRequestException.java
│   │       ├── ErrorResponse.java
│   │       └── GlobalExceptionHandler.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/rania/projet/
        ├── ProjetApplicationTests.java
        └── ProjetIntegrationTests.java     ← 12 tests d'intégration
```

---

## 6. Requêtes Personnalisées JPA

### Requête 1 — Cours par Département
**Besoin métier :** Consulter le catalogue de cours d'un département donné.

```java
// CoursRepository.java
@Query("SELECT c FROM Cours c WHERE c.departement.id = :departementId")
List<Cours> findByDepartementId(@Param("departementId") Long departementId);
```
**Endpoint :** `GET /api/cours/departement/{departementId}`

---

### Requête 2 — Cours par Salle
**Besoin métier :** Voir quels cours sont programmés dans une salle donnée (gestion des plannings d'occupation).

```java
// CoursRepository.java
@Query("SELECT c FROM Cours c JOIN c.salles s WHERE s.id = :salleId")
List<Cours> findBySalleId(@Param("salleId") Long salleId);
```
**Endpoint :** `GET /api/cours/salle/{salleId}`

---

### Requête 3 — Inscriptions par Statut et Note Minimale
**Besoin métier :** Identifier les étudiants éligibles aux mentions ou validations officielles selon leur note et statut.

```java
// InscriptionRepository.java
@Query("SELECT i FROM Inscription i WHERE i.statut = :statut AND i.note >= :minNote")
List<Inscription> findByStatutAndNoteGreaterThanEqual(
    @Param("statut") StatutInscription statut,
    @Param("minNote") Double minNote
);
```
**Endpoint :** `GET /api/inscriptions/recherche?statut=VALIDE&minNote=15.0`

---

### Requête 4 (Bonus) — Étudiants inscrits à un Cours
**Besoin métier :** Générer la feuille d'émargement et le registre d'un cours.

```java
// EtudiantRepository.java
@Query("SELECT i.etudiant FROM Inscription i WHERE i.cours.id = :coursId")
List<Etudiant> findEtudiantsByCoursId(@Param("coursId") Long coursId);
```
**Endpoint :** `GET /api/etudiants/cours/{coursId}`

---

## 7. Endpoints REST

### Départements
| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/departements` | Lister tous les départements |
| GET | `/api/departements/{id}` | Consulter un département |
| POST | `/api/departements` | Créer un département |
| PUT | `/api/departements/{id}` | Modifier un département |
| DELETE | `/api/departements/{id}` | Supprimer un département |

### Enseignants
| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/enseignants` | Lister tous les enseignants |
| GET | `/api/enseignants/{id}` | Consulter un enseignant |
| GET | `/api/enseignants/departement/{id}` | Enseignants d'un département |
| POST | `/api/enseignants` | Créer un enseignant |
| PUT | `/api/enseignants/{id}` | Modifier un enseignant |
| DELETE | `/api/enseignants/{id}` | Supprimer un enseignant |

### Salles
| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/salles` | Lister toutes les salles |
| GET | `/api/salles/{id}` | Consulter une salle |
| POST | `/api/salles` | Créer une salle |
| PUT | `/api/salles/{id}` | Modifier une salle |
| DELETE | `/api/salles/{id}` | Supprimer une salle |

### Cours
| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/cours` | Lister tous les cours |
| GET | `/api/cours/{id}` | Consulter un cours |
| **GET** | **`/api/cours/departement/{id}`** | **[Requête perso 1] Cours par département** |
| **GET** | **`/api/cours/salle/{id}`** | **[Requête perso 2] Cours par salle** |
| POST | `/api/cours` | Créer un cours |
| PUT | `/api/cours/{id}` | Modifier un cours |
| DELETE | `/api/cours/{id}` | Supprimer un cours |
| POST | `/api/cours/{id}/salles/{salleId}` | Affecter une salle (ManyToMany) |
| DELETE | `/api/cours/{id}/salles/{salleId}` | Retirer une salle (ManyToMany) |

### Étudiants
| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/etudiants` | Lister tous les étudiants |
| GET | `/api/etudiants/{id}` | Consulter un étudiant |
| **GET** | **`/api/etudiants/cours/{coursId}`** | **[Requête perso 4] Étudiants d'un cours** |
| POST | `/api/etudiants` | Créer un étudiant |
| PUT | `/api/etudiants/{id}` | Modifier un étudiant |
| DELETE | `/api/etudiants/{id}` | Supprimer un étudiant |

### Inscriptions
| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/inscriptions` | Lister toutes les inscriptions |
| GET | `/api/inscriptions/{id}` | Consulter une inscription |
| GET | `/api/inscriptions/etudiant/{id}` | Inscriptions d'un étudiant |
| GET | `/api/inscriptions/cours/{id}` | Inscriptions pour un cours |
| **GET** | **`/api/inscriptions/recherche?statut=X&minNote=Y`** | **[Requête perso 3] Filtre statut + note** |
| POST | `/api/inscriptions` | Créer une inscription |
| PUT | `/api/inscriptions/{id}` | Modifier une inscription |
| DELETE | `/api/inscriptions/{id}` | Supprimer une inscription |

---

## 8. Configuration de la Base de Données

Fichier : `src/main/resources/application.properties`

```properties
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/gestion_ecole_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

> **Note :** Le projet utilise **XAMPP / MariaDB 10.4** sur le port 3306 (sans mot de passe pour l'utilisateur root en local). La base de données `gestion_ecole_db` est créée automatiquement au démarrage.

---

## 9. Instructions d'Exécution

### Prérequis
- Java 17 (JDK)
- Maven 3.9+
- MySQL ou MariaDB (ex : XAMPP) démarré sur le port 3306

### Démarrage de la base de données (XAMPP)
```bash
# Lancer mysqld depuis le dossier XAMPP
C:\xampp\mysql\bin\mysqld.exe --defaults-file=C:\xampp\mysql\bin\my.ini --standalone
```

### Démarrage de l'application
```bash
# Via Maven
cd ds1jee
mvn spring-boot:run

# OU via le JAR compilé
mvn clean package
java -jar target/projet-1.0.0.jar
```

L'application démarre sur **http://localhost:8080**

### Données initiales
Au premier démarrage, le `CommandLineRunner` dans `ProjetApplication.java` insère automatiquement un jeu de données de démonstration :
- 2 départements, 3 enseignants, 3 salles
- 3 cours avec affectations ManyToMany
- 3 étudiants et 5 inscriptions avec notes

---

## 10. Tests avec Postman

### Importer la collection
1. Ouvrir **Postman**
2. Cliquer **Import** → sélectionner `postman_collection.json`
3. Configurer la variable `baseUrl` = `http://localhost:8080/api`
4. Lancer les requêtes

### Exécution automatisée avec Newman
```bash
npx newman run postman_collection.json
```

**Résultat obtenu :**
- **31 requêtes** exécutées
- **38 assertions** passées
- **0 échec**

### Exemples de tests importants

#### Test Requête Personnalisée 3
```
GET http://localhost:8080/api/inscriptions/recherche?statut=VALIDE&minNote=15.0
```
Retourne toutes les inscriptions validées avec note ≥ 15.

#### Test Erreur 404
```
GET http://localhost:8080/api/departements/99999
```
Retourne :
```json
{
  "timestamp": "2026-11-03T21:50:00",
  "status": 404,
  "error": "Not Found",
  "message": "Département introuvable avec l'identifiant : 99999",
  "path": "/api/departements/99999"
}
```

#### Test Erreur 400 (Validation)
```json
POST /api/enseignants
{ "nom": "Test", "email": "invalid-email", "specialite": "Math" }
```
Retourne :
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Échec de validation des données transmises",
  "validationErrors": {
    "email": "Format d'email invalide",
    "prenom": "Le prénom de l'enseignant est obligatoire"
  }
}
```

---

## 11. Livrables

- [x] Code source Spring Boot complet
- [x] `pom.xml` avec toutes les dépendances
- [x] `application.properties` configuré pour MySQL
- [x] `postman_collection.json` — collection Postman exportée
- [x] `screenshots/` — 7 captures d'écran des tests réussis
- [x] `README.md` — documentation complète
- [x] Tests d'intégration (`ProjetIntegrationTests.java`) — 12 tests passés

---

## 12. Ajout Collaborateur GitHub

Pour ajouter l'enseignant comme collaborateur du dépôt :
1. Aller dans **Settings → Collaborators** du dépôt GitHub
2. Cliquer **Add people**
3. Saisir : **awadi.hatem@gmail.com**
4. Envoyer l'invitation

---

*Projet réalisé dans le cadre du DS1 – Développement Web | Spring Boot 3 + MySQL*
