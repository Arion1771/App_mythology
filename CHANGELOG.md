# Changelog

## V3

### V3.0.0

- Refonte complète des quiz : deux nouveaux modes s'ajoutent au mode Classique
- Nouveau mode QCM (entités et artéfacts) : même indice que le mode Classique, mais réponse en un seul essai parmi 4 noms proposés ; les 3 leurres sont choisis par proximité thématique (tags, mythologie, race, équivalent) plutôt qu'au hasard ; passage immédiat à la question suivante quel que soit le résultat, écran de score final sans seconde chance
- Nouveau mode Liste : 27 thèmes sélectionnables (mythologies, familles de créatures, groupes légendaires...), grille de cartes façon quiz Lieux, clic sur une carte non trouvée → écran plein affichant toutes les informations sauf le nom, essais limités selon la taille du thème (3/5/10), révélation finale et score sur le total du thème ; certains thèmes affichent leurs cartes réparties sous plusieurs sous-titres (Muses, Enfants de Gaïa et Ouranos, Archanges et démons, Zodiaque)
- Nouveaux champs `tags` (entités et artéfacts) et `listThemes` (entités uniquement) au service de ces deux modes : schéma Room v6, migration destructive, formulaires d'ajout/édition
- Tags thématiques assignés aux 486 entités et 43 artéfacts (vent, ciel, amour, animal, principal, humain, terre, feu, eau, guerre, mort, sagesse, magie, ruse, chasse, artisanat, fertilité, soleil, jour, nuit, foudre, gardien, prophétie, arts, justice, royauté, guérison, messager, destin)
- Rattachement de 61 entités aux 7 thèmes curés du mode Liste non déductibles d'un champ existant (Guerriers grecs devant Troie, Argonautes, Chevaliers de la table ronde, Grands dieux d'Égypte, Monstres de l'arbre monde, Monstres des 12 travaux, Yokais)
- Écran d'aide du quiz mis à jour avec les nouveaux modes


## V2


#### V2.6.1

- Mise à jour de la page « Quiz Lieux » de l'écran d'aide, qui affirmait encore l'absence de limite d'essais, contredisant la limite de 5 essais introduite en V2.6.0


### V2.6.0

- Le quiz Lieux est désormais limité à 5 essais au total : chaque réponse fausse en consomme un (4 erreurs tolérées, la 5e termine le quiz), affiché en temps réel (« Erreurs : X / 5 »)
- À la fin du quiz (toutes les cartes trouvées, ou 5e erreur), toutes les cartes sont révélées : en vert si trouvée, en rouge sinon, suivies d'un écran de score dédié (« Score : X / N lieux trouvés »)


#### V2.5.1

- Correction du compteur « 0 / 0 trouvés » du quiz Lieux : le total n'était mis à jour qu'à la première trouvaille au lieu de s'afficher dès le chargement de la grille


### V2.5.0

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


### V2.4.0

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


### V2.3.0

- Ajout du champ `popularCulture` sur les entités : apparitions notables dans les jeux vidéo, films, séries et animés/mangas, renseigné pour 85 entités notables (schéma, version Room 5, formulaires d'ajout/édition et fiche détaillée) ; volontairement absent du récapitulatif du quiz pour ne pas révéler la réponse


### V2.2.0

- Pastille de difficulté colorée dans le quiz (entités et artéfacts) : vert/jaune/rouge selon le niveau facile/moyen/difficile, au lieu d'un fond neutre


### V2.1.0

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

### V2.0.0

- Ajout du système d'artéfacts (armes et objets magiques) : base de données, parcours, ajout/modification et quiz dédié


## V1


#### V1.7.1

- Rééquilibrage des niveaux de difficulté de plusieurs entités


### V1.7.0

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


### V1.6.0

- Refonte du système de score du quiz d'entités (pondéré par la difficulté) et de l'affichage des réponses


#### V1.5.1

- Adaptation de MainActivity à l'ActionBar native requise par le thème


### V1.5.0

- Ajout du choix de difficulté du quiz d'entités et renommage du package quizz→quiz


### V1.4.0

- Ajout de l'ajout/édition des lieux et de leur parcours


### V1.3.0

- Ajout de l'édition des entités et mise en forme des écrans de parcours/ajout


### V1.2.0

- Peuplement initial de la base de données mythologique (assets/prepopulate.json, populateIfEmpty)


#### V1.1.1

- Ajustements de configuration Gradle


### V1.1.0

- Mise en place du thème visuel sombre et des écrans de navigation existants (Accueil, choix Parcourir/Ajouter/Quiz)


#### V1.0.1

- Ajout du wrapper Gradle et de ressources de chaînes manquantes

### V1.0.0

- Version fonctionnelle initiale de l'application : persistance Room, navigation, écrans Accueil/Parcourir/Ajouter/Quiz


## V0


#### V0.1.2

- Ajout des classes GiantType, MuseType, ZodiacType, Realm et River


#### V0.1.1

- Réorganisation des classes du modèle en packages (base/entity/enum/sign)


### V0.1.0

- Ajout du modèle de données des entités mythologiques (dieux, titans, géants, héros, monstres, muses, archanges, chevaliers arthuriens, démons, cyclopes, hécatonchires, signes du zodiaque)

### V0.0.0

- Initialisation du dépôt (`.gitignore`, `README.md`)
