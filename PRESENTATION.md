# PRÉSENTATION ORALE – DS1 Spring Boot
## Système de Gestion Scolaire et Académique

---

## Plan (5 minutes)

### 1. Contexte Métier (1 min)

**Domaine choisi :** École / Université  
**Problème résolu :** Gérer les départements, enseignants, salles, cours, étudiants et inscriptions.

---

### 2. Les 6 Entités et leurs Relations (1 min 30)

| Entité | Rôle |
|---|---|
| `Departement` | Structure pédagogique (code unique, localisation) |
| `Enseignant` | Professeur rattaché à un département |
| `Salle` | Local avec numéro, capacité et type |
| `Cours` | Module avec code unique, crédits, volume horaire |
| `Etudiant` | Étudiant avec matricule unique |
| `Inscription` | Liaison Étudiant ↔ Cours avec note (0-20) et statut |

**Relations JPA utilisées :**
- `@OneToMany / @ManyToOne` : Departement → Enseignant, Departement → Cours, Enseignant → Cours, Cours → Inscription, Etudiant → Inscription
- `@ManyToMany` : Cours ↔ Salle (table de jointure `cours_salles`)

---

### 3. Architecture Spring Boot (30 s)

```
model/ → repository/ → service/ → controller/
```

- **model** : entités JPA annotées (`@Entity`, `@Table`, validations `@NotBlank`, `@Email`...)
- **repository** : interfaces `JpaRepository` + requêtes `@Query`
- **service** : logique métier, gestion des erreurs (`ResourceNotFoundException`, `BadRequestException`)
- **controller** : API REST (`@RestController`, `@GetMapping`, `@PostMapping`...)

---

### 4. Les 3 Requêtes Personnalisées JPQL (1 min)

#### Requête 1 — Cours par Département
```java
@Query("SELECT c FROM Cours c WHERE c.departement.id = :departementId")
// GET /api/cours/departement/1
```
Retourne le catalogue de cours d'un département.

#### Requête 2 — Cours par Salle
```java
@Query("SELECT c FROM Cours c JOIN c.salles s WHERE s.id = :salleId")
// GET /api/cours/salle/1
```
Retourne les cours programmés dans une salle (jointure ManyToMany).

#### Requête 3 — Inscriptions filtrées par Statut + Note
```java
@Query("SELECT i FROM Inscription i WHERE i.statut = :statut AND i.note >= :minNote")
// GET /api/inscriptions/recherche?statut=VALIDE&minNote=15.0
```
Retourne les inscriptions validées avec note ≥ seuil (pour les mentions).

---

### 5. API REST et Tests Postman (1 min)

**31 endpoints REST** organisés en 6 groupes.

**Tests Newman (Postman CLI) :**
- 31 requêtes exécutées
- **38 assertions passées**
- **0 échec**

**Cas testés :**
- CRUD complet sur les 6 entités
- Les 3 (+ 1 bonus) requêtes personnalisées
- Erreurs 404 (ID inexistant)
- Erreurs 400 (données invalides, doublon, note > 20...)

---

### Points clés à retenir

1. **Spring Boot auto-configure** la connexion MySQL via `application.properties`
2. **Hibernate** crée automatiquement les tables avec `ddl-auto=update`
3. **`@Valid`** dans les contrôleurs déclenche la validation des données
4. **`@RestControllerAdvice`** centralise la gestion des erreurs HTTP
5. Le `CommandLineRunner` insère des données de démo au démarrage
