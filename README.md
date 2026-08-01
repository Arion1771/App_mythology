# Mythos

## English

**Mythos** is an offline Android reference and quiz app about world mythologies (Greek, Norse, Egyptian, Arthurian, and more).

The built-in database catalogs mythological entities (gods, heroes, monsters…), places and artifacts. You can browse and search it freely, open the detailed sheet for any entry, and add or edit entries directly from the app.

Three quiz modes let you test your knowledge — Classic (a clue, guess the name), Multiple-choice (same clue, pick the right name among 4), and List (find every entry matching a chosen theme) — available across entities, places and artifacts depending on the mode, each with several difficulty levels or size-based attempt limits. A dedicated help screen (the "?" icon on the quiz selection screen) explains exactly how each one works.

Two other files in this repository are worth knowing about: [CHANGELOG.md](CHANGELOG.md) tracks the full version history of the project, and [Base.md](Base.md) is a complete, always up-to-date inventory of every entity, place and artifact currently in the database, including the theme catalog used by the List quiz.

### Latest addition — V3.0.1

The quiz selection screen now leads with the three quiz types (Multiple-choice, Classic, List, in that order); the domain (Entities, Artifacts, Places — whichever applies to that type) is chosen on the next screen instead of the other way around. The Multiple-choice mode now shows a dedicated result screen after each answer, right or wrong, with the colored name and full details plus a "next question" button, matching the Classic mode instead of auto-advancing after a brief flash.

### Getting started

The app isn't published on the Play Store — it has to be built and installed manually, either of two ways.

#### Method 1 — USB debugging from Android Studio

1. On your Android phone: enable Developer options (Settings → About phone → tap "Build number" 7 times), then enable USB debugging under Settings → Developer options.
2. Connect the phone to your computer via USB and allow debugging when prompted on the phone's screen.
3. Clone this repository, then open the folder in Android Studio.
4. Let Android Studio sync Gradle (progress bar at the bottom of the window).
5. Select your device from the target device dropdown (top of the window), then click ▶ Run.
6. The app builds, installs and launches automatically on your phone.

#### Method 2 — Build and install the debug APK

1. Clone this repository, then open the folder in Android Studio.
2. Let Android Studio sync Gradle.
3. From the menu: Build → Build Bundle(s) / APK(s) → Build APK(s).
4. Once the build finishes, click "locate" in the notification (or find the file at `app/build/outputs/apk/debug/app-debug.apk`).
5. Transfer that `.apk` file to your phone (USB cable, email, cloud storage…).
6. On the phone, allow installing apps from this source if prompted, then open the `.apk` file to install it.
7. Launch the app from the app drawer.

---

## Français

**Mythos** est une application Android de référence et de quiz hors-ligne sur les mythologies du monde (grecque, nordique, égyptienne, arthurienne, et bien d'autres).

La base de données intégrée recense des entités mythologiques (dieux, héros, monstres…), des lieux et des artéfacts. Vous pouvez la parcourir et la rechercher librement, consulter la fiche détaillée de chaque élément, et ajouter ou modifier des entrées directement depuis l'application.

Trois types de quiz permettent de tester vos connaissances — Classique (un indice, devinez le nom), QCM (même indice, choisissez le bon nom parmi 4) et Liste (retrouvez toutes les entrées correspondant à un thème choisi) — disponibles selon le mode sur les entités, les lieux et les artéfacts, chacun avec plusieurs niveaux de difficulté ou des paliers d'essais selon la taille du thème. Un écran d'aide dédié (icône « ? » sur l'écran de choix du quiz) explique en détail le fonctionnement de chacun.

Deux autres fichiers du dépôt sont utiles à connaître : [CHANGELOG.md](CHANGELOG.md) retrace l'historique complet des versions du projet, et [Base.md](Base.md) est un inventaire complet, tenu à jour, de toutes les entités, tous les lieux et tous les artéfacts actuellement dans la base, ainsi que le catalogue des thèmes utilisés par le quiz Liste.

### Dernier ajout — V3.0.1

L'écran de choix des quiz propose désormais d'abord les trois types de quiz (QCM, Classique, Liste, dans cet ordre) ; le domaine (Entités, Artéfacts, Lieux selon ce qui existe pour le type choisi) se sélectionne ensuite, plutôt que l'inverse. Le mode QCM affiche désormais un écran de résultat dédié après chaque réponse, juste ou fausse, avec le nom coloré et toutes les informations, ainsi qu'un bouton « question suivante » — comme le mode Classique, au lieu d'un enchaînement automatique après un bref retour visuel.

### Démarrage

L'application n'est pas publiée sur le Play Store ; il faut la compiler et l'installer manuellement, de deux façons possibles.

#### Méthode 1 — Débogage USB depuis Android Studio

1. Sur votre téléphone Android : activez les options développeur (Paramètres → À propos du téléphone → appuyez 7 fois sur « Numéro de build »), puis activez le débogage USB dans Paramètres → Options pour les développeurs.
2. Connectez le téléphone à l'ordinateur par USB et autorisez le débogage lorsque la demande apparaît sur l'écran du téléphone.
3. Clonez ce dépôt, puis ouvrez le dossier dans Android Studio.
4. Laissez Android Studio synchroniser Gradle (barre de progression en bas de la fenêtre).
5. Sélectionnez votre appareil dans la liste déroulante des appareils cibles (en haut de la fenêtre), puis cliquez sur ▶ Run.
6. L'application se compile, s'installe et se lance automatiquement sur votre téléphone.

#### Méthode 2 — Générer et installer l'APK de débogage

1. Clonez ce dépôt, puis ouvrez le dossier dans Android Studio.
2. Laissez Android Studio synchroniser Gradle.
3. Dans le menu : Build → Build Bundle(s) / APK(s) → Build APK(s).
4. Une fois la compilation terminée, cliquez sur le lien « locate » dans la notification (ou trouvez le fichier dans `app/build/outputs/apk/debug/app-debug.apk`).
5. Transférez ce fichier `.apk` sur votre téléphone (câble USB, e-mail, cloud…).
6. Sur le téléphone, autorisez l'installation d'applications depuis cette source si demandé, puis ouvrez le fichier `.apk` pour l'installer.
7. Lancez l'application depuis le tiroir d'applications.
