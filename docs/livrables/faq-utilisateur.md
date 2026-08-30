# FAQ utilisateur

**Projet :** MDD, Monde Du Dev  
**Version :** MVP option B  
**Dernière mise à jour :** 30/08/2026

## Compte et connexion

### Comment créer un compte ?

Depuis l'accueil, sélectionnez **S'inscrire**, renseignez votre nom d'utilisateur, votre adresse e-mail et votre mot de passe, puis validez le formulaire. Le mot de passe doit contenir entre 8 et 72 caractères, dont une minuscule, une majuscule, un chiffre et un caractère spécial.

### Comment me connecter ?

Sélectionnez **Se connecter** depuis l'accueil. Saisissez votre adresse e-mail ou votre nom d'utilisateur, puis votre mot de passe. Une fois connecté, vous pouvez accéder aux articles, thèmes et profil.

### J'ai oublié mon mot de passe. Que puis-je faire ?

La réinitialisation de mot de passe par e-mail ne fait pas partie du MVP. Si vous êtes connecté, vous pouvez choisir un nouveau mot de passe depuis votre profil. Sinon, ce cas doit être traité comme une évolution du produit.

### Comment me déconnecter ?

Utilisez **Se déconnecter** dans l'en-tête. Votre session est alors fermée et les pages réservées aux membres ne sont plus accessibles.

## Thèmes et articles

### Comment suivre un thème ?

1. Ouvrez la page **Thèmes**.
2. Choisissez un thème qui vous intéresse.
3. Sélectionnez **S'abonner**.

Le bouton indique **Déjà abonné** lorsque l'abonnement est déjà actif. Les articles de ce thème apparaîtront dans votre fil.

### Comment ne plus suivre un thème ?

Ouvrez votre **Profil**, repérez le thème dans la section **Abonnements**, puis sélectionnez **Se désabonner**.

### Comment publier un article ?

1. Ouvrez la page **Articles** et sélectionnez **Créer un article**.
2. Choisissez un thème, saisissez un titre et le contenu.
3. Sélectionnez **Créer**.

Les trois champs sont obligatoires. L'article est rattaché au thème choisi et à votre compte.

### Comment lire ou trier les articles ?

La page **Articles** affiche le fil des thèmes auxquels vous êtes abonné. Par défaut, les articles sont affichés du plus récent au plus ancien. Sélectionnez une carte pour lire l'article et ses commentaires. Le bouton **Trier par date** inverse temporairement cet ordre ; ce choix n'est pas conservé après avoir quitté ou rechargé la page.

### Comment ajouter un commentaire ?

Ouvrez le détail d'un article, saisissez votre message dans la zone prévue, puis sélectionnez l'icône d'envoi. Le bouton reste désactivé tant que le commentaire est vide. Lorsque le commentaire est publié, l'auteur de l'article reçoit une notification par e-mail.

## Profil

### Comment modifier mes informations ?

Ouvrez **Profil**, modifiez le nom d'utilisateur ou l'adresse e-mail, et saisissez un nouveau mot de passe seulement si vous souhaitez le changer. Sélectionnez **Sauvegarder**. Le bouton devient disponible lorsqu'une modification valide a été effectuée.

## Erreurs et aide

### Pourquoi le bouton de validation est-il désactivé ?

Le formulaire contient probablement un champ manquant ou invalide. Vérifiez les messages affichés sous les champs. Pour un commentaire, saisissez d'abord du texte.

### Pourquoi ne puis-je pas ouvrir une page d'articles ou de thèmes ?

Ces pages nécessitent une session active. Connectez-vous puis réessayez. Si le problème persiste après une déconnexion ou l'expiration de la session, reconnectez-vous.

### La page demandée est introuvable. Que faire ?

Utilisez le lien **Retour aux articles** proposé par l'application, ou revenez à l'accueil. Un article peut aussi être indisponible s'il n'existe pas à l'adresse demandée.

## Données et sécurité

### Quelles données sont utilisées ?

MDD utilise le nom d'utilisateur, l'adresse e-mail et le mot de passe nécessaire à votre compte. Les articles, commentaires et abonnements sont associés à votre compte afin de fournir les fonctionnalités de la communauté.

### Mon mot de passe est-il visible dans l'application ?

Vous pouvez afficher temporairement le mot de passe que vous êtes en train de saisir grâce à l'icône prévue dans le champ. Après l'envoi du formulaire, le mot de passe n'est pas retourné ni affiché dans le profil. Il est stocké sous forme hachée côté serveur.

### Comment ma session est-elle protégée ?

La session repose sur un cookie d'authentification non accessible au code JavaScript de l'application. Les actions qui modifient des données sont protégées par un jeton CSRF. Déconnectez-vous lorsque vous utilisez un appareil partagé.

## Utilisation mobile

### Comment accéder au menu sur mobile ?

Sélectionnez l'icône de menu en haut à droite. Sur un écran étroit, le panneau peut occuper toute la largeur. Pour le fermer, sélectionnez l'arrière-plan lorsqu'il reste visible ou utilisez le bouton Retour du smartphone Android.

Les captures de référence sont disponibles dans [`captures-ecran/mobile/`](captures-ecran/mobile/).
