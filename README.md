# Mythos

## English

**Mythos** is an offline Android reference and quiz app about world mythologies (Greek, Norse, Egyptian, Arthurian, and more).

The built-in database catalogs mythological entities (gods, heroes, monsters…), places and artifacts, sourced entirely from a bundled reference file rather than editable in-app — you can browse and search it freely and open the detailed sheet for any entry.

Three quiz modes let you test your knowledge — Classic (a clue, guess the name), Multiple-choice (same clue, pick the right name among 4), and List (find every entry matching a chosen theme) — available across entities, places and artifacts depending on the mode, each with several difficulty levels or size-based attempt limits. A dedicated help screen (the "?" icon on the quiz selection screen) explains exactly how each one works.

Two other files in this repository are worth knowing about: [CHANGELOG.md](CHANGELOG.md) tracks the full version history of the project, and [Base.md](Base.md) is a complete, always up-to-date inventory of every entity, place and artifact currently in the database, including the theme catalog used by the List quiz.

### Changes since the last release (V3.2.7)

#### V3.4.1

- Added 3 new level-3 entities: Shango (Yoruba), Hinezumi (Japanese), Huoshu (Chinese)
- Added the 23 gods/monsters/heroes featured in Smite and/or Smite 2 that were still missing from the database, all level 3: Méduse and Arachné (Greek), Bakasura, Kumbhakarna, Rama, Ravana and Vamana (Hindu), Chang'e, Da Ji, Guan Yu, Hou Yi, Jing Wei, Hua Mulan, Sun Wukong, Yu Huang and Zhong Kui (Chinese), Xbalanque (Maya), Sol, Ullr and Ymir (Norse), Danzaburou and Kuzenbo (Japanese), Cu Chulainn (Irish Celtic)
  - Entity (Added): Shango, Hinezumi, Huoshu, Méduse, Arachné, Bakasura, Kumbhakarna, Rama, Ravana, Vamana, Chang'e, Da Ji, Guan Yu, Hou Yi, Jing Wei, Hua Mulan, Sun Wukong, Yu Huang, Zhong Kui, Xbalanque, Sol, Ullr, Ymir, Danzaburou, Kuzenbo, Cu Chulainn

#### V3.4.0

- prepopulate.json becomes the complete database in its final form: at launch, as soon as its content changes (hash comparison), the local database is fully emptied then reloaded exactly as in the JSON, replacing the protective resync of V3.2.8
- `userModified` field (V3.2.8) removed; Room moves to version 8

#### V3.3.0

- Fully removed the in-app feature to manually add, edit and delete an entity, place or artifact ("Ajouter"/"Modifier" screens, 7 fragments and their layouts, related navigation actions): such a change would have been overwritten at the next database reload from prepopulate.json anyway

#### V3.2.8

- prepopulate.json was only read on first launch (empty database), so content fixes and additions published in updates never reached installs already launched once. The database now resyncs with prepopulate.json whenever a change in its content is detected (hash comparison), without ever overwriting an entry created or edited by the user (new `userModified` field, Room v7, non-destructive migration)
- Accepted transitional limitation: rows already in the database before this update start out protected by default, so the fixes already published (V3.2.1 to V3.2.7) will not automatically reach existing installs; only future content will resync normally

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

La base de données intégrée recense des entités mythologiques (dieux, héros, monstres…), des lieux et des artéfacts, entièrement issus d'un fichier de référence embarqué plutôt qu'éditable depuis l'application — vous pouvez la parcourir et la rechercher librement, et consulter la fiche détaillée de chaque élément.

Trois types de quiz permettent de tester vos connaissances — Classique (un indice, devinez le nom), QCM (même indice, choisissez le bon nom parmi 4) et Liste (retrouvez toutes les entrées correspondant à un thème choisi) — disponibles selon le mode sur les entités, les lieux et les artéfacts, chacun avec plusieurs niveaux de difficulté ou des paliers d'essais selon la taille du thème. Un écran d'aide dédié (icône « ? » sur l'écran de choix du quiz) explique en détail le fonctionnement de chacun.

Deux autres fichiers du dépôt sont utiles à connaître : [CHANGELOG.md](CHANGELOG.md) retrace l'historique complet des versions du projet, et [Base.md](Base.md) est un inventaire complet, tenu à jour, de toutes les entités, tous les lieux et tous les artéfacts actuellement dans la base, ainsi que le catalogue des thèmes utilisés par le quiz Liste.

### Nouveautés depuis la dernière release (V3.2.7)

#### V3.4.1

- Ajout de 3 nouvelles entités niveau 3 : Shango (Yoruba), Hinezumi (Japonaise), Huoshu (Chinoise)
- Ajout des 23 dieux/monstres/héros présents dans Smite et/ou Smite 2 encore absents de la base, tous niveau 3 : Méduse et Arachné (Grecque), Bakasura, Kumbhakarna, Rama, Ravana et Vamana (Hindouisme), Chang'e, Da Ji, Guan Yu, Hou Yi, Jing Wei, Hua Mulan, Sun Wukong, Yu Huang et Zhong Kui (Chinoise), Xbalanque (Maya), Sol, Ullr et Ymir (Nordique), Danzaburou et Kuzenbo (Japonaise), Cu Chulainn (Celtique Irlandaise)
  - Entité (Ajout) : Shango, Hinezumi, Huoshu, Méduse, Arachné, Bakasura, Kumbhakarna, Rama, Ravana, Vamana, Chang'e, Da Ji, Guan Yu, Hou Yi, Jing Wei, Hua Mulan, Sun Wukong, Yu Huang, Zhong Kui, Xbalanque, Sol, Ullr, Ymir, Danzaburou, Kuzenbo, Cu Chulainn

#### V3.4.0

- prepopulate.json devient la base complète dans sa forme finale : au lancement, dès que son contenu change (comparaison par hash), la base locale est intégralement vidée puis rechargée à l'identique du JSON, remplaçant la resynchronisation protectrice de V3.2.8
- Champ `userModified` (V3.2.8) retiré ; Room passe en version 8

#### V3.3.0

- Retrait complet de la fonctionnalité d'ajout, de modification et de suppression manuelle d'une entité, d'un lieu ou d'un artéfact depuis l'application (écrans « Ajouter »/« Modifier », 7 fragments et leurs layouts, actions de navigation associées) : un tel changement aurait de toute façon été écrasé au prochain rechargement de la base depuis prepopulate.json

#### V3.2.8

- prepopulate.json n'était lu qu'au premier lancement (base vide) ; les corrections et ajouts de contenu publiés dans les mises à jour n'atteignaient donc jamais les installations déjà lancées une fois. La base se resynchronise désormais avec prepopulate.json à chaque changement détecté de son contenu (comparaison par hash), sans jamais écraser une entrée créée ou éditée par l'utilisateur (nouveau champ `userModified`, Room v7, migration non destructive)
- Limite transitoire assumée : les lignes déjà en base avant cette mise à jour démarrent protégées par défaut, donc les correctifs déjà publiés (V3.2.1 à V3.2.7) ne rattraperont pas automatiquement les installations existantes ; seuls les contenus futurs se resynchroniseront normalement

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
