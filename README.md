# Mythos

## English

**Mythos** is an offline Android reference and quiz app about world mythologies (Greek, Norse, Egyptian, Arthurian, and more).

The built-in database catalogs mythological entities (gods, heroes, monsters…), places and artifacts, sourced entirely from a bundled reference file rather than editable in-app — you can browse and search it freely and open the detailed sheet for any entry.

Three quiz modes let you test your knowledge — Classic (a clue, guess the name), Multiple-choice (same clue, pick the right name among 4), and List (find every entry matching a chosen theme) — available across entities, places and artifacts depending on the mode, each with several difficulty levels or size-based attempt limits. A dedicated help screen (the "?" icon on the quiz selection screen) explains exactly how each one works.

Two other files in this repository are worth knowing about: [CHANGELOG.md](CHANGELOG.md) tracks the full version history of the project, and [Base.md](Base.md) is a complete, always up-to-date inventory of every entity, place and artifact currently in the database, including the theme catalog used by the List quiz.

### Changes since the last release (V4.5.3)

#### V4.5.9

- Entities added/modified in the database:
  - Huitzilopochtli (added)
  - Tezcatlipoca (added)
  - Xipe Totec (added)
  - Mictlantecuhtli (added)
  - Coatlicue (added)
  - Ahura Mazda (added)
  - Ahriman (added)
  - Mithra (added)
  - Anahita (added)
  - Perun (added)
  - Veles (added)
  - Svarog (added)
  - Dazhbog (added)
  - Mokosh (added)
  - Koschei (added)
  - Ogun (added)
  - Eshu (added)
  - Oshun (added)
  - Oya (added)
  - Orunmila (added)
  - Anu (added)
  - Enki (added)
  - Shamash (added)
  - Sin (added)
  - Ereshkigal (added)
  - Enkidou (added)
  - Humbaba (added)

#### V4.5.8

- Three new List quiz themes: "Mythologie polynésienne" (Polynesian and Hawaiian mythologies), "Mythologie inuit" and "Mythologie amazighe", each allowing 3 attempts
- Three matching achievements ("Connaisseur de la mythologie polynésienne / inuit / amazighe"); the "Faut sortir la" achievement now requires completing all 30 themes
- Entities added/modified in the database:
  - Sokaris (added)
  - Maui (added)
  - Rangi (added)
  - Papa (added)
  - Tāne (added)
  - Tangaroa (added)
  - Tū (added)
  - Rongo (added)
  - Hina (added)
  - Sedna (added)
  - Anningan (added)
  - Malina (added)
  - Nanook (added)
  - Pinga (added)
  - Sila (added)
  - Tekkeitsertok (added)
  - Tornarsuk (added)
  - Tanit (added)
  - Ammon (added)
  - Anzar (added)
  - Ayyur (added)
  - Gurzil (added)
  - Ifri (added)
  - Mastiman (added)

#### V4.5.7

- "Héros" theme of the List quiz: 10 attempts allowed instead of 5, as the theme now exceeds 50 entries
- Entities added/modified in the Greek, Norse, Roman, Egyptian and European mythologies:
  - Minotaure (added)
  - Chimère (added)
  - Néréides (added)
  - Hespérides (added)
  - Écho (added)
  - Téthys (added)
  - Calypso (added)
  - Ariane (added)
  - Amazones (added)
  - Hippolyte (added)
  - Narcisse (added)
  - Pandore (added)
  - Minos (added)
  - Chrysaor (added)
  - Léto (added)
  - Épiméthée (added)
  - Agrios (added)
  - Pontos (added)
  - Phorcys (added)
  - Kéto (added)
  - Achéloos (added)
  - Ganymède (added)
  - Psyché (added)
  - Pasiphaé (added)
  - Procuste (added)
  - Ophiotaurus (added)
  - Talos (added)
  - Gégénéis (added)
  - Eurynomos (added)
  - Arai (added)
  - Makhai (added)
  - Triptolème (added)
  - Phinée (added)
  - Galanthis (added)
  - Hécube (added)
  - Lupa (added)
  - Lares (added)
  - Ran (added)
  - Angrboda (added)
  - Vidar (added)
  - Váli (added)
  - Magni (added)
  - Módi (added)
  - Andvari (added)
  - Brokkr (added)
  - Eitri (added)
  - Sigyn (added)
  - Höd (added)
  - Hœnir (added)
  - Líf (added)
  - Lífthrasir (added)
  - Tefnout (added)
  - Khnoum (added)
  - Ammit (added)
  - Nekhbet (added)
  - Sérapis (added)
  - Catoblépas (added)

#### V4.5.6

- Muse type "Grecque" renamed "Classique" (the nine Muses, daughters of Mnemosyne, as opposed to the three Boeotian Muses): the 9 Camenae, which belong to this type, are no longer presented as Greek muses in Roman mythology
- Coelus's domain reworded without mentioning Ouranos, while staying distinct from Uranus's
- Entities added/modified:
  - Calliope : Grecque -> Classique (museType modified)
  - Clio : Grecque -> Classique (museType modified)
  - Érato : Grecque -> Classique (museType modified)
  - Euterpe : Grecque -> Classique (museType modified)
  - Melpomène : Grecque -> Classique (museType modified)
  - Polymnie : Grecque -> Classique (museType modified)
  - Terpsichore : Grecque -> Classique (museType modified)
  - Thalie : Grecque -> Classique (museType modified)
  - Uranie : Grecque -> Classique (museType modified)
  - 9 Camènes : Grecque -> Classique (museType modified)
  - Coelus : Dieu du ciel romain, équivalent d'Ouranos -> Personnification latine du ciel (domain modified)
  - Coelus : Dieu du ciel romain, équivalent d'Ouranos -> Personnification latine du ciel (clue modified)

#### V4.5.5

- Parentage of every entity checked: each parent now belongs to the right mythology (Roman entities no longer have a Greek parent) and matches the reference genealogies; Nyx, a goddess, moves from the "Father" field to the "Mother" field of her children
- Entities added/modified:
  - Apollon (Romaine) : Zeus -> Jupiter (fatherName modified)
  - Hercule : Zeus -> Jupiter (ascendantName modified)
  - Castor : Zeus -> Tyndare (fatherName modified)
  - Médée : Hélios -> Æétès (fatherName modified)
  - Encelade : Tartare -> Ouranos (fatherName modified)
  - Éris : Nyx -> (empty) (fatherName modified)
  - Éris : (empty) -> Nyx (motherName modified)
  - Hypnos : Nyx -> (empty) (fatherName modified)
  - Hypnos : (empty) -> Nyx (motherName modified)
  - Thanatos : Nyx -> (empty) (fatherName modified)
  - Thanatos : (empty) -> Nyx (motherName modified)
  - Némésis : Nyx -> (empty) (fatherName modified)
  - Némésis : (empty) -> Nyx (motherName modified)

#### V4.5.4

- Achievements screen: the "Succès" title is now shown on the same line as the back button, to its right
- Duel screen (number of players): the "?" help button is now aligned on the same line as the back button, as on the quiz selection screen

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

### Nouveautés depuis la dernière release (V4.5.3)

#### V4.5.9

- Ajout/Modification d'entités en base :
  - Huitzilopochtli (ajout)
  - Tezcatlipoca (ajout)
  - Xipe Totec (ajout)
  - Mictlantecuhtli (ajout)
  - Coatlicue (ajout)
  - Ahura Mazda (ajout)
  - Ahriman (ajout)
  - Mithra (ajout)
  - Anahita (ajout)
  - Perun (ajout)
  - Veles (ajout)
  - Svarog (ajout)
  - Dazhbog (ajout)
  - Mokosh (ajout)
  - Koschei (ajout)
  - Ogun (ajout)
  - Eshu (ajout)
  - Oshun (ajout)
  - Oya (ajout)
  - Orunmila (ajout)
  - Anu (ajout)
  - Enki (ajout)
  - Shamash (ajout)
  - Sin (ajout)
  - Ereshkigal (ajout)
  - Enkidou (ajout)
  - Humbaba (ajout)

#### V4.5.8

- Trois nouveaux thèmes dans le mode Liste : « Mythologie polynésienne » (mythologies polynésienne et hawaïenne), « Mythologie inuit » et « Mythologie amazighe », 3 essais autorisés chacun
- Trois nouveaux succès associés (« Connaisseur de la mythologie polynésienne / inuit / amazighe ») ; le succès « Faut sortir la » demande désormais de compléter les 30 thèmes
- Ajout/Modification d'entités en base :
  - Sokaris (ajout)
  - Maui (ajout)
  - Rangi (ajout)
  - Papa (ajout)
  - Tāne (ajout)
  - Tangaroa (ajout)
  - Tū (ajout)
  - Rongo (ajout)
  - Hina (ajout)
  - Sedna (ajout)
  - Anningan (ajout)
  - Malina (ajout)
  - Nanook (ajout)
  - Pinga (ajout)
  - Sila (ajout)
  - Tekkeitsertok (ajout)
  - Tornarsuk (ajout)
  - Tanit (ajout)
  - Ammon (ajout)
  - Anzar (ajout)
  - Ayyur (ajout)
  - Gurzil (ajout)
  - Ifri (ajout)
  - Mastiman (ajout)

#### V4.5.7

- Thème « Héros » du mode Liste : 10 essais autorisés au lieu de 5, le thème dépassant désormais 50 entrées
- Ajout/Modification d'entités dans les mythologies grecque, nordique, romaine, égyptienne et européenne :
  - Minotaure (ajout)
  - Chimère (ajout)
  - Néréides (ajout)
  - Hespérides (ajout)
  - Écho (ajout)
  - Téthys (ajout)
  - Calypso (ajout)
  - Ariane (ajout)
  - Amazones (ajout)
  - Hippolyte (ajout)
  - Narcisse (ajout)
  - Pandore (ajout)
  - Minos (ajout)
  - Chrysaor (ajout)
  - Léto (ajout)
  - Épiméthée (ajout)
  - Agrios (ajout)
  - Pontos (ajout)
  - Phorcys (ajout)
  - Kéto (ajout)
  - Achéloos (ajout)
  - Ganymède (ajout)
  - Psyché (ajout)
  - Pasiphaé (ajout)
  - Procuste (ajout)
  - Ophiotaurus (ajout)
  - Talos (ajout)
  - Gégénéis (ajout)
  - Eurynomos (ajout)
  - Arai (ajout)
  - Makhai (ajout)
  - Triptolème (ajout)
  - Phinée (ajout)
  - Galanthis (ajout)
  - Hécube (ajout)
  - Lupa (ajout)
  - Lares (ajout)
  - Ran (ajout)
  - Angrboda (ajout)
  - Vidar (ajout)
  - Váli (ajout)
  - Magni (ajout)
  - Módi (ajout)
  - Andvari (ajout)
  - Brokkr (ajout)
  - Eitri (ajout)
  - Sigyn (ajout)
  - Höd (ajout)
  - Hœnir (ajout)
  - Líf (ajout)
  - Lífthrasir (ajout)
  - Tefnout (ajout)
  - Khnoum (ajout)
  - Ammit (ajout)
  - Nekhbet (ajout)
  - Sérapis (ajout)
  - Catoblépas (ajout)

#### V4.5.6

- Type de muse « Grecque » renommé « Classique » (les neuf Muses filles de Mnémosyne, par opposition aux trois Muses béotiennes) : les 9 Camènes, rattachées à ce type, ne sont plus présentées comme des muses grecques dans la mythologie romaine
- Domaine de Coelus reformulé sans mention d'Ouranos, tout en restant distinct de celui d'Uranus
- Ajout/Modification d'entités :
  - Calliope : Grecque -> Classique (museType modifié)
  - Clio : Grecque -> Classique (museType modifié)
  - Érato : Grecque -> Classique (museType modifié)
  - Euterpe : Grecque -> Classique (museType modifié)
  - Melpomène : Grecque -> Classique (museType modifié)
  - Polymnie : Grecque -> Classique (museType modifié)
  - Terpsichore : Grecque -> Classique (museType modifié)
  - Thalie : Grecque -> Classique (museType modifié)
  - Uranie : Grecque -> Classique (museType modifié)
  - 9 Camènes : Grecque -> Classique (museType modifié)
  - Coelus : Dieu du ciel romain, équivalent d'Ouranos -> Personnification latine du ciel (domain modifié)
  - Coelus : Dieu du ciel romain, équivalent d'Ouranos -> Personnification latine du ciel (clue modifié)

#### V4.5.5

- Vérification des parentés de toutes les entités : chaque parent correspond désormais à la bonne mythologie (les entités romaines n'ont plus de parent grec) et aux généalogies de référence ; Nyx, déesse, passe du champ « Père » au champ « Mère » de ses enfants
- Ajout/Modification d'entités :
  - Apollon (Romaine) : Zeus -> Jupiter (fatherName modifié)
  - Hercule : Zeus -> Jupiter (ascendantName modifié)
  - Castor : Zeus -> Tyndare (fatherName modifié)
  - Médée : Hélios -> Æétès (fatherName modifié)
  - Encelade : Tartare -> Ouranos (fatherName modifié)
  - Éris : Nyx -> (vide) (fatherName modifié)
  - Éris : (vide) -> Nyx (motherName modifié)
  - Hypnos : Nyx -> (vide) (fatherName modifié)
  - Hypnos : (vide) -> Nyx (motherName modifié)
  - Thanatos : Nyx -> (vide) (fatherName modifié)
  - Thanatos : (vide) -> Nyx (motherName modifié)
  - Némésis : Nyx -> (vide) (fatherName modifié)
  - Némésis : (vide) -> Nyx (motherName modifié)

#### V4.5.4

- Écran des succès : le titre « Succès » est désormais affiché sur la même ligne que le bouton retour, à sa droite
- Écran du Duel (choix du nombre de joueurs) : le bouton d'aide « ? » est désormais aligné sur la même ligne que le bouton retour, comme sur l'écran de choix du quiz

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
