# Analyse des besoins frontend

**Projet :** MDD, Monde Du Dev
**Version :** MVP option B
**Dernière mise à jour :** 30/08/2026

## 1. Contexte et objectif

MDD est un réseau social destiné aux développeurs. L'interface doit permettre à un visiteur de créer un compte, puis à un membre authentifié de suivre des thèmes, consulter le fil associé, publier des articles, commenter et gérer son profil.

L'objectif du frontend est de fournir ces parcours de manière simple, responsive et cohérente avec les maquettes fournies. Le périmètre est volontairement limité au MVP : ni rôle administrateur, ni recherche, ni édition ou suppression d'article ou de commentaire ne sont prévus.

## 2. Utilisateurs et tâches prioritaires

| Utilisateur | Objectif | Tâches prioritaires |
|---|---|---|
| Visiteur | Accéder à la communauté | Consulter l'accueil, s'inscrire, se connecter. |
| Membre | Suivre des sujets techniques | Consulter les thèmes, s'abonner, lire son fil. |
| Membre | Partager et échanger | Créer un article, consulter son détail, ajouter un commentaire. |
| Membre | Gérer son compte | Modifier son profil et se désabonner d'un thème. |

## 3. Parcours fonctionnels

### Parcours principal

1. Le visiteur arrive sur la page d'accueil et choisit l'inscription ou la connexion.
2. Après authentification, il consulte la liste des thèmes et s'abonne à ceux qui l'intéressent.
3. Il ouvre le fil des articles issus de ses abonnements, avec un tri par date.
4. Il peut consulter le détail d'un article, le commenter ou publier son propre article.
5. Depuis son profil, il met à jour ses informations et gère ses abonnements.

### Cas alternatifs et erreurs

- un membre non authentifié qui accède à une route privée est redirigé vers la connexion ;
- un formulaire incomplet ou invalide ne peut pas être envoyé et affiche un retour associé au champ ;
- une erreur renvoyée par l'API est affichée sans exposer de détail technique ;
- un article absent affiche une page dédiée avec un retour vers le fil ;
- le menu mobile peut être ouvert puis fermé au clavier avec Échap ou par clic hors du panneau.

## 4. Fonctionnalités du MVP et critères d'acceptation

| Fonctionnalité | Critères d'acceptation observables |
|---|---|
| Inscription et connexion | Les formulaires demandent les informations nécessaires, empêchent l'envoi invalide et ouvrent une session après une réponse API valide. |
| Déconnexion et session | La navigation affiche les actions du membre connecté ; la déconnexion ferme la session et les routes privées sont protégées. |
| Thèmes et abonnements | Chaque carte indique si le membre est déjà abonné ; un abonnement met à jour l'état affiché ; le désabonnement est disponible depuis le profil. |
| Fil | Seuls les articles des thèmes suivis sont chargés ; le tri chronologique peut être inversé ; chaque carte ouvre le détail. |
| Article et commentaire | Un membre choisit un thème, saisit un titre et un contenu pour publier ; le détail affiche les commentaires et permet d'en ajouter un. |
| Profil | Le membre visualise et modifie son nom d'utilisateur, son adresse e-mail et, s'il le souhaite, son mot de passe. |
| Responsive et navigation | Les écrans fonctionnent sur desktop et mobile ; les liens de navigation sont partagés entre les deux présentations, le logo ramène systématiquement au fil et le menu mobile est accessible au clavier. |

Les captures correspondantes sont disponibles dans [`captures-ecran/`](captures-ecran/), avec huit écrans desktop et neuf captures mobile, dont le menu mobile ouvert.

## 5. Interfaces, composants et adaptation responsive

Les routes publiques sont `/`, `/login` et `/register`. Les routes `/posts`, `/posts/create`, `/posts/:postId`, `/topics` et `/profile` nécessitent une session. Les écrans sont organisés par domaines Angular : `auth`, `topic`, `post` et `user`, avec des composants communs pour l'en-tête, la navigation, les formulaires, les erreurs et les icônes.

Sur desktop, la navigation est directement visible. Sur mobile, elle est regroupée dans un panneau ouvert par le bouton "burger". Les mêmes liens fonctionnels sont utilisés dans les deux présentations. Le logo de l'en-tête est un lien permanent vers le fil : il permet de retrouver rapidement la liste des articles depuis n'importe quel écran authentifié. Les formulaires possèdent des libellés, des messages d'erreur annoncés et un indicateur de focus visible.

## 6. Contrat frontend-backend et sécurité

Les services Angular typés communiquent avec l'API REST sous `/api`. Le contrat détaillé, les schémas et les exemples sont consultables dans Swagger UI. Les routes principales couvrent l'authentification, le profil courant, les thèmes et abonnements, les articles et les commentaires.

L'authentification repose sur un JWT conservé dans un cookie `HttpOnly`, donc non lisible par le code Angular. Au chargement, le frontend restaure la session avec `GET /api/users/me`. Les requêtes qui modifient l'état initialisent puis transmettent le jeton CSRF via le cookie `XSRF-TOKEN` et l'en-tête `X-XSRF-TOKEN`.

## 7. Validation, qualité et limites assumées

Les formulaires utilisent les formulaires réactifs Angular. Le mot de passe d'inscription doit comporter de 8 à 72 caractères, avec majuscule, minuscule, chiffre et caractère spécial. Les règles sont aussi contrôlées côté backend.

Les parcours sont couverts par des tests Vitest unitaires et d'intégration, des scénarios Cypress et des vérifications API Bruno. Les rapports générés sont référencés dans le [README de couverture](../reports/coverage/README.md).
