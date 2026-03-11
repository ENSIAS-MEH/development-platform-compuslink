# CampusLink – Plateforme sociale pour étudiants

## Description

CampusLink est une application web conçue pour faciliter la vie étudiante en centralisant plusieurs services utiles dans une seule plateforme.
L’application permet aux étudiants de vendre ou acheter des objets, trouver des colocataires, consulter des offres de stage/PFE ou de jobs étudiants, et interagir avec une communauté étudiante via un système de messagerie et de groupes de discussion.

L’objectif de CampusLink est de créer un **écosystème numérique dédié aux étudiants**, facilitant les échanges, l’entraide et l’accès aux opportunités académiques et professionnelles.

---

# Problématique

Les étudiants qui déménagent vers une nouvelle ville ou commencent leurs études rencontrent plusieurs difficultés :

* Trouver un logement ou un colocataire
* Acheter ou vendre du mobilier étudiant
* Trouver un stage ou un PFE
* Chercher un job étudiant
* S’intégrer dans une nouvelle communauté

Ces informations sont souvent dispersées entre plusieurs plateformes (groupes Facebook, sites d’emploi, forums, etc.).

CampusLink vise donc à **centraliser ces services dans une seule application dédiée aux étudiants**.

---

# Objectifs de l’application

* Faciliter les échanges entre étudiants
* Permettre la vente et l’achat d’objets entre étudiants
* Aider les étudiants à trouver des stages, PFE et jobs étudiants
* Faciliter la recherche de colocataires
* Créer une communauté étudiante interactive

---

# Acteurs du système

## Étudiant

Acteur principal de la plateforme.

Actions possibles :

* créer un compte et gérer son profil
* publier des annonces dans la marketplace
* rechercher ou proposer une colocation
* consulter les offres de stage, PFE et job étudiant
* envoyer des messages à d’autres utilisateurs
* rejoindre ou créer des groupes de discussion

---

## Entreprise / Recruteur

Peut publier des opportunités professionnelles.

Actions possibles :

* publier des offres de stage
* publier des offres de PFE
* publier des jobs étudiants
* consulter les profils étudiants

---

## Administrateur

Responsable de la gestion de la plateforme.

Actions possibles :

* gérer les utilisateurs
* modérer les annonces et contenus
* supprimer les annonces frauduleuses
* surveiller les discussions et signalements

---

# Sections principales de l’application

## 1. Marketplace Étudiante

Section permettant aux étudiants de vendre ou acheter des objets.

Exemples d’objets :

* chaises
* tables
* lits
* livres
* ordinateurs
* électroménager
* vélos

Fonctionnalités :

* publier une annonce avec photos
* fixer un prix
* rechercher des objets
* filtrer par ville ou université
* contacter le vendeur via chat

---

## 2. Colocation

Section permettant aux étudiants de trouver un logement ou un colocataire.

Fonctionnalités :

* publier une annonce de colocation
* rechercher un logement ou un colocataire
* filtrer par ville
* consulter les informations du logement
* contacter l’annonceur via messagerie

---

## 3. Offres (Stages – PFE – Jobs étudiants)

Section dédiée aux opportunités académiques et professionnelles.

Types d’offres :

* stage
* projet de fin d’études (PFE)
* job étudiant

Fonctionnalités :

* publier une offre
* rechercher par domaine
* filtrer par ville
* postuler directement

---

## 4. Messagerie (Chat)

Système de messagerie interne permettant aux utilisateurs de communiquer.

Fonctionnalités :

* chat privé entre utilisateurs
* discussions liées aux annonces
* notifications de messages

---

## 5. Groupes de discussion

Espace communautaire permettant aux étudiants de rejoindre ou créer des groupes.

Exemples :

* groupes par université
* groupes par ville
* groupes par domaine d’étude
* entraide académique

Objectif :
Aider les étudiants à **s’intégrer dans une nouvelle communauté étudiante**.

---

# Architecture technique

## Backend

* Java
* Spring Boot
* Spring Security
* REST API
* JPA / Hibernate

## Frontend

* React.js
* Axios (communication API)
* React Router
* Material UI ou TailwindCSS

## Base de données

* PostgreSQL ou MySQL

## Architecture

Frontend (React) communique avec Backend (Spring Boot) via **API REST**.

---

# Fonctionnalités principales

* authentification et gestion des comptes
* gestion des profils utilisateurs
* publication et recherche d’annonces
* gestion des offres de stage, PFE et jobs
* messagerie entre utilisateurs
* groupes communautaires

---

# Évolutions futures

Améliorations possibles :

* système d’évaluation des utilisateurs
* notifications en temps réel
* géolocalisation des annonces
* recommandation d’offres selon le profil
* version mobile de l’application

---

# Conclusion

CampusLink est une plateforme conçue pour améliorer l’expérience étudiante en réunissant dans une seule application les services essentiels : marketplace, colocation, opportunités académiques et interaction sociale.

L’application permet aux étudiants de **se connecter, collaborer et s’entraider au sein d’une communauté universitaire dynamique**.
