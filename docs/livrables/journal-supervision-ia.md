# Journal de supervision IA

**Projet :** MDD, Monde Du Dev  
**Version :** MVP option B  
**Dernière mise à jour :** 30/08/2026

## 1. Objet et source

Ce document synthétise la supervision de l'assistant Codex pendant le projet. Le [journal détaillé dans Notion](https://app.notion.com/p/Projet-5-Prenez-en-charge-le-d-veloppement-d-une-application-full-stack-compl-te-Journal-IA-3b56820f18a0804ea33bd639c0e17d2e) contient 16 journaux thématiques et 55 tâches renseignées : 18 propositions ont été conservées et 33 ont été retravaillées après relecture humaine. Quatre entrées correspondent à une décision non finalisée, à un renvoi vers un autre journal ou à une idée laissée hors périmètre.

L'objectif n'était pas de déléguer le projet sans contrôle, mais de confier des tâches bornées, de demander des explications et des tests, puis de valider, corriger ou écarter les propositions.

## 2. Méthode de supervision

Chaque échange suivi applique le même principe :

1. le besoin, le périmètre et les contraintes sont formulés avant l'action ;
2. l'IA propose une analyse, des tests ou une implémentation limitée ;
3. la proposition est relue et une décision humaine est notée, conservée ou retravaillée ;
4. lorsque du code est accepté, les tests ou contrôles adaptés sont exécutés avant le commit ;
5. les fonctionnalités hors MVP restent non implémentées.

Cette méthode a notamment conduit à demander des tests avant plusieurs implémentations, à corriger des propositions d'interface, à conserver le cookie JWT non lisible côté Angular et à écarter la validation d'inscription par e-mail du MVP.

## 3. Synthèse des travaux supervisés

| Journal | Tâches confiées à l'IA | Décision et contrôle humain |
|---|---|---|
| Cadrage initial | Analyse des consignes, périmètre, feuille de route et préparation du document de choix. | Les premières propositions ont été relues puis recadrées à deux reprises pour maintenir le MVP et les preuves attendues. |
| Socle backend | Configuration Spring Boot, README, Docker Compose MySQL, formatage et outils qualité Java. | Les choix de socle ont été conservés ou ajustés, notamment pour l'outillage et les conventions. |
| Socle frontend | Analyse de la migration Angular 14 vers Angular 22 et échafaudage. | La reconstruction depuis un squelette Angular 22 a été conservée après examen de la dette du projet initial. |
| Modèle de données | Choix de l'outil de modélisation et séparation schéma initial / données d'amorçage. | dbdiagram.io a été retenu après discussion et le découpage des migrations a été validé. |
| Sécurité backend | TDD de la chaîne de sécurité, JWT en cookie HttpOnly, CSRF, inscription, connexion, déconnexion et OpenAPI. | Les propositions ont été validées ou retravaillées après tests. La validation d'inscription par e-mail a été laissée hors MVP. |
| Gestion des erreurs | Format d'erreurs backend et affichage côté frontend. | La solution a été retravaillée avant validation afin de conserver des messages compréhensibles sans fuite technique. |
| PrimeNG et premier écran d'authentification | Analyse de PrimeNG 22, thème et premier composant. | La direction retenue a été ajustée après vérification des dépendances et du rendu. |
| Inscription | Composant, validation, appel API et règle de mot de passe. | Les choix d'interface et d'intégration ont été relus ; la règle de mot de passe côté backend a été conservée. |
| Connexion | Composant, validation et appel API. | L'implémentation a été retravaillée pour rester cohérente avec l'inscription et le contrat serveur. |
| Persistance de session | État de session, déconnexion, rechargement et expiration. | La solution a été construite progressivement, avec confirmation serveur par `/api/users/me` plutôt que lecture du cookie HttpOnly. |
| Navigation | Navigation desktop et mobile conforme aux maquettes. | La proposition a été retravaillée et contrôlée pour les liens, la sémantique et l'accessibilité. |
| Thèmes et abonnements | API, liste des thèmes, abonnement, profil et désabonnement. | Les étapes ont été menées par verticales et retravaillées après contrôle des tests et de l'interface. |
| Profil, lecture de session | Endpoint `/api/users/me` et usage frontend. | Le contrat ne retourne que les données nécessaires au profil, jamais le mot de passe. |
| Mise à jour du profil | Validations partagées, `PATCH /api/users/me`, appel frontend et invalidation de session après changement de mot de passe. | Les étapes ont été revues séparément ; la session versionnée a été conservée pour l'invalidation effective. |
| Articles | TDD de la création, endpoint, formulaire frontend et tri. | Le périmètre a été rappelé : création et consultation uniquement, avec ordre décroissant par défaut. |
| Détail et commentaires | Navigation vers le détail, lecture de l'article, création de commentaire. | L'interface et le câblage API ont été retravaillés avant validation ; le flux final couvre aussi la notification e-mail à l'auteur. |

## 4. Exemples de décisions de supervision

### Ne pas ajouter une couche sans responsabilité réelle

L'IA a été interrogée sur l'usage systématique d'une façade Angular. Après analyse, aucune façade n'a été implémentée : les écrans concernés ne nécessitaient pas de coordonner plusieurs états, actions ou navigations. Une couche de simple délégation a donc été écartée.

### Ne pas exposer le cookie d'authentification au frontend

Le journal montre que la restauration de session a été conçue autour de `GET /api/users/me`. Angular ne lit pas le cookie JWT `HttpOnly`. Cette décision a été conservée car elle respecte la propriété de sécurité du cookie et permet de protéger les routes après rechargement.

### Garder le contrôle du périmètre

La validation d'inscription par e-mail a été évoquée puis laissée en attente. Elle ne fait pas partie des spécifications du MVP et n'a pas été ajoutée pour éviter une fonctionnalité non demandée.

### Corriger au lieu d'accepter la première proposition

Les décisions « retravaillé » documentent la relecture active : cadrage initial, choix PrimeNG, écrans d'authentification, navigation, abonnements, profil, article et détail. Les corrections ont porté sur le périmètre, la sécurité, la cohérence visuelle, la sémantique, les tests et les conventions du dépôt.

## 5. Conclusion

L'IA a servi d'assistant d'analyse, de proposition et d'implémentation bornée. Les décisions structurantes, les modifications de périmètre et l'acceptation du code ont été contrôlées humainement. Les résultats retenus sont ceux qui ont été relus, testés et intégrés au dépôt, non les réponses brutes de l'IA.
