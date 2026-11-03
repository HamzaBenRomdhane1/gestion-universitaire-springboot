$base = "http://localhost:8080/api"
$results = @()

function Execute-Test {
    param(
        [string]$testName,
        [string]$method,
        [string]$endpoint,
        [object]$body = $null,
        [int]$expectedCode = 200
    )
    $url = "$base$endpoint"
    $statusCode = 0
    $responseContent = ""
    $parsedJson = $null

    try {
        $params = @{
            Uri = $url
            Method = $method
            ContentType = "application/json; charset=utf-8"
            UseBasicParsing = $true
            ErrorAction = "Stop"
        }
        if ($body -ne $null) {
            $params.Body = ($body | ConvertTo-Json -Depth 5)
        }

        $res = Invoke-WebRequest @params
        $statusCode = [int]$res.StatusCode
        $responseContent = $res.Content
        if ($responseContent) {
            try { $parsedJson = $responseContent | ConvertFrom-Json } catch {}
        }
    } catch {
        if ($_.Exception.Response -ne $null) {
            $statusCode = [int]$_.Exception.Response.StatusCode
            $stream = $_.Exception.Response.GetResponseStream()
            if ($stream -ne $null) {
                $reader = New-Object System.IO.StreamReader($stream)
                $responseContent = $reader.ReadToEnd()
                try { $parsedJson = $responseContent | ConvertFrom-Json } catch {}
            }
        } else {
            $responseContent = $_.Exception.Message
        }
    }

    $success = ($statusCode -eq $expectedCode)
    $verdict = if ($success) { "[OK] SUCCESS" } else { "[FAIL] FAILED" }

    $obj = [PSCustomObject]@{
        TestName = $testName
        Method = $method
        Endpoint = $endpoint
        StatusCode = $statusCode
        ExpectedCode = $expectedCode
        Verdict = $verdict
        ResponsePreview = if ($responseContent.Length -gt 150) { $responseContent.Substring(0, 150) + "..." } else { $responseContent }
        RawData = $parsedJson
    }
    return $obj
}

Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host " LANCEMENT DE LA SUITE DE TESTS API REST COMPLETE - DS1 SPRING BOOT " -ForegroundColor Cyan
Write-Host "=================================================================" -ForegroundColor Cyan

# 1. DEPARTEMENTS
$t1 = Execute-Test "1.1 GET Tous les départements" "GET" "/departements" $null 200
$results += $t1

$t2 = Execute-Test "1.2 GET Département par ID" "GET" "/departements/1" $null 200
$results += $t2

$codeDep = "DEP-" + (Get-Random -Minimum 100 -Maximum 999)
$t3 = Execute-Test "1.3 POST Créer un département" "POST" "/departements" @{nom="Génie Industriel"; code=$codeDep; localisation="Bâtiment D"} 201
$results += $t3
$newDepId = $t3.RawData.id

$t4 = Execute-Test "1.4 PUT Modifier un département" "PUT" "/departements/$newDepId" @{nom="Génie Industriel et Logistique"; code=$codeDep; localisation="Bâtiment D - Étage 1"} 200
$results += $t4

$t5 = Execute-Test "1.5 DELETE Département créé" "DELETE" "/departements/$newDepId" $null 204
$results += $t5

$t6 = Execute-Test "1.6 GET Département inexistant (404)" "GET" "/departements/99999" $null 404
$results += $t6

$t7 = Execute-Test "1.7 POST Département validation invalide (400)" "POST" "/departements" @{nom=""; code=""} 400
$results += $t7

# 2. ENSEIGNANTS
$t8 = Execute-Test "2.1 GET Tous les enseignants" "GET" "/enseignants" $null 200
$results += $t8

$t9 = Execute-Test "2.2 GET Enseignant par ID" "GET" "/enseignants/1" $null 200
$results += $t9

$t10 = Execute-Test "2.3 GET Enseignants par département" "GET" "/enseignants/departement/1" $null 200
$results += $t10

$emailEns = "prof." + (Get-Random -Minimum 100 -Maximum 999) + "@ecole.tn"
$t11 = Execute-Test "2.4 POST Créer un enseignant" "POST" "/enseignants" @{nom="Hammami"; prenom="Firas"; email=$emailEns; specialite="Sécurité Systèmes"; telephone="+216 20 888 999"; departement=@{id=1}} 201
$results += $t11

$t12 = Execute-Test "2.5 POST Enseignant email invalide (400)" "POST" "/enseignants" @{nom="Test"; prenom="Prof"; email="invalid-email-format"; specialite="Math"} 400
$results += $t12

$t13 = Execute-Test "2.6 GET Enseignant inexistant (404)" "GET" "/enseignants/99999" $null 404
$results += $t13

# 3. SALLES
$t14 = Execute-Test "3.1 GET Toutes les salles" "GET" "/salles" $null 200
$results += $t14

$t15 = Execute-Test "3.2 GET Salle par ID" "GET" "/salles/1" $null 200
$results += $t15

$numSalle = "Lab-Test-" + (Get-Random -Minimum 100 -Maximum 999)
$t16 = Execute-Test "3.3 POST Créer une salle" "POST" "/salles" @{numero=$numSalle; capacite=25; type="Laboratoire Réseau"} 201
$results += $t16

$t17 = Execute-Test "3.4 POST Salle capacité invalide (400)" "POST" "/salles" @{numero="Salle-Invalide"; capacite=0; type="TD"} 400
$results += $t17

$t18 = Execute-Test "3.5 GET Salle inexistante (404)" "GET" "/salles/99999" $null 404
$results += $t18

# 4. COURS & REQUETES PERSONNALISEES 1 & 2
$t19 = Execute-Test "4.1 GET Tous les cours" "GET" "/cours" $null 200
$results += $t19

$t20 = Execute-Test "4.2 GET Cours par ID" "GET" "/cours/1" $null 200
$results += $t20

$codeCours = "CS-TEST-" + (Get-Random -Minimum 100 -Maximum 999)
$t21 = Execute-Test "4.3 POST Créer un cours" "POST" "/cours" @{titre="DevOps & Docker CI/CD"; code=$codeCours; volumeHoraire=40; credits=5; departement=@{id=1}; enseignant=@{id=1}} 201
$results += $t21

$t22 = Execute-Test "4.4 POST Affecter Salle à Cours (ManyToMany)" "POST" "/cours/1/salles/3" $null 200
$results += $t22

$t23 = Execute-Test "4.5 [REQUETE PERSO 1] Cours par département" "GET" "/cours/departement/1" $null 200
$results += $t23

$t24 = Execute-Test "4.6 [REQUETE PERSO 2] Cours par salle" "GET" "/cours/salle/1" $null 200
$results += $t24

$t25 = Execute-Test "4.7 POST Cours volume horaire négatif (400)" "POST" "/cours" @{titre="Test"; code="ERR-01"; volumeHoraire=-10; credits=3} 400
$results += $t25

# 5. ETUDIANTS & REQUETE PERSONNALISEE 4 (BONUS)
$t26 = Execute-Test "5.1 GET Tous les étudiants" "GET" "/etudiants" $null 200
$results += $t26

$t27 = Execute-Test "5.2 GET Étudiant par ID" "GET" "/etudiants/1" $null 200
$results += $t27

$matEtu = "ETU-" + (Get-Random -Minimum 1000 -Maximum 9999)
$emailEtu = "etu." + (Get-Random -Minimum 1000 -Maximum 9999) + "@etudiant.tn"
$t28 = Execute-Test "5.3 POST Créer un étudiant" "POST" "/etudiants" @{matricule=$matEtu; nom="Dridi"; prenom="Meriem"; email=$emailEtu; dateNaissance="2002-11-20"} 201
$results += $t28

$t29 = Execute-Test "5.4 [REQUETE PERSO 4] Étudiants inscrits à un cours" "GET" "/etudiants/cours/1" $null 200
$results += $t29

$t30 = Execute-Test "5.5 POST Étudiant doublon matricule (400)" "POST" "/etudiants" @{matricule="ETU-2026-001"; nom="Autre"; prenom="Autre"; email="unique@mail.tn"} 400
$results += $t30

# 6. INSCRIPTIONS & REQUETE PERSONNALISEE 3
$t31 = Execute-Test "6.1 GET Toutes les inscriptions" "GET" "/inscriptions" $null 200
$results += $t31

$t32 = Execute-Test "6.2 GET Inscription par ID" "GET" "/inscriptions/1" $null 200
$results += $t32

$t33 = Execute-Test "6.3 POST Créer une inscription" "POST" "/inscriptions" @{etudiant=@{id=2}; cours=@{id=2}; dateInscription="2026-10-05"; note=18.5; statut="VALIDE"} 201
$results += $t33

$t34 = Execute-Test "6.4 [REQUETE PERSO 3] Inscriptions VALIDE note >= 15.0" "GET" "/inscriptions/recherche?statut=VALIDE&minNote=15.0" $null 200
$results += $t34

$t35 = Execute-Test "6.5 [REQUETE PERSO 3] Inscriptions VALIDE note >= 17.5" "GET" "/inscriptions/recherche?statut=VALIDE&minNote=17.5" $null 200
$results += $t35

$t36 = Execute-Test "6.6 POST Inscription note > 20 (400)" "POST" "/inscriptions" @{etudiant=@{id=1}; cours=@{id=1}; dateInscription="2026-10-01"; note=22.5; statut="VALIDE"} 400
$results += $t36

$t37 = Execute-Test "6.7 GET Inscription inexistante (404)" "GET" "/inscriptions/99999" $null 404
$results += $t37

Write-Host "`n=================== RÉSULTATS DÉTAILLÉS DES TESTS ===================" -ForegroundColor Yellow
$results | Format-Table -Property TestName, Method, StatusCode, ExpectedCode, Verdict -AutoSize

$total = $results.Count
$successCount = ($results | Where-Object { $_.Verdict -like "*SUCCESS*" }).Count
$failCount = $total - $successCount

Write-Host "Total tests : $total | Réussis : $successCount | Échoués : $failCount" -ForegroundColor Green

$results | Select-Object TestName, Method, Endpoint, StatusCode, ExpectedCode, Verdict, ResponsePreview | Export-Csv -Path "test_results.csv" -NoTypeInformation -Encoding UTF8
