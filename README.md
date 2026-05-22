# 🌸 Fluxia — Application de Suivi du Cycle Menstruel

Application complète de suivi du cycle menstruel, développée avec **Java 17 + Spring Boot 3 + H2 (base de données embarquée)**.
Aucune API externe, tout tourne en local sur ton ordinateur.

---

## 🚀 Démarrage rapide

### Prérequis
- **Java 17+** — Télécharger sur https://adoptium.net/
- **Maven 3.8+** — Télécharger sur https://maven.apache.org/

### Lancer l'application
```bash
cd fluxia
mvn spring-boot:run
```
Puis ouvrir dans le navigateur : **http://localhost:8080**

> La base de données H2 est créée automatiquement dans le dossier `data/`.
> Aucune installation de base de données requise !

---

## ✨ Fonctionnalités

### 📱 Pages de l'application
| Page | URL | Description |
|------|-----|-------------|
| Tableau de bord | `/dashboard` | Phase actuelle, prédictions, mini-calendrier |
| Calendrier | `/calendar` | Vue mensuelle avec toutes les données |
| Journalisation | `/log` | Enregistrement quotidien complet |
| Insights | `/insights` | Statistiques et historique |
| Profil | `/profile` | Paramètres du cycle et compte |

### 🩸 Journalisation quotidienne
- Jours de règles avec intensité du flux (léger, normal, abondant, spotting)
- **8 états d'humeur** : heureuse, triste, anxieuse, irritée, calme, énergique, sensible, fatiguée
- **12 symptômes** : crampes, maux de tête, ballonnements, fatigue, seins sensibles, acné, nausées, douleurs dos, sautes d'humeur, insomnie, fringales, spotting
- Glaire cervicale (sèche, collante, crémeuse, aqueuse, blanc d'œuf)
- Température basale
- Activité sexuelle & contraception
- Notes personnelles

### 📅 Calculs & Prédictions
- Phase actuelle du cycle (menstruelle, folliculaire, ovulation, lutéale)
- Date des prochaines règles
- Fenêtre fertile (5 jours avant ovulation + jour J + 1 jour après)
- Jour d'ovulation (J-14 avant les prochaines règles)
- Jour actuel dans le cycle
- Durée moyenne du cycle calculée sur l'historique

### 📊 Insights
- Statistiques des symptômes (3 derniers mois)
- Statistiques d'humeur
- Durée moyenne du cycle
- Historique détaillé des logs

---

## 🏗️ Architecture

```
fluxia/
├── src/main/java/com/fluxia/
│   ├── FluxiaApplication.java          # Point d'entrée
│   ├── controller/
│   │   ├── AuthController.java         # Login, Register, Onboarding
│   │   ├── DashboardController.java    # Dashboard, Calendrier, Insights, Profil
│   │   ├── LogController.java          # Page de journalisation
│   │   └── ApiController.java          # REST API endpoints
│   ├── model/
│   │   ├── User.java                   # Entité utilisateur
│   │   ├── CycleLog.java              # Log quotidien
│   │   └── Reminder.java              # Rappels
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── CycleLogRepository.java
│   │   └── ReminderRepository.java
│   └── service/
│       ├── UserService.java
│       ├── CycleLogService.java
│       └── CycleCalculatorService.java  # Tous les calculs de cycle
├── src/main/resources/
│   ├── templates/                      # Pages Thymeleaf
│   │   ├── login.html
│   │   ├── register.html
│   │   ├── onboarding.html
│   │   ├── dashboard.html
│   │   ├── log.html
│   │   ├── calendar.html
│   │   ├── insights.html
│   │   └── profile.html
│   ├── static/
│   │   ├── css/main.css               # Design system complet
│   │   └── js/log.js                  # JavaScript journalisation
│   └── application.properties
└── pom.xml
```

---

## 🔧 Configuration

Dans `src/main/resources/application.properties` :

```properties
server.port=8080           # Changer le port si nécessaire
spring.jpa.hibernate.ddl-auto=update   # 'create' pour réinitialiser la BDD
```

### Accès à la console H2 (debug)
Ouvrir : **http://localhost:8080/h2-console**
- JDBC URL : `jdbc:h2:file:./data/fluxiadb`
- Utilisateur : `sa`
- Mot de passe : *(vide)*

---

## 🔒 Sécurité

> **Note** : Cette version utilise des sessions HTTP simples et stocke les mots de passe en clair.
> Pour une version production, ajouter :
> - Spring Security avec BCrypt pour les mots de passe
> - HTTPS
> - Gestion des tokens CSRF

---

## 🎨 Design

- **Police** : Fraunces (serif élégant) + DM Sans
- **Palette** : Rose poudré `#E8829A`, Terracotta `#C97D5A`, Vert sauge `#7BA694`
- **Interface** : Mobile-first, max 430px, navigation bottom-nav style app native
- **Base de données** : H2 fichier local (persist entre les redémarrages)

---

## 📦 Packaging (JAR exécutable)

```bash
mvn clean package -DskipTests
java -jar target/fluxia-1.0.0.jar
```

---

Développé avec ❤️ — Toutes les données restent sur ton appareil.
