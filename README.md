# Mythos

## English

**Mythos** is an offline Android reference and quiz app about world mythologies (Greek, Norse, Egyptian, Arthurian, and more).

The built-in database catalogs mythological entities (gods, heroes, monsters…), places and artifacts, sourced entirely from a bundled reference file rather than editable in-app — you can browse and search it freely and open the detailed sheet for any entry.

Three quiz modes let you test your knowledge — Classic (a clue, guess the name), Multiple-choice (same clue, pick the right name among 4), and List (find every entry matching a chosen theme) — available across entities, places and artifacts depending on the mode, each with several difficulty levels or size-based attempt limits. A dedicated help screen (the "?" icon on the quiz selection screen) explains exactly how each one works.

Two other files in this repository are worth knowing about: [CHANGELOG.md](CHANGELOG.md) tracks the full version history of the project, and [Base.md](Base.md) is a complete, always up-to-date inventory of every entity, place and artifact currently in the database, including the theme catalog used by the List quiz.

### Changes since the last release (V4.2.3)

#### V4.3.8

- Search restored in the Entities, Artifacts and Places lists: it lived in the action bar, removed in V4.3.1, and had disappeared with it. It now takes the form of a search field built into the top of each list, above the filter (in the Places list, search takes precedence over the filter, which takes over again when the field is cleared)

#### V4.3.7

- The back button (←), lost with the action bar in V4.3.1, is back: it shows in the top-left corner of every screen except home, in the same style as the Achievements and Help buttons; it sits in the empty corner of three-button menus, shares the line of the search field on lists, and pushes the content below it on the other screens so as not to hide it
- Added the "Valkyrie Apocalypse" pop-culture reference (Shūmatsu no Valkyrie / Record of Ragnarok) on the most important gods of the series, and on Brynhild, its heroine
- Entities added/modified:
  - Zeus : Smite, Smite 2, Le Choc des Titans, God of War III, Odyssée, Iliade, Ulysse 31, Les Héros de l'Olympe, Les Travaux d'Apollon, One Piece -> Smite, Smite 2, Le Choc des Titans, God of War III, Odyssée, Iliade, Ulysse 31, Les Héros de l'Olympe, Les Travaux d'Apollon, One Piece, Valkyrie Apocalypse (popularCulture modified)
  - Poséidon : Smite, Smite 2, Percy Jackson, Assassin's Creed Odyssey, Odyssée, Ulysse 31, Les Héros de l'Olympe, One Piece -> Smite, Smite 2, Percy Jackson, Assassin's Creed Odyssey, Odyssée, Ulysse 31, Les Héros de l'Olympe, One Piece, Valkyrie Apocalypse (popularCulture modified)
  - Hadès : Smite, Smite 2, Hadès, Hercule (Disney), God of War, Odyssée, Les Héros de l'Olympe -> Smite, Smite 2, Hadès, Hercule (Disney), God of War, Odyssée, Les Héros de l'Olympe, Valkyrie Apocalypse (popularCulture modified)
  - Apollon (Grecque) : Smite, Smite 2, God of War III, Percy Jackson, Iliade, Les Héros de l'Olympe, Les Travaux d'Apollon -> Smite, Smite 2, God of War III, Percy Jackson, Iliade, Les Héros de l'Olympe, Les Travaux d'Apollon, Valkyrie Apocalypse (popularCulture modified)
  - Héraclès : Hercule (Disney), Hercules -> Hercule (Disney), Hercules, Valkyrie Apocalypse (popularCulture modified)
  - Thor : Smite, Smite 2, Marvel, God of War, Magnus Chase -> Smite, Smite 2, Marvel, God of War, Magnus Chase, Valkyrie Apocalypse (popularCulture modified)
  - Odin : Smite, Smite 2, God of War, Marvel, Assassin's Creed Valhalla, Magnus Chase -> Smite, Smite 2, God of War, Marvel, Assassin's Creed Valhalla, Magnus Chase, Valkyrie Apocalypse (popularCulture modified)
  - Loki : Smite, Smite 2, Marvel, Loki, Magnus Chase -> Smite, Smite 2, Marvel, Loki, Magnus Chase, Valkyrie Apocalypse (popularCulture modified)
  - Shiva : Smite, Final Fantasy, Asura's Wrath -> Smite, Final Fantasy, Asura's Wrath, Valkyrie Apocalypse (popularCulture modified)
  - Susanoo : Smite, Smite 2, Naruto -> Smite, Smite 2, Naruto, Valkyrie Apocalypse (popularCulture modified)
  - Bouddha : (empty) -> Valkyrie Apocalypse (popularCulture modified)
  - Brynhild : L'Anneau du Nibelung (Wagner) -> L'Anneau du Nibelung (Wagner), Valkyrie Apocalypse (popularCulture modified)

#### V4.3.6

- Collection achievements ("C'est bien, bon toutou", "Très bizarre ton Zoo", "Astrologie de trottoir", "Quand le chat dort..."): obtained entities are now recorded by name + mythology + race instead of name alone, so that a namesake no longer counts for another (e.g. the European Dragon used to validate the Chinese Dragon). Progress already made is kept: previously stored names are converted automatically (with namesakes, all are considered obtained)

#### V4.3.5

- Added the "Quand le chat dort..." achievement: get each of the 12 Chinese zodiac signs at least once in a Classic or Multiple-choice quiz, on the same principle as "Astrologie de trottoir" for the classical zodiac — generic emblem until a final illustration

#### V4.3.4

- Rank of the 12 Chinese zodiac signs corrected in their descriptions and clues to follow the true order of the race held by the Jade Emperor (Rat, Buffle, Tigre, Lapin, Dragon, Serpent, Cheval, Chèvre, Singe, Coq, Chien, Cochon); the years associated with each sign (from 2000) are unchanged
- Entities added/modified:
  - Rat : Neuvième -> Premier signe (description, clue modified)
  - Buffle : Dixième -> Deuxième signe (description, clue modified)
  - Tigre : Onzième -> Troisième signe (description, clue modified)
  - Lapin : Douzième -> Quatrième signe (description, clue modified)
  - Dragon : Premier -> Cinquième signe (description, clue modified)
  - Serpent : Deuxième -> Sixième signe (description, clue modified)
  - Cheval : Troisième -> Septième signe (description, clue modified)
  - Chèvre : Quatrième -> Huitième signe (description, clue modified)
  - Singe : Cinquième -> Neuvième signe (description, clue modified)
  - Coq : Sixième -> Dixième signe (description, clue modified)
  - Chien : Septième -> Onzième signe (description, clue modified)
  - Cochon : Huitième -> Douzième signe (description, clue modified)

#### V4.3.3

- Three-button menu screens (home, Browse, quiz choice, Places quiz, domain choice, Duel mode choice): the column of main buttons is moved down so that the bottom of the first button falls in the middle of the screen, and the screen title/text is now centred in the upper third (two thirds of the way up from the bottom) instead of sticking to the top

#### V4.3.2

- Added an automated test suite (Test-Non-Regression branch): navigation of every screen with main buttons and of their standalone buttons (back included), runs of the Classic/Multiple-choice quizzes (Entities and Artifacts), the List quiz and the Places quiz, run of Duel mode (Classic and Multiple-choice) up to the first question, instrumented tests of the Room database (round-trip of the 5 tables, replacement strategy of `entity_levels`, window of `entity_encounters`, uniqueness of the name+mythology+race key on the real data), and a check that each List theme includes exactly all matching entities/artifacts from `prepopulate.json`, no more, no less
- Added `PlaceDao.getAllSync()` (missing, unlike `EntiteDao`/`ArtifactDao`) to allow this last test

#### V4.3.1

- Added 4 new entities, and fully renumbered the internal ids of every entity so that they exactly match their position in `prepopulate.json` (no entity without an id any more, now a permanent convention); entities whose only change is their id because of this shift are not listed below
- Merged the generic "Celte"/"Celtique" mythologies into two specific categories, "Celtique Irlandaise" and "Celtique Gauloise" (renamed from "Gaulois"); the entities concerned were moved to the right place in the file according to the grouping convention by mythology then by race
- Added 3 new achievements: "Le ciel nous tombe sur la tête" (answer Teutatès correctly), "Ça va Immotep, ça va" (answer Ptah correctly) and "Astrologie de trottoir" (get each of the 12 classical zodiac signs at least once) — generic emblem until a final illustration, as for the other achievements not yet illustrated
- Minor graphical adjustment
- Entities added/modified:
  - Taranis (added)
  - Esus (added)
  - Enlil (added)
  - Nergal (added)
  - Gilgamesh : Sumérienne -> Mésopotamienne (mythology modified)
  - Artio : Celtique -> Celtique Gauloise (mythology modified)
  - Morrigan : Celtique -> Celtique Gauloise (mythology modified)
  - Cliodhna : Celte -> Celtique Irlandaise (mythology modified)
  - Cernunnos : Gaulois -> Celtique Gauloise (mythology modified)
  - Moritasgus : Gaulois -> Celtique Gauloise (mythology modified)
  - Teutatès : Gaulois -> Celtique Gauloise (mythology modified)

#### V4.3.0

- Entities quiz (Classic and Multiple-choice only — Artifacts and Duel unchanged) redesigned with a two-tier adaptive difficulty: on top of the chosen canonical level (Easy/Medium/Hard), each entity gets an internal level (1 to 3) based on the player's own results on it, set for good once it has been met 10 times (mistakes over its last 10 encounters: ≤3 → level 1, ≥8 → level 3, otherwise 2; a mistake counts 1 if never found, 0.5 if found on the second attempt in Classic, 0 otherwise)
- The three levels (Easy/Medium/Hard) now each offer 15 questions drawn only from entities of the chosen canonical level, instead of 10/20/30 questions accumulated over the lower levels; once the internal level is established for every entity of the canonical level, the draw takes 5 questions per internal level (1/2/3, falling back on the neighbouring levels if one of them lacks entities)
- A question's points and the displayed difficulty badge use this internal level rather than the canonical difficulty alone, once it is established
- An entity's internal level and encounter history are kept across `prepopulate.json` updates, unless its name, mythology, race, domain, clue or difficulty changes
- Quiz help screen updated accordingly

#### V4.2.4

- Duel mode choice screen (Classic/Multiple-choice) switched to the image plates `assets/button/classique.png` and `assets/button/qcm.png`, already used by the solo quiz selection screen for the same notion, instead of two plain text buttons

### Getting started

A stable release of the app is published as an `.apk` file in this repository's **Releases** section — download and install that one for a stable experience. The build methods below are only useful to get the very latest version, which may not be 100% stable.

The app isn't published on the Play Store, so building it yourself is done either of two ways.

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

### Nouveautés depuis la dernière release (V4.2.3)

#### V4.3.8

- Rétablissement de la recherche dans les listes Entités, Artéfacts et Lieux : elle était portée par la barre d'action, retirée en V4.3.1, et avait disparu avec elle. Elle prend désormais la forme d'un champ de recherche intégré en haut de chaque liste, au-dessus du filtre (dans la liste des Lieux, la recherche prime sur le filtre, qui reprend la main quand le champ est vidé)

#### V4.3.7

- Retour du bouton retour (←), disparu avec la barre d'action en V4.3.1 : il s'affiche dans le coin supérieur gauche de tous les écrans sauf l'accueil, au même style que les boutons Succès et Aide ; il se place dans le coin vide des menus à trois boutons, partage la ligne du champ de recherche sur les listes, et décale le contenu sous lui sur les autres écrans pour ne pas le masquer
- Ajout de la référence de culture populaire « Valkyrie Apocalypse » (Shūmatsu no Valkyrie / Record of Ragnarok) sur les dieux les plus importants de la série, ainsi que sur Brynhild, son héroïne
- Ajout/Modification d'entités :
  - Zeus : Smite, Smite 2, Le Choc des Titans, God of War III, Odyssée, Iliade, Ulysse 31, Les Héros de l'Olympe, Les Travaux d'Apollon, One Piece -> Smite, Smite 2, Le Choc des Titans, God of War III, Odyssée, Iliade, Ulysse 31, Les Héros de l'Olympe, Les Travaux d'Apollon, One Piece, Valkyrie Apocalypse (popularCulture modifié)
  - Poséidon : Smite, Smite 2, Percy Jackson, Assassin's Creed Odyssey, Odyssée, Ulysse 31, Les Héros de l'Olympe, One Piece -> Smite, Smite 2, Percy Jackson, Assassin's Creed Odyssey, Odyssée, Ulysse 31, Les Héros de l'Olympe, One Piece, Valkyrie Apocalypse (popularCulture modifié)
  - Hadès : Smite, Smite 2, Hadès, Hercule (Disney), God of War, Odyssée, Les Héros de l'Olympe -> Smite, Smite 2, Hadès, Hercule (Disney), God of War, Odyssée, Les Héros de l'Olympe, Valkyrie Apocalypse (popularCulture modifié)
  - Apollon (Grecque) : Smite, Smite 2, God of War III, Percy Jackson, Iliade, Les Héros de l'Olympe, Les Travaux d'Apollon -> Smite, Smite 2, God of War III, Percy Jackson, Iliade, Les Héros de l'Olympe, Les Travaux d'Apollon, Valkyrie Apocalypse (popularCulture modifié)
  - Héraclès : Hercule (Disney), Hercules -> Hercule (Disney), Hercules, Valkyrie Apocalypse (popularCulture modifié)
  - Thor : Smite, Smite 2, Marvel, God of War, Magnus Chase -> Smite, Smite 2, Marvel, God of War, Magnus Chase, Valkyrie Apocalypse (popularCulture modifié)
  - Odin : Smite, Smite 2, God of War, Marvel, Assassin's Creed Valhalla, Magnus Chase -> Smite, Smite 2, God of War, Marvel, Assassin's Creed Valhalla, Magnus Chase, Valkyrie Apocalypse (popularCulture modifié)
  - Loki : Smite, Smite 2, Marvel, Loki, Magnus Chase -> Smite, Smite 2, Marvel, Loki, Magnus Chase, Valkyrie Apocalypse (popularCulture modifié)
  - Shiva : Smite, Final Fantasy, Asura's Wrath -> Smite, Final Fantasy, Asura's Wrath, Valkyrie Apocalypse (popularCulture modifié)
  - Susanoo : Smite, Smite 2, Naruto -> Smite, Smite 2, Naruto, Valkyrie Apocalypse (popularCulture modifié)
  - Bouddha : (vide) -> Valkyrie Apocalypse (popularCulture modifié)
  - Brynhild : L'Anneau du Nibelung (Wagner) -> L'Anneau du Nibelung (Wagner), Valkyrie Apocalypse (popularCulture modifié)

#### V4.3.6

- Succès de collection (« C'est bien, bon toutou », « Très bizarre ton Zoo », « Astrologie de trottoir », « Quand le chat dort... ») : les entités obtenues sont désormais mémorisées par nom + mythologie + race au lieu du seul nom, pour qu'un homonyme ne compte plus à la place d'un autre (ex. le Dragon européen validait le Dragon chinois). La progression déjà acquise est conservée : les anciens noms enregistrés sont convertis automatiquement (en cas d'homonymes, tous sont considérés obtenus)

#### V4.3.5

- Ajout du succès « Quand le chat dort... » : obtenir au moins une fois chacun des 12 signes du zodiaque chinois dans un quiz Classique ou QCM, sur le même principe que « Astrologie de trottoir » pour le zodiaque classique — blason générique en attendant une illustration définitive

#### V4.3.4

- Rang des 12 signes du zodiaque chinois corrigé dans leurs descriptions et indices pour suivre le vrai ordre de la course organisée par l'Empereur de Jade (Rat, Buffle, Tigre, Lapin, Dragon, Serpent, Cheval, Chèvre, Singe, Coq, Chien, Cochon) ; les années associées à chaque signe (à partir de 2000) restent inchangées
- Ajout/Modification d'entités :
  - Rat : Neuvième -> Premier signe (description, indice modifiés)
  - Buffle : Dixième -> Deuxième signe (description, indice modifiés)
  - Tigre : Onzième -> Troisième signe (description, indice modifiés)
  - Lapin : Douzième -> Quatrième signe (description, indice modifiés)
  - Dragon : Premier -> Cinquième signe (description, indice modifiés)
  - Serpent : Deuxième -> Sixième signe (description, indice modifiés)
  - Cheval : Troisième -> Septième signe (description, indice modifiés)
  - Chèvre : Quatrième -> Huitième signe (description, indice modifiés)
  - Singe : Cinquième -> Neuvième signe (description, indice modifiés)
  - Coq : Sixième -> Dixième signe (description, indice modifiés)
  - Chien : Septième -> Onzième signe (description, indice modifiés)
  - Cochon : Huitième -> Douzième signe (description, indice modifiés)

#### V4.3.3

- Écrans de menu à trois boutons (accueil, Parcourir, choix du quiz, quiz Lieux, choix du domaine, choix du mode en Duel) : la colonne des boutons principaux est descendue pour que le bas du premier bouton tombe au milieu de l'écran, et le titre/texte de l'écran est désormais centré au tiers supérieur (aux deux tiers de l'écran en partant du bas) au lieu d'être collé en haut

#### V4.3.2

- Ajout d'une suite de tests automatiques (branche Test-Non-Regression) : navigation de tous les écrans à boutons principaux et de leurs boutons isolés (retour inclus), déroulé des quiz Classique/QCM (Entités et Artéfacts), du quiz Liste et du quiz Lieux, déroulé du mode Duel (Classique et QCM) jusqu'à la première question, tests instrumentés de la base Room (round-trip des 5 tables, stratégie de remplacement de `entity_levels`, fenêtre de `entity_encounters`, unicité de la clé nom+mythologie+race sur les vraies données), et vérification que chaque thème du mode Liste prend en compte toutes les entités/artéfacts correspondants de `prepopulate.json`, ni plus ni moins
- Ajout de `PlaceDao.getAllSync()` (manquant, contrairement à `EntiteDao`/`ArtifactDao`) pour permettre ce dernier test

#### V4.3.1

- Ajout de 4 nouvelles entités, et renumérotation complète des identifiants internes de toutes les entités pour qu'ils correspondent exactement à leur position dans `prepopulate.json` (plus aucune entité sans identifiant, convention désormais permanente) ; les entités dont seul l'identifiant a changé à cause de ce décalage n'apparaissent pas ci-dessous
- Fusion des mythologies génériques « Celte »/« Celtique » dans deux catégories précises, « Celtique Irlandaise » et « Celtique Gauloise » (renommée depuis « Gaulois ») ; les entités concernées ont été replacées au bon endroit du fichier selon la convention de regroupement par mythologie puis par race
- Ajout de 3 nouveaux succès : « Le ciel nous tombe sur la tête » (répondre correctement à Teutatès), « Ça va Immotep, ça va » (répondre correctement à Ptah) et « Astrologie de trottoir » (obtenir au moins une fois chacun des 12 signes du zodiaque classique) — blason générique en attendant une illustration définitive, comme pour les autres succès non encore illustrés
- Changement d'adaptation graphique mineure
- Ajout/Modification d'entités :
  - Taranis (ajout)
  - Esus (ajout)
  - Enlil (ajout)
  - Nergal (ajout)
  - Gilgamesh : Sumérienne -> Mésopotamienne (mythologie modifiée)
  - Artio : Celtique -> Celtique Gauloise (mythologie modifiée)
  - Morrigan : Celtique -> Celtique Gauloise (mythologie modifiée)
  - Cliodhna : Celte -> Celtique Irlandaise (mythologie modifiée)
  - Cernunnos : Gaulois -> Celtique Gauloise (mythologie modifiée)
  - Moritasgus : Gaulois -> Celtique Gauloise (mythologie modifiée)
  - Teutatès : Gaulois -> Celtique Gauloise (mythologie modifiée)

#### V4.3.0

- Quiz Entités (Classique et QCM uniquement — Artéfacts et Duel inchangés) repensé avec une difficulté adaptative à deux niveaux : en plus du niveau canonique choisi (Facile/Moyen/Difficile), chaque entité acquiert un niveau interne (1 à 3) propre aux performances du joueur sur elle, établi définitivement dès qu'elle a été rencontrée 10 fois (fautes sur ses 10 dernières rencontres : ≤3 → niveau 1, ≥8 → niveau 3, sinon 2 ; une faute vaut 1 si jamais trouvée, 0,5 si trouvée au second essai en Classique, 0 sinon)
- Les trois niveaux (Facile/Moyen/Difficile) proposent désormais chacun 15 questions puisées uniquement parmi les entités du niveau canonique choisi, au lieu de 10/20/30 questions cumulées sur les niveaux inférieurs ; une fois le niveau interne établi pour toutes les entités du niveau canonique, le tirage se fait à 5 questions par niveau interne (1/2/3, avec repli sur les niveaux voisins si l'un d'eux manque d'entités)
- Les points d'une question et la pastille de difficulté affichée utilisent ce niveau interne plutôt que la seule difficulté canonique, une fois celui-ci établi
- Le niveau interne et l'historique de rencontres d'une entité sont conservés lors des mises à jour de `prepopulate.json`, sauf si son nom, sa mythologie, sa race, son domaine, son indice ou sa difficulté changent
- Écran d'aide du quiz mis à jour en conséquence

#### V4.2.4

- Écran de choix du mode en Duel (Classique/QCM) passé aux plaques-images `assets/button/classique.png` et `assets/button/qcm.png`, déjà utilisées par l'écran de choix du quiz solo pour la même notion, au lieu de deux boutons texte unis

### Démarrage

Une version stable de l'application est publiée au format `.apk` dans la section **Releases** de ce dépôt — téléchargez-la et installez-la pour une expérience stable. Les méthodes de compilation ci-dessous ne servent qu'à obtenir la toute dernière version, qui peut ne pas être 100 % stable.

L'application n'est pas publiée sur le Play Store ; pour la compiler soi-même, deux façons possibles.

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
