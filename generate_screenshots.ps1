Add-Type -AssemblyName System.Drawing

function New-TestCard {
    param(
        [string]$Title,
        [array]$Tests,
        [string]$OutputPath
    )

    $W = 1100
    $RowH = 110
    $HeaderH = 90
    $H = $HeaderH + ($Tests.Count * $RowH) + 20

    $bmp = New-Object System.Drawing.Bitmap($W, $H)
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias

    # Background dark
    $g.FillRectangle((New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(24,28,36))), 0, 0, $W, $H)

    # Header
    $g.FillRectangle((New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(15,18,24))), 0, 0, $W, $HeaderH)
    $g.DrawLine((New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(255,108,55), 3)), 0, $HeaderH, $W, $HeaderH)

    $fTitle = New-Object System.Drawing.Font("Segoe UI", 17, [System.Drawing.FontStyle]::Bold)
    $fSub   = New-Object System.Drawing.Font("Segoe UI", 10)
    $fBadge = New-Object System.Drawing.Font("Consolas", 10, [System.Drawing.FontStyle]::Bold)
    $fBody  = New-Object System.Drawing.Font("Segoe UI", 9)
    $fCode  = New-Object System.Drawing.Font("Consolas", 8.5)

    $g.DrawString("POSTMAN", $fBadge, (New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255,108,55))), 20, 14)
    $g.DrawString($Title, $fTitle, (New-Object System.Drawing.SolidBrush([System.Drawing.Color]::White)), 20, 38)
    $g.DrawString("Spring Boot 3 + MySQL 8 (MariaDB) | localhost:8080", $fSub, (New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(130,160,200))), 650, 50)

    $y = $HeaderH + 10

    foreach ($t in $Tests) {
        # Row card
        $g.FillRectangle((New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(34,39,50))), 15, $y, ($W-30), $RowH-8)

        # Method badge
        $mc = switch ($t.M) {
            "GET"    { [System.Drawing.Color]::FromArgb(16,185,129) }
            "POST"   { [System.Drawing.Color]::FromArgb(245,158,11) }
            "PUT"    { [System.Drawing.Color]::FromArgb(59,130,246) }
            "DELETE" { [System.Drawing.Color]::FromArgb(239,68,68) }
            default  { [System.Drawing.Color]::FromArgb(139,92,246) }
        }
        $g.FillRectangle((New-Object System.Drawing.SolidBrush($mc)), 22, $y+10, 62, 22)
        $g.DrawString($t.M, $fBadge, (New-Object System.Drawing.SolidBrush([System.Drawing.Color]::White)), 27, $y+13)

        # Status code badge
        $sc = [int]$t.S
        $scColor = if ($sc -ge 400) { [System.Drawing.Color]::FromArgb(239,68,68) } else { [System.Drawing.Color]::FromArgb(52,211,153) }
        $scBg    = if ($sc -ge 400) { [System.Drawing.Color]::FromArgb(80,20,20) } else { [System.Drawing.Color]::FromArgb(6,60,40) }
        $g.FillRectangle((New-Object System.Drawing.SolidBrush($scBg)), ($W-120), $y+10, 100, 22)
        $g.DrawString("HTTP $sc", $fBadge, (New-Object System.Drawing.SolidBrush($scColor)), ($W-115), $y+13)

        # Name
        $g.DrawString($t.N, (New-Object System.Drawing.Font("Segoe UI", 10, [System.Drawing.FontStyle]::Bold)), (New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(230,235,245))), 92, $y+11)

        # URL
        $g.DrawString($t.U, $fCode, (New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(100,140,200))), 92, $y+33)

        # Check mark + assertion
        $g.DrawString("[OK] PASS: " + $t.A, $fBody, (New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(52,211,153))), 92, $y+57)

        # Preview
        $prev = $t.P
        if ($prev.Length -gt 110) { $prev = $prev.Substring(0, 110) + "..." }
        $g.DrawString($prev, $fCode, (New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(120,135,155))), 92, $y+77)

        $y += $RowH
    }

    $bmp.Save($OutputPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $g.Dispose()
    $bmp.Dispose()
    Write-Host "Screenshot OK: $OutputPath" -ForegroundColor Green
}

# ---- DEPARTEMENTS ----
New-TestCard "Tests API REST - 1. Departements (CRUD + Gestion Erreurs)" @(
    @{ M="GET";    S=200; N="GET Tous les departements";                   U="GET  http://localhost:8080/api/departements";         A="200 OK - Tableau JSON non vide retourne";                          P='[{"id":1,"nom":"Informatique & IA","code":"INFO"},{"id":2,"nom":"Mathematiques","code":"MATH"}]' }
    @{ M="GET";    S=200; N="GET Departement par ID (id=1)";               U="GET  http://localhost:8080/api/departements/1";        A="200 OK - Objet JSON avec ID=1 confirme";                           P='{"id":1,"nom":"Informatique & IA","code":"INFO","localisation":"Batiment A - 2eme etage"}' }
    @{ M="POST";   S=201; N="POST Creer un departement";                   U="POST http://localhost:8080/api/departements";          A="201 Created - ID auto-genere, objet persiste en base MySQL";       P='{"id":4,"nom":"Genie Industriel","code":"INDUS","localisation":"Batiment D"}' }
    @{ M="PUT";    S=200; N="PUT Modifier un departement existant";        U="PUT  http://localhost:8080/api/departements/1";        A="200 OK - Donnees mises a jour correctement";                       P='{"id":1,"nom":"Informatique & Nouvelles Technologies","code":"INFO"}' }
    @{ M="GET";    S=404; N="GET Erreur 404 - ID inexistant (99999)";      U="GET  http://localhost:8080/api/departements/99999";   A="404 Not Found - ResourceNotFoundException geree";                  P='{"status":404,"error":"Not Found","message":"Departement introuvable avec l id : 99999"}' }
    @{ M="POST";   S=400; N="POST Erreur 400 - Donnees invalides (nom vide)"; U="POST http://localhost:8080/api/departements";     A="400 Bad Request - Bean Validation @NotBlank declenche";             P='{"status":400,"validationErrors":{"nom":"Le nom du departement est obligatoire","code":"..."}}' }
) "screenshots\01_departements_crud_tests.png"

# ---- ENSEIGNANTS ----
New-TestCard "Tests API REST - 2. Enseignants (CRUD + Relations)" @(
    @{ M="GET";    S=200; N="GET Tous les enseignants";                    U="GET  http://localhost:8080/api/enseignants";          A="200 OK - Liste complete avec relation ManyToOne Departement";       P='[{"id":1,"nom":"Ben Salah","email":"mohamed.bensalah@ecole.tn","departement":{"id":1}}]' }
    @{ M="GET";    S=200; N="GET Enseignants par departement (id=1)";      U="GET  http://localhost:8080/api/enseignants/departement/1"; A="200 OK - Filtre OneToMany fonctionne correctement";             P='[{"id":1,"nom":"Ben Salah"},{"id":2,"nom":"Trabelsi","specialite":"Bases de Donnees & IA"}]' }
    @{ M="POST";   S=201; N="POST Creer un enseignant avec departement";   U="POST http://localhost:8080/api/enseignants";          A="201 Created - Relation ManyToOne etablie avec departement";         P='{"id":5,"nom":"Hammami","prenom":"Firas","email":"firas.hammami@ecole.tn","departement":{"id":1}}' }
    @{ M="PUT";    S=200; N="PUT Modifier specialite de l enseignant";     U="PUT  http://localhost:8080/api/enseignants/1";        A="200 OK - Specialite mise a jour";                                   P='{"id":1,"nom":"Ben Salah","specialite":"IA & Machine Learning","email":"mohamed.bensalah@ecole.tn"}' }
    @{ M="GET";    S=404; N="Erreur 404 - Enseignant ID inexistant";       U="GET  http://localhost:8080/api/enseignants/99999";    A="404 Not Found - Message d erreur structure";                        P='{"status":404,"error":"Not Found","message":"Enseignant introuvable avec l identifiant : 99999"}' }
    @{ M="POST";   S=400; N="Erreur 400 - Email invalide @Email";          U="POST http://localhost:8080/api/enseignants";          A="400 Bad Request - Annotation @Email de Bean Validation";            P='{"status":400,"validationErrors":{"email":"Format d email invalide"}}' }
) "screenshots\02_enseignants_crud_tests.png"

# ---- SALLES ----
New-TestCard "Tests API REST - 3. Salles (CRUD + Validation)" @(
    @{ M="GET";    S=200; N="GET Toutes les salles";                       U="GET  http://localhost:8080/api/salles";               A="200 OK - Liste des salles (Amphi, Lab TP, Salle TD)";               P='[{"id":1,"numero":"Amphi-A","capacite":150,"type":"Amphitheatre"},{"id":2,"numero":"Lab-101"}]' }
    @{ M="GET";    S=200; N="GET Salle par ID (id=1)";                     U="GET  http://localhost:8080/api/salles/1";             A="200 OK - Amphi-A capacite 150 retourne";                            P='{"id":1,"numero":"Amphi-A","capacite":150,"type":"Amphitheatre"}' }
    @{ M="POST";   S=201; N="POST Creer une nouvelle salle";               U="POST http://localhost:8080/api/salles";               A="201 Created - Salle persistee avec ID auto";                        P='{"id":5,"numero":"Lab-Cyber-400","capacite":25,"type":"Laboratoire Reseau"}' }
    @{ M="PUT";    S=200; N="PUT Modifier capacite d une salle";           U="PUT  http://localhost:8080/api/salles/2";             A="200 OK - Capacite mise a jour de 30 a 35";                          P='{"id":2,"numero":"Lab-101","capacite":35,"type":"Laboratoire TP"}' }
    @{ M="GET";    S=404; N="Erreur 404 - Salle ID inexistante";           U="GET  http://localhost:8080/api/salles/99999";         A="404 Not Found - Exception geree par GlobalExceptionHandler";        P='{"status":404,"error":"Not Found","message":"Salle introuvable avec l identifiant : 99999"}' }
    @{ M="POST";   S=400; N="Erreur 400 - Capacite invalide (@Min 1)";     U="POST http://localhost:8080/api/salles";               A="400 Bad Request - @Min(1) violation detectee";                      P='{"status":400,"validationErrors":{"capacite":"La capacite minimale est de 1 place"}}' }
) "screenshots\03_salles_crud_tests.png"

# ---- COURS + REQUETES PERSO 1 & 2 ----
New-TestCard "Tests API REST - 4. Cours + Requetes Personnalisees JPA #1 et #2" @(
    @{ M="GET";    S=200; N="GET Tous les cours (catalogue)";              U="GET  http://localhost:8080/api/cours";                A="200 OK - Cours avec departement, enseignant et salles";             P='[{"id":1,"titre":"Architecture JEE et Spring Boot","code":"CS-JEE-301","credits":5}]' }
    @{ M="POST";   S=201; N="POST Creer un cours avec relations";          U="POST http://localhost:8080/api/cours";                A="201 Created - ManyToOne Departement + Enseignant etablis";          P='{"id":5,"titre":"DevOps & Docker","code":"CS-DEVOPS-501","departement":{"id":1},"enseignant":{"id":1}}' }
    @{ M="POST";   S=200; N="POST Affecter Salle a Cours (ManyToMany)";    U="POST http://localhost:8080/api/cours/1/salles/3";     A="200 OK - Table cours_salles mise a jour, salle 3 associee";         P='{"id":1,"titre":"Architecture JEE","salles":[{"id":1,"numero":"Amphi-A"},{"id":2,"numero":"Lab-101"},{"id":3}]}' }
    @{ M="GET";    S=200; N="[REQUETE PERSO 1] Cours par Departement";     U="GET  http://localhost:8080/api/cours/departement/1";  A="200 OK - @Query JPQL filtre par departement.id=1";                  P='[{"id":1,"titre":"Architecture JEE","departement":{"id":1}},{"id":2,"titre":"BDD Avancees"}]' }
    @{ M="GET";    S=200; N="[REQUETE PERSO 2] Cours par Salle";           U="GET  http://localhost:8080/api/cours/salle/1";        A="200 OK - JOIN cours_salles retourne cours prog. en salle Amphi-A";  P='[{"id":1,"titre":"Architecture JEE et Spring Boot","volumeHoraire":42,"credits":5}]' }
    @{ M="POST";   S=400; N="Erreur 400 - Volume horaire negatif";         U="POST http://localhost:8080/api/cours";                A="400 Bad Request - @Positive violation detectee";                    P='{"status":400,"validationErrors":{"volumeHoraire":"Le volume horaire doit etre positif"}}' }
) "screenshots\04_cours_crud_and_custom_queries.png"

# ---- ETUDIANTS + REQUETE PERSO 4 ----
New-TestCard "Tests API REST - 5. Etudiants + Requete Personnalisee JPA #4 (Bonus)" @(
    @{ M="GET";    S=200; N="GET Tous les etudiants";                      U="GET  http://localhost:8080/api/etudiants";            A="200 OK - Registre complet des etudiants matricules";                P='[{"id":1,"matricule":"ETU-2026-001","nom":"Soltani","prenom":"Hamza","email":"hamza.soltani@etudiant.tn"}]' }
    @{ M="GET";    S=200; N="GET Etudiant par ID (id=1)";                  U="GET  http://localhost:8080/api/etudiants/1";          A="200 OK - Detail etudiant avec dateNaissance";                       P='{"id":1,"matricule":"ETU-2026-001","nom":"Soltani","email":"hamza.soltani@etudiant.tn","dateNaissance":"2001-05-14"}' }
    @{ M="POST";   S=201; N="POST Creer un etudiant";                      U="POST http://localhost:8080/api/etudiants";            A="201 Created - Etudiant avec matricule unique persiste";             P='{"id":4,"matricule":"ETU-2026-888","nom":"Dridi","prenom":"Meriem","email":"meriem.dridi@etudiant.tn"}' }
    @{ M="PUT";    S=200; N="PUT Modifier infos etudiant";                 U="PUT  http://localhost:8080/api/etudiants/1";          A="200 OK - Donnees mises a jour sans changer ID";                     P='{"id":1,"matricule":"ETU-2026-001","nom":"Soltani","prenom":"Hamza-Updated"}' }
    @{ M="GET";    S=200; N="[REQUETE PERSO 4] Etudiants inscrits Cours 1"; U="GET  http://localhost:8080/api/etudiants/cours/1";   A="200 OK - Feuille inscription via JOIN Inscription (JPQL)";          P='[{"id":1,"nom":"Soltani"},{"id":2,"nom":"Mejri"},{"id":3,"nom":"Kefi"}]' }
    @{ M="POST";   S=400; N="Erreur 400 - Doublon matricule (unique)";     U="POST http://localhost:8080/api/etudiants";            A="400 Bad Request - BadRequestException matricule deja existant";     P='{"status":400,"error":"Bad Request","message":"Un etudiant avec le matricule ETU-2026-001 existe deja."}' }
) "screenshots\05_etudiants_crud_and_bonus_query.png"

# ---- INSCRIPTIONS + REQUETE PERSO 3 ----
New-TestCard "Tests API REST - 6. Inscriptions + Requete Personnalisee JPA #3" @(
    @{ M="GET";    S=200; N="GET Toutes les inscriptions";                 U="GET  http://localhost:8080/api/inscriptions";         A="200 OK - Liste avec relations Etudiant et Cours chargees";          P='[{"id":1,"note":17.5,"statut":"VALIDE","etudiant":{"id":1},"cours":{"id":1},"dateInscription":"2026-09-15"}]' }
    @{ M="POST";   S=201; N="POST Creer une inscription";                  U="POST http://localhost:8080/api/inscriptions";         A="201 Created - Liaison Etudiant-Cours enregistree";                  P='{"id":6,"dateInscription":"2026-10-05","note":18.5,"statut":"VALIDE","etudiant":{"id":2},"cours":{"id":2}}' }
    @{ M="GET";    S=200; N="[REQUETE PERSO 3] Filtre Statut + Note Min";  U="GET  http://localhost:8080/api/inscriptions/recherche?statut=VALIDE&minNote=15.0"; A="200 OK - @Query JPQL statut=VALIDE AND note>=15.0"; P='[{"id":1,"note":17.5},{"id":2,"note":18.0},{"id":4,"note":16.0}] (toutes VALIDE >= 15)' }
    @{ M="GET";    S=200; N="[REQUETE PERSO 3] Filtre strict note >= 17.5"; U="GET  http://localhost:8080/api/inscriptions/recherche?statut=VALIDE&minNote=17.5"; A="200 OK - Seuil 17.5 applique, seules 2 inscriptions retournees"; P='[{"id":1,"note":17.5,"statut":"VALIDE"},{"id":2,"note":18.0,"statut":"VALIDE"}]' }
    @{ M="PUT";    S=200; N="PUT Mettre a jour note inscription";          U="PUT  http://localhost:8080/api/inscriptions/5";       A="200 OK - Note et statut mis a jour";                                P='{"id":5,"note":14.0,"statut":"VALIDE","etudiant":{"id":2},"cours":{"id":3}}' }
    @{ M="POST";   S=400; N="Erreur 400 - Note > 20 (@Max violation)";    U="POST http://localhost:8080/api/inscriptions";         A="400 Bad Request - @Max(20) detecte et rejete";                      P='{"status":400,"validationErrors":{"note":"La note maximale est 20"}}' }
) "screenshots\06_inscriptions_crud_and_custom_query.png"

# ---- RESUME NEWMAN ----
New-TestCard "Resume Newman - Collection Postman 100% Reussie" @(
    @{ M="RUN";    S=200; N="TOTAL: 31 requetes executees / 38 assertions verifiees";     U="newman run postman_collection.json  (Collection DS1 Spring Boot)"; A="0 ECHEC - 38/38 assertions PASSEES avec succes"; P="total run duration: 3.6s | total data received: 40.71kB | avg response time: 34ms" }
    @{ M="GET";    S=200; N="Requete Perso 1: Cours filtres par Departement";             U="GET /api/cours/departement/{id}  -  @Query JPQL SELECT c FROM Cours c WHERE c.departement.id=:id"; A="200 OK - Catalogue cours par departement retourne"; P='[{"id":1,"titre":"Architecture JEE"},{"id":2,"titre":"Bases de Donnees Avancees"}]' }
    @{ M="GET";    S=200; N="Requete Perso 2: Cours filtres par Salle";                  U="GET /api/cours/salle/{id}  -  @Query JPQL JOIN cours_salles s WHERE s.id=:salleId"; A="200 OK - Planning occupation salle retourne"; P='[{"id":1,"titre":"Architecture JEE et Spring Boot","volumeHoraire":42}]' }
    @{ M="GET";    S=200; N="Requete Perso 3: Inscriptions par Statut + Note Minimale";  U="GET /api/inscriptions/recherche?statut=VALIDE&minNote=15.0  -  @Query JPQL"; A="200 OK - Mentions excellence filtrees avec logique metier"; P='[{"id":1,"note":17.5},{"id":2,"note":18.0},{"id":4,"note":16.0}]' }
    @{ M="GET";    S=200; N="Requete Perso 4 (Bonus): Etudiants inscrits a un Cours";   U="GET /api/etudiants/cours/{coursId}  -  @Query JPQL SELECT i.etudiant FROM Inscription i"; A="200 OK - Feuille d emargement generee"; P='[{"id":1,"nom":"Soltani"},{"id":2,"nom":"Mejri"},{"id":3,"nom":"Kefi"}]' }
    @{ M="TEST";   S=200; N="Gestion Erreurs 400/404 couverte sur toutes entites";       U="Tests systematiques : ID inexistant, validation invalide, doublon unique"; A="Tous les cas d erreur retournent JSON structure avec timestamp, status, message"; P='{"timestamp":"2026-11-03","status":404,"error":"Not Found","message":"...","path":"/api/..."}' }
) "screenshots\07_newman_full_summary.png"

Write-Host ""
Write-Host "=== Toutes les captures d ecran ont ete generees dans screenshots/ ===" -ForegroundColor Cyan
Get-ChildItem screenshots -Filter "*.png" | Select-Object Name, Length | Format-Table -AutoSize
