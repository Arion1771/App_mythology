# Mythos

## English

**Mythos** is an offline Android reference and quiz app about world mythologies (Greek, Norse, Egyptian, Arthurian, and more).

The built-in database catalogs mythological entities (gods, heroes, monsters…), places and artifacts. You can browse and search it freely, open the detailed sheet for any entry, and add or edit entries directly from the app.

Three independent quizzes let you test your knowledge: an entity quiz, a place quiz and an artifact quiz, each with several difficulty levels. A dedicated help screen (the "?" icon on the quiz selection screen) explains exactly how each one works.

### Changes since the first version

#### V2.5.1

- Fixed the "0 / 0 found" counter of the Places quiz: the total was only updated on the first find instead of showing as soon as the grid loaded

#### V2.5.0

- Quiz help screen over several swipeable pages (`QuizHelpFragment`, ViewPager2), reachable through a "?" icon on the quiz selection screen: how the three quizzes work, difficulty and scoring, answer conventions (case, accents, no article)
- App locked in portrait orientation (`android:screenOrientation="portrait"`)

#### V2.4.2

- Pop culture: updated 108 entities according to the exact rosters of Smite and Smite 2 ("Smite", "Smite 2" or both depending on presence in each game); Smite's Norse "Sol" was deliberately not matched with the Roman entity of the same name (separate pantheons)
- Entity:
  - Agamemnon (Added)
  - Ajax le Petit (Added)
  - Ajax le Grand, ex-Ajax (Updated)
  - Diomède (Added)
  - Ménélas (Added)
  - Nestor (Added)
  - Patrocle (Added)
  - Bake Kujira (Added)
  - Moritasgus (Added)

#### V2.4.1

- Fixed the `navGraphViewModels` import (declared in `androidx.navigation`, not `androidx.navigation.fragment`), which prevented the quiz and result screens introduced in V2.4.0 from compiling

#### V2.4.0

- Dedicated result screen for the entity and artifact quizzes (`QuizEntityResultFragment` / `QuizArtifactResultFragment`), shown only once the current question can no longer be answered (found, or two wrong attempts): name coloured (green/yellow/red) according to the result, all the additional information (pop culture included), and a button to go to the next question or see the score
- The rest of the quiz flow (step 1 clue only, step 2 information revealed inline after a first wrong answer) is unchanged

#### V2.3.3

- Overhaul of the `popularCulture` field: it now only lists the title(s) of the work (video game, film, series, anime/manga), comma-separated, with no descriptive sentence
- New review pass over all entities: 109 more entities filled in (194 in total), with verified references (Smite, God of War, Percy Jackson, Kaamelott, Hades/Hades II, Assassin's Creed, Marvel, Naruto, Harry Potter, Dungeons & Dragons…)

#### V2.3.2

- Archangels: aligned with the Book of Enoch (1 Enoch 20), removed Chamuel, Haniel, Jophiel, Métatron, Sandalphon and Zadkiel (a separate esoteric tradition) and added Sariel, keeping only the 7 original archangels
- Name fixes: Freyja (formerly Freya), Valkyrie (formerly Valkyries), Nephtys (formerly Nephthys)
- Rebalanced the difficulty (1 → 2) of 8 entities: 3 Moires, 9 Muses, Aigle du Caucase, Érèbe, 3 Parques, 9 Camènes, Polyphemus, Tyr
- Entity:
  - Sariel (Added)
  - Freyja, ex-Freya (Updated)
  - Valkyrie, ex-Valkyries (Updated)
  - Nephtys, ex-Nephthys (Updated)

#### V2.3.1

- Added `Base.md`, a complete inventory of the entities, places and artifacts in the database (bullet lists by mythology then by race/artifact type/quiz grouping), to be kept up to date with every database change

#### V2.3.0

- Added the `popularCulture` field on entities: notable appearances in video games, films, series and anime/manga, filled in for 85 notable entities (schema, Room version 5, add/edit forms and detail sheet); deliberately left out of the quiz recap so as not to give the answer away

#### V2.2.0

- Coloured difficulty badge in the quiz (entities and artifacts): green/yellow/red for easy/medium/hard, instead of a neutral background

#### V2.1.0

- Removed duplicate information at step 2 of the quiz (entities and artifacts): race/type, mythology and clue, already shown at all times since the start of the question, are no longer repeated in the additional information block

#### V2.0.7

- Enriched the 353 remaining entity descriptions, completing the work started in V2.0.6: every entity now has a description distinct from the quiz clue

#### V2.0.6

- Full spelling and grammar review of entities, places and artifacts (accents, agreement, typography) and removal of leading articles from names, propagated to the fields that reference them
- Enriched the 54 entity descriptions that were until then identical to the quiz clue
- Adjusted Olorun's difficulty (1 → 2)
- Entity:
  - Iapetus, ex-Lapetus (Updated)
  - Poissons, ex-Poisson (Updated)
  - Dvalin, ex-Dwalin (Updated)
  - Cottos, ex-Cotos (Updated)
  - Goibniu, ex-Goibnu (Updated)
  - Érèbe, ex-Erèbe (Updated)
  - Érinyes, ex-Erinyes (Updated)
  - Éros, ex-Eros (Updated)
  - Éphialtès, ex-Ephialtès (Updated)
  - Ériu, ex-Eriu (Updated)
  - Étain, ex-Etain (Updated)
  - Sanglier d'Érymanthe, ex-Sanglier d'Erymanthe (Updated)
  - Astréos, ex-Astreos (Updated)
  - Coéos, ex-Coeos (Updated)
  - Eurymédon, ex-Eurymedon (Updated)
  - Océanos, ex-Oceanos (Updated)
  - Lélaps, ex-Lelaps (Updated)
  - 9 Muses, ex-9 muses (Updated)
  - Susanoo, ex-Susano (Updated)
  - Dame du Lac, ex-La dame du Lac (Updated)
  - Qilin, ex-Kirin (Updated)
  - Ogma, ex-Ogmios (Updated)
  - Pele, ex-Pélé (Updated)
  - Bouddha, ex-Buddha (Updated)
  - Rê, ex-Ré (Updated)
  - Mélinoé (Added)
- Place:
  - Vanaheim, ex-Vanneheim (Updated)
  - Jotunheim, ex-Jotunnheim (Updated)
- Artifact:
  - Mjölnir, ex-Mjolnir (Updated)

#### V2.0.5

- Normalised non-Latin letters (attached oe ligature, Norse thorn/eth) into Latin equivalents

#### V2.0.4

- Added 23 new artifacts
- Artifact:
  - Ambroisie (Added)
  - Nectar (Added)
  - Pommes d'Idunn (Added)
  - Pêches d'immortalité (Added)
  - Amrita (Added)
  - Óðrœrir (Added)
  - Char d'Hélios (Added)
  - Skidbladnir (Added)
  - Barque solaire de Rê (Added)
  - Pushpaka Vimana (Added)
  - Draupnir (Added)
  - Caducée (Added)
  - Œil d'Horus (Added)
  - Lance de Lugh (Added)
  - Claíomh Solais (Added)
  - Lia Fáil (Added)
  - Chaudron du Dagda (Added)
  - Tablette des Destinées (Added)
  - Ruyi Jingu Bang (Added)
  - Totsuka-no-Tsurugi (Added)
  - Fourreau d'Excalibur (Added)
  - Boîte de Pandore (Added)
  - Pommes d'or des Hespérides (Added)

#### V2.0.3

- Extended the artifact types (Vehicle, Food)

#### V2.0.2

- Real names for the artifacts and 8 new ones added:
  - Andvaranaut (Updated)
  - Keraunos (Added)
  - Kunée (Added)
  - Talaria (Added)
  - Harpé (Added)
  - Yata no Kagami (Added)
  - Yasakani no Magatama (Added)
  - Gleipnir (Added)
  - Muramasa (Added)

#### V2.0.1

- Added the initial artifacts to the database:
  - Excalibur (Added)
  - Mjolnir (Added)
  - Anneau d'Andvari (Added)
  - Gungnir (Added)
  - Égide (Added)
  - Trident de Poséidon (Added)
  - Toison d'or (Added)
  - Brísingamen (Added)
  - Gáe Bulg (Added)
  - Kusanagi-no-Tsurugi (Added)
  - Sudarshana Chakra (Added)
  - Saint Graal (Added)

#### V2.0.0

- Added the artifact system (weapons and magic items): database, browsing, add/edit and a dedicated quiz

#### V1.7.1

- Rebalanced the difficulty levels of several entities

#### V1.7.0

- Global button style to avoid truncated text on small screens

#### V1.6.5

- The quiz (entities and places) is kept when the screen rotates

#### V1.6.4

- Fixed the 'Primodrial' → 'Primordial' typo in the database

#### V1.6.3

- The old 'Primodrial' typo is tolerated in the divine typing

#### V1.6.2

- Finished displaying the Érinyes, Grées and Valkyries races in the existing screens (translations, form groups)

#### V1.6.1

- Added new entities (Érinyes, Grées, Valkyries) to the database and sorted the ids by mythology

#### V1.6.0

- Overhaul of the entity quiz scoring (weighted by difficulty) and of how answers are displayed

#### V1.5.1

- MainActivity adapted to the native ActionBar required by the theme

#### V1.5.0

- Added difficulty selection to the entity quiz and renamed the quizz→quiz package

#### V1.4.0

- Added creating/editing places and browsing them

#### V1.3.0

- Added entity editing and styled the browse/add screens

#### V1.2.0

- Initial population of the mythology database (assets/prepopulate.json, populateIfEmpty)

#### V1.1.1

- Gradle configuration adjustments

#### V1.1.0

- Set up the dark visual theme and the existing navigation screens (Home, Browse/Add/Quiz choice)

#### V1.0.1

- Added the Gradle wrapper and missing string resources

#### V1.0.0

- First working version of the app: Room persistence, navigation, Home/Browse/Add/Quiz screens

#### V0.1.2

- Added the GiantType, MuseType, ZodiacType, Realm and River classes

#### V0.1.1

- Reorganised the model classes into packages (base/entity/enum/sign)

#### V0.1.0

- Added the data model for mythological entities (gods, titans, giants, heroes, monsters, muses, archangels, Arthurian knights, demons, cyclopes, hecatoncheires, zodiac signs)

#### V0.0.0

- Repository initialisation (`.gitignore`, `README.md`)

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

Trois quiz indépendants permettent de tester vos connaissances : un quiz d'entités, un quiz de lieux et un quiz d'artéfacts, chacun avec plusieurs niveaux de difficulté. Un écran d'aide dédié (icône « ? » sur l'écran de choix du quiz) explique en détail le fonctionnement de chacun.

### Nouveautés depuis la première version

#### V2.5.1

- Correction du compteur « 0 / 0 trouvés » du quiz Lieux : le total n'était mis à jour qu'à la première trouvaille au lieu de s'afficher dès le chargement de la grille

#### V2.5.0

- Écran d'aide du quiz sur plusieurs pages défilantes (`QuizHelpFragment`, ViewPager2), accessible via une icône « ? » sur l'écran de choix du quiz : fonctionnement des trois quiz, difficulté et calcul des points, conventions de réponse (casse, accents, absence d'article)
- Application verrouillée en orientation portrait (`android:screenOrientation="portrait"`)

#### V2.4.2

- Culture populaire : mise à jour de 108 entités selon les rosters exacts de Smite et Smite 2 (« Smite », « Smite 2 » ou les deux selon présence dans chaque jeu) ; le « Sol » nordique de Smite n'a volontairement pas été rapproché de l'entité romaine homonyme (panthéons distincts)
- Entité :
  - Agamemnon (Ajout)
  - Ajax le Petit (Ajout)
  - Ajax le Grand, ex-Ajax (Mise à jour)
  - Diomède (Ajout)
  - Ménélas (Ajout)
  - Nestor (Ajout)
  - Patrocle (Ajout)
  - Bake Kujira (Ajout)
  - Moritasgus (Ajout)

#### V2.4.1

- Correction de l'import `navGraphViewModels` (déclaré dans `androidx.navigation`, pas `androidx.navigation.fragment`) qui empêchait la compilation des écrans de quiz et de résultat introduits en V2.4.0

#### V2.4.0

- Écran de résultat dédié pour les quiz d'entités et d'artéfacts (`QuizEntityResultFragment` / `QuizArtifactResultFragment`), affiché uniquement lorsqu'on ne peut plus répondre à la question en cours (trouvé, ou deux essais faux) : nom coloré (vert/jaune/rouge) selon le résultat, toutes les informations complémentaires (dont la culture populaire), et bouton pour passer à la question suivante ou voir le score
- Le reste du déroulé du quiz (étape 1 indice seul, étape 2 infos révélées inline après une première réponse fausse) reste inchangé

#### V2.3.3

- Refonte du champ `popularCulture` : ne cite plus que le(s) titre(s) de l'œuvre (jeu vidéo, film, série, animé/manga), séparés par virgule, sans phrase descriptive
- Nouvelle passe de vérification sur l'ensemble des entités : 109 entités supplémentaires renseignées (194 au total), avec des références vérifiées (Smite, God of War, Percy Jackson, Kaamelott, Hadès/Hadès II, Assassin's Creed, Marvel, Naruto, Harry Potter, Donjons & Dragons…)

#### V2.3.2

- Archanges : conformité au Livre d'Hénoch (1 Hénoch 20), suppression de Chamuel, Haniel, Jophiel, Métatron, Sandalphon et Zadkiel (tradition ésotérique distincte) et ajout de Sariel, pour ne conserver que les 7 archanges originels
- Corrections de noms : Freyja (ex-Freya), Valkyrie (ex-Valkyries), Nephtys (ex-Nephthys)
- Rééquilibrage de la difficulté (1 → 2) de 8 entités : 3 Moires, 9 Muses, Aigle du Caucase, Érèbe, 3 Parques, 9 Camènes, Polyphemus, Tyr
- Entité :
  - Sariel (Ajout)
  - Freyja, ex-Freya (Mise à jour)
  - Valkyrie, ex-Valkyries (Mise à jour)
  - Nephtys, ex-Nephthys (Mise à jour)

#### V2.3.1

- Ajout de `Base.md`, inventaire complet des entités, lieux et artéfacts de la base (listes à puces par mythologie puis par race/type d'artéfact/regroupement de quiz), à tenir à jour à chaque changement touchant la base

#### V2.3.0

- Ajout du champ `popularCulture` sur les entités : apparitions notables dans les jeux vidéo, films, séries et animés/mangas, renseigné pour 85 entités notables (schéma, version Room 5, formulaires d'ajout/édition et fiche détaillée) ; volontairement absent du récapitulatif du quiz pour ne pas révéler la réponse

#### V2.2.0

- Pastille de difficulté colorée dans le quiz (entités et artéfacts) : vert/jaune/rouge selon le niveau facile/moyen/difficile, au lieu d'un fond neutre

#### V2.1.0

- Suppression des doublons d'information à l'étape 2 du quiz (entités et artéfacts) : race/type, mythologie et indice, déjà affichés en permanence depuis le début de la question, ne sont plus répétés dans le bloc d'informations complémentaires

#### V2.0.7

- Enrichissement des 353 descriptions d'entités restantes, complétant le travail débuté en V2.0.6 : toutes les entités disposent désormais d'une description distincte de l'indice du quiz

#### V2.0.6

- Audit orthographique et grammatical complet des entités, lieux et artéfacts (accents, accords, typographie) et suppression des articles en début de nom, avec propagation aux champs qui les référencent
- Enrichissement des 54 descriptions d'entités jusque-là identiques à l'indice du quiz
- Ajustement de la difficulté d'Olorun (1 → 2)
- Entité :
  - Iapetus, ex-Lapetus (Mise à jour)
  - Poissons, ex-Poisson (Mise à jour)
  - Dvalin, ex-Dwalin (Mise à jour)
  - Cottos, ex-Cotos (Mise à jour)
  - Goibniu, ex-Goibnu (Mise à jour)
  - Érèbe, ex-Erèbe (Mise à jour)
  - Érinyes, ex-Erinyes (Mise à jour)
  - Éros, ex-Eros (Mise à jour)
  - Éphialtès, ex-Ephialtès (Mise à jour)
  - Ériu, ex-Eriu (Mise à jour)
  - Étain, ex-Etain (Mise à jour)
  - Sanglier d'Érymanthe, ex-Sanglier d'Erymanthe (Mise à jour)
  - Astréos, ex-Astreos (Mise à jour)
  - Coéos, ex-Coeos (Mise à jour)
  - Eurymédon, ex-Eurymedon (Mise à jour)
  - Océanos, ex-Oceanos (Mise à jour)
  - Lélaps, ex-Lelaps (Mise à jour)
  - 9 Muses, ex-9 muses (Mise à jour)
  - Susanoo, ex-Susano (Mise à jour)
  - Dame du Lac, ex-La dame du Lac (Mise à jour)
  - Qilin, ex-Kirin (Mise à jour)
  - Ogma, ex-Ogmios (Mise à jour)
  - Pele, ex-Pélé (Mise à jour)
  - Bouddha, ex-Buddha (Mise à jour)
  - Rê, ex-Ré (Mise à jour)
  - Mélinoé (Ajout)
- Lieu :
  - Vanaheim, ex-Vanneheim (Mise à jour)
  - Jotunheim, ex-Jotunnheim (Mise à jour)
- Artefact :
  - Mjölnir, ex-Mjolnir (Mise à jour)

#### V2.0.5

- Normalisation des lettres non-latines (ligature oe attachée, thorn/eth norrois) en équivalents latins

#### V2.0.4

- Ajout de 23 nouveaux artéfacts
- Artefact :
  - Ambroisie (Ajout)
  - Nectar (Ajout)
  - Pommes d'Idunn (Ajout)
  - Pêches d'immortalité (Ajout)
  - Amrita (Ajout)
  - Óðrœrir (Ajout)
  - Char d'Hélios (Ajout)
  - Skidbladnir (Ajout)
  - Barque solaire de Rê (Ajout)
  - Pushpaka Vimana (Ajout)
  - Draupnir (Ajout)
  - Caducée (Ajout)
  - Œil d'Horus (Ajout)
  - Lance de Lugh (Ajout)
  - Claíomh Solais (Ajout)
  - Lia Fáil (Ajout)
  - Chaudron du Dagda (Ajout)
  - Tablette des Destinées (Ajout)
  - Ruyi Jingu Bang (Ajout)
  - Totsuka-no-Tsurugi (Ajout)
  - Fourreau d'Excalibur (Ajout)
  - Boîte de Pandore (Ajout)
  - Pommes d'or des Hespérides (Ajout)

#### V2.0.3

- Extension des types d'artéfacts (Véhicule, Nourriture)

#### V2.0.2

- Vrais noms des artéfacts et ajout de 8 nouveaux :
  - Andvaranaut (Mise à jour)
  - Keraunos (Ajout)
  - Kunée (Ajout)
  - Talaria (Ajout)
  - Harpé (Ajout)
  - Yata no Kagami (Ajout)
  - Yasakani no Magatama (Ajout)
  - Gleipnir (Ajout)
  - Muramasa (Ajout)

#### V2.0.1

- Ajout des artéfacts initiaux en base :
  - Excalibur (Ajout)
  - Mjolnir (Ajout)
  - Anneau d'Andvari (Ajout)
  - Gungnir (Ajout)
  - Égide (Ajout)
  - Trident de Poséidon (Ajout)
  - Toison d'or (Ajout)
  - Brísingamen (Ajout)
  - Gáe Bulg (Ajout)
  - Kusanagi-no-Tsurugi (Ajout)
  - Sudarshana Chakra (Ajout)
  - Saint Graal (Ajout)

#### V2.0.0

- Ajout du système d'artéfacts (armes et objets magiques) : base de données, parcours, ajout/modification et quiz dédié

#### V1.7.1

- Rééquilibrage des niveaux de difficulté de plusieurs entités

#### V1.7.0

- Style de bouton global pour éviter le texte tronqué sur petits écrans

#### V1.6.5

- Conservation du quiz (entités et lieux) lors d'une rotation d'écran

#### V1.6.4

- Correction de la coquille 'Primodrial' → 'Primordial' en base de données

#### V1.6.3

- Tolérance de l'ancienne coquille 'Primodrial' dans le typage divin

#### V1.6.2

- Finition de l'affichage des races Érinyes, Grées et Valkyries dans les écrans existants (traductions, groupes de formulaire)

#### V1.6.1

- Ajout de nouvelles entités (Érinyes, Grées, Valkyries) en base et tri des identifiants par mythologie

#### V1.6.0

- Refonte du système de score du quiz d'entités (pondéré par la difficulté) et de l'affichage des réponses

#### V1.5.1

- Adaptation de MainActivity à l'ActionBar native requise par le thème

#### V1.5.0

- Ajout du choix de difficulté du quiz d'entités et renommage du package quizz→quiz

#### V1.4.0

- Ajout de l'ajout/édition des lieux et de leur parcours

#### V1.3.0

- Ajout de l'édition des entités et mise en forme des écrans de parcours/ajout

#### V1.2.0

- Peuplement initial de la base de données mythologique (assets/prepopulate.json, populateIfEmpty)

#### V1.1.1

- Ajustements de configuration Gradle

#### V1.1.0

- Mise en place du thème visuel sombre et des écrans de navigation existants (Accueil, choix Parcourir/Ajouter/Quiz)

#### V1.0.1

- Ajout du wrapper Gradle et de ressources de chaînes manquantes

#### V1.0.0

- Version fonctionnelle initiale de l'application : persistance Room, navigation, écrans Accueil/Parcourir/Ajouter/Quiz

#### V0.1.2

- Ajout des classes GiantType, MuseType, ZodiacType, Realm et River

#### V0.1.1

- Réorganisation des classes du modèle en packages (base/entity/enum/sign)

#### V0.1.0

- Ajout du modèle de données des entités mythologiques (dieux, titans, géants, héros, monstres, muses, archanges, chevaliers arthuriens, démons, cyclopes, hécatonchires, signes du zodiaque)

#### V0.0.0

- Initialisation du dépôt (`.gitignore`, `README.md`)

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
