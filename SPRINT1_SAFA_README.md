# Sprint 1 — Partie Safa Balloumi (US3, US4, US5, US6)

Projet Android Studio Kotlin. Le backend Spring Boot de la collègue n’est **pas modifié** ni inclus.

## Fichiers concernés
- `ProfileActivity.kt` et `activity_profile.xml` : US3, US4, US5.
- `ForgotPasswordActivity.kt` et `activity_forgot_password.xml` : US6 (saisie e-mail, message si e-mail non existant, envoi d'un lien de réinitialisation).
- `ApiService.kt` : contrats Retrofit existants.
- `RetrofitClient.kt` : cookie de session et URL du serveur existants.

## Bloquants constatés dans le backend fourni
1. `PUT /api/clients/profil` convertit l’objectif en MAJUSCULES alors que l'enum `Client.Objectif` a des noms en minuscules. Les sauvegardes du profil échouent pour tout objectif valide actuel. Correction nécessaire par la propriétaire du backend : utiliser `toLowerCase()` dans `ClientController` pour l'objectif, ou uniformiser l'enum et ses consommateurs.
2. Trois objectifs seulement existent dans `Client.Objectif` : `perdre_poids`, `gagne_poids`, `maintenir_masse`. Le quatrième objectif demandé (améliorer la condition physique) exige une valeur d'enum supplémentaire décidée avec l'équipe.
3. Aucun `GET /api/clients/profil` : après une nouvelle connexion, l'app ne peut pas recharger âge, poids, taille, sexe depuis le serveur. Demander à la collègue un endpoint de lecture du profil pour compléter US3/US4.
4. La réinitialisation par e-mail dépend de la configuration SMTP de Spring Boot ; tester l'envoi réel.

## Exécution
1. Ouvrir le dossier `NutritionFitness` dans Android Studio.
2. Démarrer Spring Boot sur le port 8081.
3. L'émulateur Android utilise `http://10.0.2.2:8081/`. Pour un téléphone physique, adapter l'URL à l'adresse IP LAN du PC.
4. Se connecter avec un compte valide pour récupérer le cookie de session, puis accéder au profil.
5. Tester le formulaire profil et la réinitialisation de mot de passe.

**Attention :** la sauvegarde complète US3–US5 reste bloquée par le backend tant que les points ci-dessus ne sont pas réglés. Le code n'est pas certifié testé sur émulateur dans cet environnement.
