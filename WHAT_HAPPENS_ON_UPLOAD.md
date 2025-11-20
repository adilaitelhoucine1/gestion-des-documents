# 🚀 Ce qui se passe quand vous faites POST /api/documents/upload

## 📊 Vue d'ensemble du processus

Quand vous envoyez une requête POST avec un fichier et des données JSON, voici EXACTEMENT ce qui se passe :

---

## 🔄 Flux d'exécution étape par étape

### 1️⃣ **Réception de la requête (DocumentController)**
```
POST http://localhost:8080/api/documents/upload
Headers: Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: multipart/form-data

Body:
  - Part 1 (document): JSON avec les métadonnées
  - Part 2 (file): Fichier binaire (PDF/JPG/PNG)
```

**Ce qui arrive :**
- Le contrôleur reçoit la requête
- Le filtre JWT vérifie le token et authentifie l'utilisateur
- Spring valide automatiquement le JSON (@Valid annotation)

---

### 2️⃣ **Validation des données JSON (Automatic)**

Spring Boot valide automatiquement :
```java
✓ numeroPiece: Obligatoire, max 100 caractères
✓ type: Doit être FACTURE_ACHAT, FACTURE_VENTE, TICKET_CAISSE, RELEVE_BANCAIRE, ou AUTRE
✓ datePiece: Obligatoire, ne peut pas être dans le futur
✓ montant: Obligatoire, doit être > 0, max 13 chiffres avant virgule, 2 après
✓ exerciceComptable: Obligatoire, format YYYY (ex: 2024)
✓ fournisseur: Optionnel, max 255 caractères
✓ categorieComptable: Optionnel, max 100 caractères
✓ commentaireComptable: Optionnel, max 1000 caractères
```

**Si validation échoue :**
```json
HTTP 400 Bad Request
{
    "message": "Le montant doit être supérieur à 0"
}
```

---

### 3️⃣ **Appel du Service (DocumentServiceImpl)**

Le contrôleur appelle :
```java
documentService.uploadDocument(request, file, userEmail)
```

**Le service effectue 5 validations sur le fichier :**

#### ✅ Validation 1 : Fichier existe
```java
if (file == null || file.isEmpty())
    → Erreur: "Le fichier est vide ou n'existe pas"
```

#### ✅ Validation 2 : Taille du fichier
```java
if (file.getSize() > 10MB)
    → Erreur: "La taille du fichier dépasse la limite de 10MB"
```

#### ✅ Validation 3 : Extension du fichier
```java
Extension autorisées: .pdf, .jpg, .jpeg, .png
if (!autorisé)
    → Erreur: "Type de fichier non autorisé. Seuls les fichiers PDF, JPG, JPEG et PNG sont acceptés"
```

#### ✅ Validation 4 : Type MIME
```java
MIME autorisés: application/pdf, image/jpeg, image/jpg, image/png
if (!autorisé)
    → Erreur: "Type MIME non autorisé..."
```

#### ✅ Validation 5 : Utilisateur et Société
```java
Utilisateur user = chercher par email
if (user non trouvé)
    → Erreur: "Utilisateur non trouvé"

if (user.getSociete() == null)
    → Erreur: "L'utilisateur doit être associé à une société"
```

---

### 4️⃣ **Création de l'entité Document**

Si toutes les validations passent :

```java
Document document = new Document();

// Données du formulaire
document.setNumeroPiece("FAC-2024-001");
document.setType(TypeDocument.FACTURE_ACHAT);
document.setCategorieComptable("Achats de marchandises");
document.setDatePiece(LocalDate.parse("2024-11-15"));
document.setMontant(new BigDecimal("1250.50"));
document.setFournisseur("Fournisseur XYZ");
document.setExerciceComptable("2024");
document.setCommentaireComptable("Achat de matériel informatique");

// Métadonnées du fichier
document.setNomFichierOriginal("facture_exemple.pdf");
document.setTypeFichier("application/pdf");
document.setTailleFichier(245678L); // en octets

// Relations
document.setSociete(user.getSociete());
document.setUploadePar(user);

// Statut par défaut
document.setStatut(StatutDocument.EN_ATTENTE);

// Timestamps (auto via @PrePersist)
document.setDateCreation(LocalDateTime.now());
document.setDateModification(LocalDateTime.now());
```

---

### 5️⃣ **Sauvegarde du fichier sur le disque**

Le système génère un nom unique pour éviter les conflits :

```java
String originalFilename = "facture_exemple.pdf"
String extension = "pdf"
String uniqueFilename = UUID.randomUUID() + ".pdf"
// Résultat: "a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf"
```

**Création du répertoire si nécessaire :**
```bash
mkdir -p uploads/
```

**Sauvegarde physique :**
```java
Path uploadPath = Paths.get("uploads/")
Path filePath = uploadPath.resolve(uniqueFilename)
Files.copy(file.getInputStream(), filePath, REPLACE_EXISTING)

// Chemin final: uploads/a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf
```

**Le chemin est enregistré dans la base :**
```java
document.setCheminFichier("uploads/a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf");
```

---

### 6️⃣ **Sauvegarde dans la base de données H2**

```sql
INSERT INTO documents (
    id,
    numero_piece,
    type,
    categorie_comptable,
    date_piece,
    montant,
    fournisseur,
    chemin_fichier,
    nom_fichier_original,
    type_fichier,
    taille_fichier,
    statut,
    exercice_comptable,
    commentaire_comptable,
    societe_id,
    uploade_par_id,
    valide_par_id,
    date_creation,
    date_modification,
    date_validation
) VALUES (
    1,                                                    -- Auto-increment
    'FAC-2024-001',                                      
    'FACTURE_ACHAT',                                     
    'Achats de marchandises',                            
    '2024-11-15',                                        
    1250.50,                                             
    'Fournisseur XYZ',                                   
    'uploads/a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf', 
    'facture_exemple.pdf',                               
    'application/pdf',                                   
    245678,                                              
    'EN_ATTENTE',                                        
    '2024',                                              
    'Achat de matériel informatique',                   
    1,                                                    -- ID de la société
    1,                                                    -- ID de l'utilisateur
    NULL,                                                 -- Pas encore validé
    '2024-11-19 10:30:45',                               
    '2024-11-19 10:30:45',                               
    NULL                                                  -- Pas encore validé
);
```

---

### 7️⃣ **Réponse HTTP retournée**

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
    "id": 1,
    "numeroPiece": "FAC-2024-001",
    "type": "FACTURE_ACHAT",
    "categorieComptable": "Achats de marchandises",
    "datePiece": "2024-11-15",
    "montant": 1250.50,
    "fournisseur": "Fournisseur XYZ",
    "nomFichierOriginal": "facture_exemple.pdf",
    "typeFichier": "application/pdf",
    "tailleFichier": 245678,
    "statut": "EN_ATTENTE",
    "exerciceComptable": "2024",
    "dateCreation": "2024-11-19T10:30:45",
    "message": null
}
```

---

## 📂 État du système après l'upload

### Sur le disque :
```
/home/ad/Desktop/gestion-des-documents/
├── uploads/
│   └── a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf  ← Votre fichier uploadé
├── src/
├── pom.xml
└── ...
```

### Dans la base de données H2 :
```
Table: documents
+----+---------------+-----------------+---------------------------+
| id | numero_piece  | type            | statut                    |
+----+---------------+-----------------+---------------------------+
| 1  | FAC-2024-001  | FACTURE_ACHAT   | EN_ATTENTE                |
+----+---------------+-----------------+---------------------------+

Table: societes
+----+-----------------+
| id | raison_sociale  |
+----+-----------------+
| 1  | Société ABC     |
+----+-----------------+

Table: utilisateurs
+----+----------------------+-----------+
| id | email                | societe_id|
+----+----------------------+-----------+
| 1  | user1@example.com    | 1         |
+----+----------------------+-----------+
```

---

## 🎯 Ce qui se passe APRÈS l'upload

### 1. **Le document attend validation**
- Statut: `EN_ATTENTE`
- Visible par la société qui l'a uploadé
- Visible par tous les comptables

### 2. **Un comptable peut :**

#### Option A: Valider le document
```http
PUT /api/comptable/documents/1/validate
```
→ Statut change à `VALIDE`
→ `date_validation` = maintenant
→ `valide_par_id` = ID du comptable

#### Option B: Rejeter le document
```http
PUT /api/comptable/documents/1/reject
Body: {"reason": "Document incomplet"}
```
→ Statut change à `REJETE`
→ Commentaire du rejet sauvegardé

---

## 🔍 Logs de l'application

Dans la console Spring Boot, vous verrez :

```log
2024-11-19 10:30:45.123  INFO --- [nio-8080-exec-1] o.e.g.s.i.DocumentServiceImpl            : Répertoire de téléchargement créé: /home/ad/Desktop/gestion-des-documents/uploads
2024-11-19 10:30:45.234  INFO --- [nio-8080-exec-1] o.e.g.s.i.DocumentServiceImpl            : Fichier sauvegardé: /home/ad/Desktop/gestion-des-documents/uploads/a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf
Hibernate: insert into documents (categorie_comptable, chemin_fichier, commentaire_comptable, date_creation, date_modification, date_piece, date_validation, exercice_comptable, fournisseur, montant, nom_fichier_original, numero_piece, societe_id, statut, taille_fichier, type, type_fichier, uploade_par_id, valide_par_id) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
2024-11-19 10:30:45.345  INFO --- [nio-8080-exec-1] o.e.g.s.i.DocumentServiceImpl            : Document uploadé avec succès: ID=1, Fichier=facture_exemple.pdf
```

---

## ⚠️ Scénarios d'erreur courants

### Erreur 1: Fichier trop volumineux (11MB)
```http
HTTP 400 Bad Request
{
    "message": "La taille du fichier dépasse la limite de 10MB"
}
```
**Rien n'est sauvegardé** (ni fichier, ni base de données)

---

### Erreur 2: Mauvais type de fichier (.docx)
```http
HTTP 400 Bad Request
{
    "message": "Type de fichier non autorisé. Seuls les fichiers PDF, JPG, JPEG et PNG sont acceptés"
}
```
**Rien n'est sauvegardé**

---

### Erreur 3: Données invalides (montant négatif)
```http
HTTP 400 Bad Request
{
    "message": "Le montant doit être supérieur à 0"
}
```
**Rien n'est sauvegardé**

---

### Erreur 4: Token JWT manquant/invalide
```http
HTTP 401 Unauthorized
{
    "error": "Unauthorized",
    "message": "Token manquant ou invalide"
}
```
**La requête n'atteint même pas le contrôleur**

---

### Erreur 5: Utilisateur sans société
```http
HTTP 400 Bad Request
{
    "message": "L'utilisateur doit être associé à une société"
}
```
**Rien n'est sauvegardé**

---

## 🧪 Test en direct avec Postman

### Étape 1: Login
```
POST http://localhost:8080/api/auth/login
Body: {"email": "user1@example.com", "password": "password123"}

Réponse:
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "email": "user1@example.com",
    "roles": ["ROLE_SOCIETE"]
}
```

### Étape 2: Upload
```
POST http://localhost:8080/api/documents/upload
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: multipart/form-data

Form Data:
  document (JSON): {données du formulaire}
  file (File): [Sélectionnez votre fichier]

Réponse (201):
{
    "id": 1,
    "numeroPiece": "FAC-2024-001",
    "statut": "EN_ATTENTE",
    ...
}
```

---

## 📊 Résumé visuel

```
┌─────────────────┐
│   POSTMAN       │
│   Upload File   │
└────────┬────────┘
         │
         ↓
┌─────────────────────────────┐
│  JWT Filter                 │
│  ✓ Vérifie le token         │
└────────┬────────────────────┘
         │
         ↓
┌─────────────────────────────┐
│  DocumentController         │
│  ✓ Reçoit multipart data    │
│  ✓ Valide @Valid            │
└────────┬────────────────────┘
         │
         ↓
┌─────────────────────────────┐
│  DocumentServiceImpl        │
│  ✓ Valide fichier (taille)  │
│  ✓ Valide fichier (type)    │
│  ✓ Vérifie utilisateur      │
│  ✓ Vérifie société          │
└────────┬────────────────────┘
         │
         ├─────────────────────────┐
         ↓                         ↓
┌──────────────────┐     ┌──────────────────┐
│  Système fichiers│     │  Base de données │
│  uploads/        │     │  H2              │
│  UUID.pdf        │     │  INSERT...       │
└──────────────────┘     └──────────────────┘
         │                         │
         └──────────┬──────────────┘
                    ↓
         ┌─────────────────────┐
         │  Réponse JSON 201   │
         │  Created            │
         └─────────────────────┘
```

---

## ✨ Points importants

1. **Transaction atomique** : Si une erreur survient, RIEN n'est sauvegardé (ni fichier ni base)
2. **UUID unique** : Chaque fichier a un nom unique, pas de conflits possibles
3. **Nom original conservé** : Pour afficher le bon nom à l'utilisateur
4. **Statut EN_ATTENTE** : Tous les nouveaux documents attendent validation
5. **Logs détaillés** : Chaque étape est loggée pour le debugging
6. **Validation stricte** : 9 validations différentes avant sauvegarde
7. **Sécurisé** : JWT obligatoire + utilisateur doit avoir une société

---

## 🎓 Pour vérifier que tout fonctionne

1. Démarrez l'application : `./mvnw spring-boot:run`
2. Importez la collection Postman
3. Faites Login pour obtenir le token
4. Uploadez un fichier
5. Vérifiez le dossier `uploads/` → Le fichier doit y être
6. Connectez-vous à H2 Console : http://localhost:8080/h2-console
7. Exécutez : `SELECT * FROM documents;`

**Vous devriez voir votre document avec statut EN_ATTENTE !** ✅

