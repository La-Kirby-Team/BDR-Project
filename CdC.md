# Cahier des Charges
## Projet : Wine Cave Inventory Management System (Wineventory)

### 1. **Contexte du projet**
Le projet **Wineventory** vise à concevoir une application de gestion de cave à vin, permettant aux utilisateurs de suivre leurs stocks de vins, d'ajouter de nouvelles acquisitions, et de gérer les retraits de bouteilles. L'objectif est de proposer un outil efficace, ergonomique et adapté aux besoins des propriétaires de caves à vin privées ou professionnelles.

---

### 2. **Objectifs du projet**
Le but de ce projet est de :
- Développer une base de données relationnelle pour stocker les informations sur les vins, les fournisseurs, et les transactions.
- Permettre une gestion complète des stocks de vins : ajout, suppression et consultation des niveaux de stock.
- Offrir un suivi détaillé de l’historique des ajouts et des retraits de bouteilles, en indiquant les raisons pour lesquelles une bouteille a été retirée (vendue, offerte, consommée, etc.).
- Alerter l'utilisateur en cas de stock faible afin d'éviter les ruptures.
- Fournir une interface utilisateur intuitive et responsive (desktop ou web) facilitant la gestion quotidienne de la cave.

---

### 3. **Périmètre fonctionnel**
Le système permettra aux utilisateurs de :
- Ajouter de nouvelles bouteilles de vin avec des informations telles que le nom, la variété, l’année, le fournisseur, et le prix.
- Supprimer des bouteilles de l’inventaire en indiquant la raison de la suppression (vendue, offerte, consommée, etc.).
- Suivre les stocks et recevoir des alertes pour les vins dont les quantités passent sous un certain seuil.
- Consulter l’historique complet des mouvements de stock (ajouts et retraits).
- Gérer les informations des fournisseurs de vin.

---

### 4. **Spécifications fonctionnelles**
Les principales fonctionnalités de l'application incluent :
- **Ajout de vins :** L'utilisateur peut enregistrer une nouvelle référence de vin dans le système, en spécifiant le nom, la variété, le millésime, le prix et le fournisseur.
- **Suivi des stocks :** L'application présente une vue en temps réel de la quantité de chaque vin en stock. 
- **Retrait de vins :** Lorsque des bouteilles sont retirées de la cave, l'utilisateur doit indiquer la raison du retrait (vente, consommation, cadeau, etc.).
- **Alertes de stocks faibles :** Le système envoie une alerte lorsque la quantité d'une référence de vin tombe en dessous d’un seuil prédéfini.
- **Historique des mouvements :** Un historique complet des vins ajoutés et retirés est conservé, avec les dates et les raisons des mouvements.
- **Gestion des fournisseurs :** Les utilisateurs peuvent gérer les informations des fournisseurs, y compris leur nom et leurs coordonnées.

---

### 5. **Spécifications non fonctionnelles**
- **Base de données :** Utilisation de **PostgreSQL** pour stocker et gérer les données du système.
- **Performance :** Le système doit être capable de gérer une base de données de plusieurs centaines de références de vins sans ralentissement.
- **Sécurité :** L’accès aux données doit être sécurisé, avec des restrictions basées sur les rôles si nécessaire.
- **Interface utilisateur :** Une interface simple et intuitive pour l'utilisateur final, soit en version desktop (JavaFX) soit en version web (HTML/CSS/Bootstrap).
- **Portabilité :** Le système doit être déployable sur des serveurs locaux ou distants.
  
---

### 6. **Contraintes**
- **Technologies imposées :** L'application doit utiliser PostgreSQL pour la base de données relationnelle.
- **Langages recommandés :** Java pour le backend (JDBC pour les connexions à la base de données), avec des options frontend telles que JavaFX pour desktop ou HTML/CSS pour une version web.
- **Évolutivité :** L'architecture de la base de données doit être conçue de manière à permettre une expansion future (ajout de nouvelles fonctionnalités).

---

### 7. **Livrables**
- **Cahier des charges** au format PDF.
- **Schéma conceptuel** de la base de données au format UML.
- **Schéma relationnel** basé sur le modèle conceptuel.
- **Scripts SQL** pour la création des tables et des contraintes d'intégrité.
- **Application** (code source complet) incluant toutes les fonctionnalités spécifiées.
- **Documentation utilisateur** décrivant comment utiliser l'application.
- **Guide d'installation** pour la base de données et l'application.

---

### 8. **Planning**
- **Livraison du cahier des charges :** 13 octobre 2024
- **Modélisation conceptuelle et relationnelle :** À déterminer
- **Développement de l'application :** En parallèle avec les phases de modélisation
- **Rendu final et présentation du projet :** 24 janvier 2025

---

### 9. **Responsabilités**
- Chaque membre du groupe est responsable de la réalisation de certaines parties du projet (par ex. conception de la base de données, développement de l'interface utilisateur, etc.).
- Une collaboration étroite entre les membres est requise pour assurer l'intégration réussie des différentes composantes du projet.

---

### 10. **Évolutions possibles**
- Intégration d'une version mobile pour permettre une gestion des stocks en déplacement.
- Fonctionnalités avancées de gestion des fournisseurs (historique des commandes, suivi des livraisons).
- Analyse des ventes avec des statistiques et graphiques pour les références les plus populaires.

---

### 11. **Annexes**
- **Diagramme UML** du schéma conceptuel.
- **Script SQL** pour la création des tables et des contraintes d'intégrité.
- **Documentation technique** expliquant les choix de conception.
