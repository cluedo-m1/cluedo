---
title: "CLUEDO"
subtitle: "Dossier de projet — M1 MIAGE Apprentissage — R1 : dossier de lancement"
author: "Nathan EULLAFFROY, Rafael RODRIGUES, Ilies HAMADENE, Steven KAMEL"
date: "20 octobre 2026"
geometry: margin=2cm
fontsize: 11pt
---

# 01 Équipe, comptes développeurs et dépôt

| Membre | Compte GitHub | Email des commits |
|---------|---------|--------------|
| Nathan EULLAFFROY | @neullaffroy | 46014811@parisnanterre.fr |
| Rafael RODRIGUES | @rdsrafa  | rafi.rodrigues10@gmail.com |
| Ilies HAMADENE | @Deep0315 | deardeep91230@gmail.com |
| Steven KAMEL | @Stevenkml | 43005976@parisnanterre.fr |

# 02 Projet et caractère innovant

## 1. Mise en contexte

Cluedo est un jeu de société d’enquête dans lequel les joueurs cherchent à découvrir qui a commis un crime dans le manoir Tudor, en déduisant la vérité à partir des indices disponibles.

Notre projet reprend ce principe à l’échelle de la faculté. Le jeu propose d’enquêter sur des situations inspirées de problématiques que nous avons rencontrées au cours de nos études.

L’année dernière, dans un cours de programmation orientée objet, un sujet d’examen a fuité, ce qui nous a obligés à repasser l’épreuve. Cet événement constitue un exemple de scénario que notre jeu pourrait proposer.

Les joueurs devront mener leur enquête en déterminant, à partir des indices, qui a pu accéder à certains lieux ou équipements : une salle, une imprimante ou encore un serveur. Le tout se déroule dans un environnement interactif, au sein d’un campus fictif.

## 2. Objectif et problématique

L’objectif du projet est de créer un jeu d’enquête interactif capable de générer automatiquement des scénarios cohérents, tout en permettant aux joueurs de progresser grâce aux indices et aux explications fournies.

**Problématique :**

> Comment générer automatiquement des enquêtes cohérentes, à solution unique et de difficulté maîtrisée, puis expliquer chaque déduction, dans des situations proches du quotidien d’un campus ?

## 3. Ce qui rend notre projet innovant

### 3.1. Une seule bonne réponse

Dans les énigmes conçues manuellement, il peut arriver que plusieurs coupables soient possibles, ou qu’aucune solution ne soit correcte.

Notre programme vérifie chaque enquête avant de la proposer au joueur. Il s’assure qu’elle possède exactement une solution et que les indices disponibles permettent de la retrouver.

### 3.2. Chaque déduction est expliquée

Lorsque le joueur se trompe ou rencontre une difficulté, le jeu ne se contente pas d’indiquer que sa réponse est fausse.

Il présente le raisonnement étape par étape et peut proposer un indice pour aider le joueur à progresser. Le jeu devient ainsi un outil permettant de développer ses capacités de déduction.

### 3.3. Un campus fictif, des incidents réalistes

L’histoire se déroule dans une faculté imaginaire peuplée de personnages inventés.

Les enquêtes s’inspirent de situations réalistes pouvant survenir dans un environnement universitaire, comme une fuite de sujet d’examen, une intrusion sur un serveur ou la disparition de matériel.

### 3.4. Une difficulté réglable

Le jeu doit pouvoir proposer des enquêtes de difficultés différentes.

Il peut notamment intégrer de fausses pistes, avec des suspects qui semblent coupables sans l’être. Cela rend les enquêtes plus réalistes et plus stimulantes, tout en conservant une solution unique.

# 03 Scénarios d'usage

1. En tant que joueur ou joueuse, je lance une nouvelle enquête. L'application génère automatiquement une affaire sous contraintes et vérifie, avant de me la proposer, qu'elle possède exactement une solution unique et qu'elle peut toujours être résolue grâce aux indices fournis.

2. En tant que joueur ou joueuse, je formule des hypothèses sur le suspect, le lieu et l'objet de l'incident. L'application met à jour dynamiquement la liste des possibles en éliminant les pistes incompatibles avec les indices déjà recueillis.

3. En tant que joueur ou joueuse, bloqué(e) dans mon enquête, je demande un indice. L'application me fournit un indice adaptatif calculé pour maximiser le gain d'information sans me donner directement la solution.

4. En tant que joueur ou joueuse, après avoir soumis ma conclusion (ou en cas d'erreur), je demande l'explication du raisonnement. L'application m'affiche la démonstration étape par étape du chemin logique menant à la solution unique.

5. En tant que joueur ou joueuse, j'explore le campus virtuel en consultant la carte et les déplacements horodatés des suspects, puis je croise les témoignages pour repérer les mensonges et les incohérences de planning. L'application contrôle la cohérence spatio-temporelle de chaque trajet à partir des temps de parcours du graphe du campus, et signale toute impossibilité physique de déplacement pour identifier un témoignage mensonger.

6. En tant qu'enseignant ou enseignante, je saisis une série d'indices ou de contraintes sur un incident de campus. L'application vérifie automatiquement que l'enquête est solvable, possède exactement une seule solution et génère une explication complète.

# 04 Plan des features

Une feature a un effet sur la BD, le back, l'API REST et le front. Chaque feature est portée par une seule personne et repose sur un algorithme identifié.

| Version | Steven (Génération) | Rafael (Solveur) | Ilies (Monde) | Nathan (Joueur) |
|------|-----------|-----------|-----------|-----------|
| v0.1.0 24/11 | Affaire tirée sous contraintes — Satisfaction de contraintes (CSP / Backtracking) | Moteur de déduction — Propagation de contraintes (AC-3) et retour arrière | Graphe spatio-temporel du campus — Cheminement sur graphe orienté horodaté | Filtrage d'hypothèses — Réduction dynamique de domaine d'états |
| v0.2.0 08/12 | Indices minimaux à solution unique — Réduction d'ensemble minimal (Unsat Core / Hill-Climbing) | Évaluation de la difficulté — Calcul de profondeur d'arbre de preuve logique | Témoignages et mensonges réfutables — Détection d'incohérences par logique modale | Indice adaptatif — Gain d'information (Entropie de Shannon) |
| v1.0.0 19/01 | Génération à difficulté cible — Recherche locale (Recuit simulé) | Explication pas à pas de la solution — Extraction et linéarisation de preuve formelle | Génération procédurale des plans — Partitionnement binaire d'espace (BSP) | Affaire du jour et score optimal — Pseudo-aléatoire déterministe (Seed) & Distance à la preuve minimale |

# 05 Architecture

# Architecture technique et frameworks envisagés

Notre projet repose sur une **architecture trois tiers** :
un front-end, un back-end exposant une API REST et une
base de données relationnelle.

## Front-end – Angular

Angular permettra de développer l'interface interactive
de notre jeu d'enquête inspiré du Cluedo, situé dans un
campus universitaire fictif. Les joueurs pourront explorer
des scénarios, consulter des indices, interroger les
informations disponibles, formuler des hypothèses et
progresser dans leurs enquêtes.

## Back-end – Java avec Spring Boot

Le back-end constituera le **cœur algorithmique** de notre
application. Il assurera la génération automatique
d'enquêtes cohérentes, la vérification de leur solution
unique et la résolution par déduction logique.

Les algorithmes permettront également d'évaluer la
difficulté des enquêtes, de proposer des indices adaptés
et d'expliquer les raisonnements étape par étape.

Le système pourra aussi vérifier les enquêtes
personnalisées créées par les enseignants.

Une API REST assurera la communication avec Angular
via HTTP et JSON.

## Base de données – PostgreSQL

PostgreSQL stockera les scénarios d'enquête, les suspects,
les lieux, les indices, les solutions ainsi que les
informations relatives aux joueurs et à leur progression.

La communication avec Java pourra utiliser
**Spring Data JPA**.

## Organisation du développement

Nous développerons d'abord le back-end Java, notamment
les algorithmes de génération, de vérification et de
déduction, puis les API REST et le front-end Angular.

Cette séparation facilitera le développement collaboratif,
les tests des algorithmes et la maintenance de l'application.

## Schéma de l'architecture

| Couche | Choix | Remarques |
|------|--------------|--------------|
| Front | Angular | Interface d'enquête : scénarios, indices, hypothèses et progression |
| API REST | Spring Boot | Échanges HTTP/JSON entre Angular et Java |
| Back | Java / Spring Boot | Algorithmes de génération, déduction, vérification de solution unique et difficulté |
| BD | PostgreSQL | Stockage des enquêtes, suspects, indices, solutions et joueurs |

<!-- ![](img/architecture.png){width=45%} -->

# 06 Démarche pour chaque feature

<!--
Responsable : ... [le tableau : 5 étapes, 15 mots par case au plus]
- Les étapes d'une feature, dans l'ordre, de l'idée au front, et pour chacune
  le critère qui dit qu'elle est terminée.
-->

| Étape | Ce qu'on fait | Terminé quand |
|-----|---------------|----------|
|  |  |  |

# 07 Protocole d'usage de l'IA

## Principe :
L’intelligence artificielle est utilisée comme un assistant pour suggérer des solutions, expliquer des notions et relire le code. L’équipe reste responsable de ses choix techniques et de l’ensemble du code intégré au projet. Chaque membre doit être capable d’expliquer le code qu’il soumet, notamment lors de la soutenance.

| Activité | Ce que fait l'IA | Ce que fait l'humain |
|----------|------------------|----------------------|
| Idéation et conception | Aide à approfondir les idées et à proposer des pistes d'amélioration. | Imagine les fonctionnalités, définit les besoins et choisit les solutions à développer. |
| Algorithme (cœur de la feature) | Suggère des algorithmes et explique leur fonctionnement. | Choisit l'algorithme adapté, vérifie sa logique et comprend son fonctionnement. |
| Code technique (API, front, configuration) | Génère des exemples de code et propose des implémentations. | Adapte le code à l'architecture du projet, le vérifie et l'intègre. |
| Tests | Propose des cas de test et peut aider à analyser des résultats. | Exécute les tests, analyse les résultats et vérifie que la fonctionnalité répond aux besoins. |
| Documentation | Aide à rédiger les commentaires, les descriptions et la documentation technique. | Vérifie l'exactitude des informations et valide la documentation finale. |
| Débogage | Analyse les erreurs et suggère des corrections possibles. | Identifie la cause du problème, teste les corrections et valide la solution retenue. |

## Règles et interdictions :
Le code généré par l’IA ne doit jamais être intégré sans vérification et sans compréhension de son fonctionnement. Il est interdit de considérer les réponses de l’IA comme systématiquement correctes ou de lui déléguer les décisions techniques finales. Les membres doivent également éviter de lui transmettre des secrets, des mots de passe ou des informations confidentielles.

## Traçabilité :
Chaque pull request comporte une section « Usage de l’IA » précisant les tâches réalisées avec son aide et les vérifications effectuées par le développeur. Pour chaque livrable, une représentation en araignée de la répartition entre les contributions humaines et celles de l’IA est produite à partir des estimations de l’équipe.

## Utilisation pour le présent dossier :
Pour la rédaction de cette section, l’IA a été utilisée comme aide à la formulation et à la structuration des idées. Le contenu final est relu et validé par le membre responsable.

# Annexe : part IA et part humaine du dossier de lancement

<!--
Responsable : ... [30 mots + le graphe]
1. Fixez en équipe doc/part-ia/plafonds.json : plafonds communs à tous les livrables,
   plus bas pour le haut niveau.
2. Estimez honnêtement doc/part-ia/dossier-lancement.json : ce dossier et la mise
   en place du dépôt.
3. Lancez : python doc/outils/araignee.py doc/part-ia/dossier-lancement.json
4. Retirez les balises de commentaire autour de la ligne d'image ci-dessous.
5. Une phrase de constat : sur quels axes la part IA dépasse-t-elle votre plafond, et pourquoi ?
-->

<!-- ![](part-ia/dossier-lancement.png){width=75%} -->
