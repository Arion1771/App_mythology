# Mythos

## English

**Mythos** is an offline Android reference and quiz app about world mythologies (Greek, Norse, Egyptian, Arthurian, and more). Browse, search, add and edit mythological entities, places and artifacts, then test your knowledge with three-difficulty quizzes.

### Changes since the first version

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

1. Clone the repository.
2. Open the project in Android Studio.
3. Sync Gradle, then run the app on an emulator or device.

---

## Français

**Mythos** est une application Android de référence et de quiz hors-ligne sur les mythologies du monde (grecque, nordique, égyptienne, arthurienne, et bien d'autres). Parcourez, recherchez, ajoutez et modifiez des entités mythologiques, des lieux et des artéfacts, puis testez vos connaissances via des quiz à trois niveaux de difficulté.

### Nouveautés depuis la première version

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

1. Cloner le dépôt.
2. Ouvrir le projet dans Android Studio.
3. Synchroniser Gradle, puis lancer l'application sur un émulateur ou un appareil.