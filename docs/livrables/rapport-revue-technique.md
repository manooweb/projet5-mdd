# Rapport de revue technique

**Projet :** MDD, Monde Du Dev  
**Périmètre revu :** frontend Angular, API Spring Boot, données, sécurité, tests et qualité locale  
**Version :** MVP option B  
**Dernière mise à jour :** 30/08/2026

## 1. Synthèse

La solution présente une architecture cohérente avec le périmètre du MVP. Les domaines métier sont séparés côté backend, le frontend est organisé par fonctionnalités et l'API constitue une frontière claire entre les deux couches. Les mécanismes d'authentification, de validation, de gestion des erreurs et de tests sont en place.

Les principaux points forts sont la sécurité de session fondée sur un cookie JWT `HttpOnly` avec protection CSRF, les tests à plusieurs niveaux et les contrôles locaux de qualité. Les améliorations prioritaires pour une suite du produit concernent l'automatisation CI, la formalisation des règles de rétention des données et l'évolution fonctionnelle au-delà du MVP.

### Forces constatées

- architecture front et back modulaire, organisée par fonctionnalité ou domaine ;
- contrats REST documentés dans Swagger, DTO distincts des entités et erreurs centralisées ;
- authentification par cookie `HttpOnly`, CSRF, validations et contrôles d'accès ;
- tests unitaires, d'intégration, end-to-end et API, complétés par des outils de formatage, de couverture et d'analyse statique avec SonarQube Cloud ;
- requêtes du fil et des commentaires conçues pour éviter le chargement N+1 des auteurs et thèmes.

### Axes d'amélioration

- absence d'intégration continue pour rejouer automatiquement les contrôles ;
- absence de pagination et de mesure de charge sur le fil, acceptable pour le volume du MVP mais à traiter avant une montée en charge ;
- absence de politique formalisée de suppression, d'export et de conservation des données personnelles ;
- absence de supervision d'exploitation et d'alertes de production.

### Recommandation prioritaire

Avant toute évolution fonctionnelle, mettre en place une intégration continue qui exécute les tests, les contrôles de formatage et la génération des rapports sur chaque changement. Elle rendrait la qualité actuelle reproductible sans contrôle manuel systématique.

## 2. Périmètre et méthode de revue

La revue couvre :

- l'organisation des modules Angular et Spring Boot ;
- les échanges REST, la sécurité et les données ;
- la lisibilité, les conventions et la documentation du code ;
- les tests unitaires, d'intégration, end-to-end et API ;
- les corrections visibles dans l'historique Git.

Les conclusions s'appuient sur le code versionné, les README, les migrations Flyway, la documentation OpenAPI, les rapports de couverture générés et les commandes de vérification. Les taux de couverture et résultats détaillés sont reportés séparément dans le rapport de tests afin de conserver une source de chiffres unique et datée.

## 3. Architecture et conception

### Backend

L'API est un monolithe modulaire Spring Boot organisé par domaine : `authentication`, `topic`, `post`, `comment` et `user`. Chaque domaine regroupe les contrôleurs, DTO, services, dépôts et modèles nécessaires. Les préoccupations transverses, telles que la sécurité, les erreurs, les validations et la configuration, sont isolées dans `system`.

Cette organisation rend lisible le flux `controller → service → repository` et évite d'exposer les entités de persistance dans l'API. Flyway versionne le schéma et les données initiales, tandis que Testcontainers isole la base de données des tests d'intégration.

### Performance des accès aux données

Le fil récupère les articles des thèmes suivis en chargeant explicitement leur auteur et leur thème dans la requête `PostRepository.findAllForSubscribedTopics`. Le détail applique le même principe pour l'article, et `CommentRepository.findAllByPostIdWithAuthor` charge les auteurs des commentaires avec les commentaires. Cette approche évite les requêtes supplémentaires par article ou commentaire lors de la construction des réponses, dites requêtes N+1.

La liste des thèmes charge les identifiants des abonnements de l'utilisateur, puis les thèmes, avant de déterminer l'état d'abonnement en mémoire. Elle évite ainsi une vérification individuelle par thème. Ces choix sont adaptés au MVP ; l'absence de pagination sur le fil reste une limite documentée pour un volume de données plus important.

### Frontend

Le frontend Angular est organisé par fonctionnalités : `auth`, `topic`, `post` et `user`, avec un dossier `shared` pour les composants réutilisables. Les composants sont standalone, le typage TypeScript est strict, les services HTTP sont typés et la détection de changement est zoneless.

## 4. Sécurité et protection des données

L'authentification utilise un JWT signé placé dans un cookie `HttpOnly`. Le frontend ne lit pas le jeton : il rétablit la session avec `GET /api/users/me`. Les opérations qui modifient des données conservent la protection CSRF au moyen du cookie `XSRF-TOKEN` et de l'en-tête `X-XSRF-TOKEN`.

Les mots de passe sont hachés avec BCrypt. Les secrets et mots de passe de configuration sont hors du dépôt, dans un fichier `.env` local créé à partir de `.env.example`. Les validations, les contrôles d'accès liés à l'utilisateur courant et la gestion centralisée des erreurs limitent les entrées invalides et la divulgation d'informations techniques.

La définition détaillée des données et des limites du MVP est disponible dans [l'annexe dédiée](definition-donnees.md).

## 5. Qualité du code et corrections réalisées

| Correction ou amélioration | Décision et impact |
|---|---|
| Règle de mot de passe centralisée | La validation a été extraite dans une contrainte réutilisable côté backend, au lieu de dupliquer les règles entre inscription et profil. |
| Validation frontend simplifiée | La règle de complexité du mot de passe est maintenue dans une source typée et réutilisée par les formulaires concernés. |
| Filtre JWT lisible | Une référence de méthode remplace une lambda inutile dans le filtre d'authentification. |
| Tests plus lisibles | Des assertions enchaînées et des scénarios inatteignables supprimés améliorent la lisibilité et la pertinence de la suite. |
| Navigation responsive | Les liens sont partagés entre les présentations desktop et mobile, avec une action de déconnexion distincte de la navigation. |
| Accessibilité du menu mobile | La fermeture par clic extérieur, Échap au clavier et sélection d'un lien est contrôlée par des tests. |
| Documentation | Les README, la documentation Javadoc, Swagger UI et la collection Bruno ont été actualisés pour correspondre au comportement livré. |

Le formatage Java est contrôlé par Spotless. Le frontend dispose d'ESLint et Prettier. Les rapports de couverture et l'audit SonarQube Cloud font partie des outils de suivi de qualité configurés localement.

La Javadoc cible les contrôleurs REST et leurs endpoints publics lorsque l'information est utile à la maintenance. Les README séparent les responsabilités du frontend, du backend, des rapports de couverture et de Bruno. Les captures, la FAQ et les annexes complètent la documentation destinée à l'évaluation.

## 6. Tests et automatisation

Les contrôles couvrent plusieurs niveaux :

- JUnit et Mockito pour les comportements backend ciblés ;
- Spring Boot, MockMvc et Testcontainers MySQL pour les intégrations backend ;
- Vitest pour les tests unitaires et d'intégration frontend ;
- Cypress pour les parcours navigateur ;
- Bruno pour le contrat API, les cookies et le CSRF dans une stack isolée.

Les commandes et les rapports HTML sont documentés dans le [README de couverture](../reports/coverage/README.md). Les résultats finaux et les taux de couverture sont centralisés dans le rapport de tests et de couverture.

## 7. Axes d'amélioration et recommandations

### Évolutions fonctionnelles

| Axe d'amélioration constaté | Recommandation |
|---|---|
| Aucun parcours de mot de passe oublié. | Ajouter une réinitialisation de mot de passe par e-mail, avec un jeton à durée de vie limitée et à usage unique. |
| L'inscription ne vérifie pas que l'adresse e-mail est réellement accessible. | Envoyer un e-mail de validation après l'inscription, sans bloquer la création initiale du compte. L'adresse est confirmée par un lien à usage unique avant les usages qui nécessitent un e-mail fiable. |
| Une modification sensible du profil ne demande pas de confirmer le mot de passe actuel. | Demander le mot de passe actuel avant de modifier l'adresse e-mail ou le mot de passe. |
| Une nouvelle adresse e-mail est enregistrée directement lors de la mise à jour du profil. | Envoyer un lien de confirmation à la nouvelle adresse et ne mettre à jour la base de données qu'après validation du lien. La connexion par nom d'utilisateur reste possible pendant ce processus. |
| Le fil d'articles n'est pas paginé. | Ajouter une pagination documentée afin de faciliter la consultation d'un volume de contenus plus important. |
| L'adresse d'un article ne contient que son identifiant. | Ajouter un titre adapté aux URL, ou slug, à côté de l'identifiant afin de rendre le lien plus compréhensible sans imposer l'unicité des titres. |

### Améliorations techniques et d'exploitation

| Axe d'amélioration constaté | Recommandation |
|---|---|
| Les contrôles restent déclenchés manuellement. | Ajouter une intégration continue exécutant les tests, le formatage, l'analyse statique et la génération des rapports de couverture à chaque changement. |
| Aucune mesure de charge n'a été menée. | Établir des mesures de performance avant une hausse du volume de contenus. |
| Les chaînes de l'interface sont écrites dans les templates et composants. | Externaliser les libellés, messages et textes d'interface afin de préparer une future internationalisation et de centraliser leur maintenance. |
| La gestion du cycle de vie des données personnelles n'est pas formalisée. | Définir une politique de conservation, d'export et de suppression des données avant toute mise en production. |
| Il n'existe pas de supervision d'exploitation. | Préciser les alertes, la journalisation et la gestion des incidents. |

## 8. Conclusion

La revue confirme que le code livré est structuré, documenté et contrôlé en cohérence avec un MVP. Les ajustements réalisés améliorent la sécurité, la cohérence des formulaires, la lisibilité des tests, l'accessibilité de la navigation et l'efficacité des accès aux données. Les évolutions proposées sont volontairement distinguées du périmètre livré afin de ne pas les présenter comme des fonctionnalités existantes.
