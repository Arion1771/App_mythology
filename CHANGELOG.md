# Changelog

## V4

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

### V4.3.0

- Quiz Entités (Classique et QCM uniquement — Artéfacts et Duel inchangés) repensé avec une difficulté adaptative à deux niveaux : en plus du niveau canonique choisi (Facile/Moyen/Difficile), chaque entité acquiert un niveau interne (1 à 3) propre aux performances du joueur sur elle, établi définitivement dès qu'elle a été rencontrée 10 fois (fautes sur ses 10 dernières rencontres : ≤3 → niveau 1, ≥8 → niveau 3, sinon 2 ; une faute vaut 1 si jamais trouvée, 0,5 si trouvée au second essai en Classique, 0 sinon)
- Les trois niveaux (Facile/Moyen/Difficile) proposent désormais chacun 15 questions puisées uniquement parmi les entités du niveau canonique choisi, au lieu de 10/20/30 questions cumulées sur les niveaux inférieurs ; une fois le niveau interne établi pour toutes les entités du niveau canonique, le tirage se fait à 5 questions par niveau interne (1/2/3, avec repli sur les niveaux voisins si l'un d'eux manque d'entités)
- Les points d'une question et la pastille de difficulté affichée utilisent ce niveau interne plutôt que la seule difficulté canonique, une fois celui-ci établi
- Le niveau interne et l'historique de rencontres d'une entité sont conservés lors des mises à jour de `prepopulate.json`, sauf si son nom, sa mythologie, sa race, son domaine, son indice ou sa difficulté changent
- Écran d'aide du quiz mis à jour en conséquence

#### V4.2.4

- Écran de choix du mode en Duel (Classique/QCM) passé aux plaques-images `assets/button/classique.png` et `assets/button/qcm.png`, déjà utilisées par l'écran de choix du quiz solo pour la même notion, au lieu de deux boutons texte unis

#### V4.2.3

- Nouvelles versions des 6 plaques générées en V4.1.0 (QCM, Classique, Liste, Arbre Monde, Fleuves de l'Enfer, Royaume des Morts) : recadrées au ras de la plaque et allégées en 8 bits (1226×422, comme les 6 autres boutons), après un premier dépôt en 1920×1080/16 bits avec une grosse marge vide qui les aurait fait apparaître nettement plus petites que les autres boutons à l'écran

#### V4.2.2

- Ajout du dossier `assets/background/` (vide pour l'instant, avec un `.gitkeep`), prêt à recevoir les images des futurs thèmes de fond

#### V4.2.1

- Les thèmes de fond se chargent désormais depuis `assets/background/<asset>.png` (`BackgroundAssets`), au lieu d'un drawable statique par thème, à la manière des boutons (`assets/button/`) et des succès (`assets/achievements/`) : ajouter un nouveau thème de fond ne demande plus que de déposer l'image correspondante. Tant qu'aucune image n'est fournie, le thème « Sombre (défaut) » se rabat sur `bg_theme_default` (même halo discret qu'en V4.2.0)

### V4.2.0

- Mise en place d'un système de thèmes de fond pour l'ensemble des écrans de l'application : un seul thème disponible pour l'instant (« Sombre (défaut) », un halo radial discret proche de l'ancien fond uni `@color/surface`), mais l'architecture (`BackgroundTheme`, `BackgroundThemeManager`, persistance par SharedPreferences) est prête à en accueillir d'autres. Le fond est appliqué une seule fois au conteneur racine de `MainActivity`, tous les écrans restant transparents ; pas encore d'écran de sélection, à venir

#### V4.1.1

- Ajout des 4 emblèmes de succès manquants dans `assets/achievements/` (`entity_arion`, `entity_ulysse_telemaque`, `collection_dogs`, `collection_animals`) : ces succès affichaient jusqu'ici le blason générique de secours. Blason générique par défaut fourni pour chacun, à remplacer individuellement.

### V4.1.0

- Refonte du skin des boutons : tous les écrans de menu à trois boutons principaux (accueil, Parcourir, choix du quiz, quiz Lieux, choix du domaine) utilisent désormais des boutons-images (plaques de bois gravées) chargés depuis `assets/button/`, au lieu de boutons texte
- Les 6 plaques manquantes (QCM, Classique, Liste, Arbre Monde, Fleuves de l'Enfer, Royaume des Morts) ont été générées dans le même style que les 6 fournies ; les 12 plaques sont normalisées au même cadrage et allégées (8 bits, 1280×720)
- Nouveau gabarit partagé `view_primary_buttons` : la colonne des trois boutons est toujours centrée à l'écran, sans marge, donc placée au pixel près de façon identique d'un écran à l'autre
- Le placement identique des trois boutons est verrouillé par un test automatique (`PrimaryButtonPlacementTest`, branche Test-Unit)

### V4.0.0

- Refonte des menus : l'écran d'accueil expose désormais trois boutons principaux — « Données » (consulter la base), « Quizz » (lancer les quiz) et « Duel » (lancer le mode duel multijoueur local) — au lieu de deux boutons plus deux icônes de coin
- L'icône Duel du coin supérieur gauche disparaît (Duel devient un bouton principal) et laisse place à l'affichage du numéro de version de l'application (Vn.n.n), renseigné automatiquement depuis `versionName` et mis à jour à chaque nouvelle version
- L'icône Succès (🏆) reste accessible dans le coin supérieur droit
- Objectif d'ergonomie retenu pour la suite : trois boutons principaux par écran, et une refonte ultérieure du fond et du style des boutons sur l'ensemble des écrans

## V3

#### V3.4.4

- Dans le quiz Liste, les thèmes de mythologie complète (grecque, romaine, hindoue, chinoise, shinto, Amérique du Sud) affichent désormais leurs cartes sous-groupées par race (ordre alphabétique), et les thèmes de race complète (Dieux, Monstres, Héros) ainsi que le thème global (Entités) sous-groupées par mythologie
- Nouvel ordre canonique des mythologies, par nombre total d'entités décroissant (remplace l'ordre alphabétique) : appliqué à ces regroupements du quiz Liste, au menu déroulant de filtre par mythologie en mode Parcourir (entités et artéfacts), et à l'ensemble de Base.md (sections Entités, Lieux, Artéfacts)
- Correction incidente dans Base.md d'une mention obsolète du formulaire d'ajout/édition (retiré en V3.3.0)

#### V3.4.3

- Ajout de deux succès de collection, débloqués dès que toutes les entités d'une catégorie ont été obtenues au moins une fois en quiz Classique ou QCM, toutes sessions confondues : « C'est bien, bon toutou » (toutes les entités de type Chien) et « Très bizarre ton Zoo » (toutes les entités portant le tag Animal)

#### V3.4.2

- Orthos rejoint le type de monstre « Chien » (auparavant « Chien à Deux Têtes »), commun à Cerbère, Lélaps et Garm
  - Ajout/Modification d'entités :
    - Orthos : Chien à Deux Têtes -> Chien (type de monstre modifié)

#### V3.4.1

- Ajout de 3 nouvelles entités niveau 3 : Shango (Yoruba), Hinezumi (Japonaise), Huoshu (Chinoise)
- Ajout des 23 dieux/monstres/héros présents dans Smite et/ou Smite 2 encore absents de la base, tous niveau 3 : Méduse et Arachné (Grecque), Bakasura, Kumbhakarna, Rama, Ravana et Vamana (Hindouisme), Chang'e, Da Ji, Guan Yu, Hou Yi, Jing Wei, Hua Mulan, Sun Wukong, Yu Huang et Zhong Kui (Chinoise), Xbalanque (Maya), Sol, Ullr et Ymir (Nordique), Danzaburou et Kuzenbo (Japonaise), Cu Chulainn (Celtique Irlandaise)
  - Ajout/Modification d'entités :
    - Shango (ajout)
    - Hinezumi (ajout)
    - Huoshu (ajout)
    - Méduse (ajout)
    - Arachné (ajout)
    - Bakasura (ajout)
    - Kumbhakarna (ajout)
    - Rama (ajout)
    - Ravana (ajout)
    - Vamana (ajout)
    - Chang'e (ajout)
    - Da Ji (ajout)
    - Guan Yu (ajout)
    - Hou Yi (ajout)
    - Jing Wei (ajout)
    - Hua Mulan (ajout)
    - Sun Wukong (ajout)
    - Yu Huang (ajout)
    - Zhong Kui (ajout)
    - Xbalanque (ajout)
    - Sol (ajout)
    - Ullr (ajout)
    - Ymir (ajout)
    - Danzaburou (ajout)
    - Kuzenbo (ajout)
    - Cu Chulainn (ajout)

### V3.4.0

- prepopulate.json devient la base complète dans sa forme finale : au lancement, dès que son contenu change (comparaison par hash), la base locale est intégralement vidée puis rechargée à l'identique du JSON, remplaçant la resynchronisation protectrice de V3.2.8
- Champ `userModified` (V3.2.8) retiré ; Room passe en version 8

### V3.3.0

- Retrait complet de la fonctionnalité d'ajout, de modification et de suppression manuelle d'une entité, d'un lieu ou d'un artéfact depuis l'application (écrans « Ajouter »/« Modifier », 7 fragments et leurs layouts, actions de navigation associées) : un tel changement aurait de toute façon été écrasé au prochain rechargement de la base depuis prepopulate.json


#### V3.2.8

- prepopulate.json n'était lu qu'au premier lancement (base vide) ; les corrections et ajouts de contenu publiés dans les mises à jour n'atteignaient donc jamais les installations déjà lancées une fois. La base se resynchronise désormais avec prepopulate.json à chaque changement détecté de son contenu (comparaison par hash), sans jamais écraser une entrée créée ou éditée par l'utilisateur (nouveau champ `userModified`, Room v7, migration non destructive)
- Limite transitoire assumée : les lignes déjà en base avant cette mise à jour démarrent protégées par défaut, donc les correctifs déjà publiés (V3.2.1 à V3.2.7) ne rattraperont pas automatiquement les installations existantes ; seuls les contenus futurs se resynchroniseront normalement


#### V3.2.7

- L'écran plein affiché au clic sur une carte non trouvée du mode Liste expose désormais tous les champs de l'entité/de l'artéfact (race, niveau, primordial, équivalent chinois, indice, tags, thèmes de liste selon le cas) au lieu d'un sous-ensemble


#### V3.2.6

- Le niveau (difficulté) d'une entité s'affiche désormais sur sa fiche de détail en base, et se règle via un sélecteur Facile/Moyen/Difficile dans le formulaire d'ajout/édition (jusque-là inaccessible depuis l'interface ; une entité éditée retombait silencieusement à Facile)


#### V3.2.5

- Ajout de deux nouveaux succès : « Très Beau Pseudonyme » (Arion) et « Nono le Robot est fière de toi » (Ulysse ou Télémaque), en quiz Classique ou QCM


#### V3.2.4

- Correction de la description de Camazotz, qui reprenait par erreur celle de Hun Batz (le singe transformé, saint patron des artisans) au lieu de décrire le dieu chauve-souris associé à la nuit et au sacrifice
- Retrait de « Pendragon » du nom d'Arthur et d'Uther, patronyme tardif absent des sources arthuriennes les plus anciennes
  - Ajout/Modification d'entités :
    - Arthur : Arthur Pendragon -> Arthur (nom modifié)
    - Uther : Uther Pendragon -> Uther (nom modifié)
    - Camazotz : "Transformé en singe par ses demi-frères jaloux de son talent, il devient avec son jumeau le saint patron des artistes et des artisans." -> "Dieu chauve-souris associé à la nuit, à la mort et au sacrifice, il règne sur la Maison des Chauves-souris de Xibalba où il décapite l'un des Jumeaux Héros dans le Popol Vuh." (description modifiée)


#### V3.2.3

- Retrait du tag Principal (fourre-tout hérité du repérage heuristique de V3.0.0, sans valeur thématique) sur les 214 entités qui le portaient
- Ajout de 7 nouveaux tags thématiques sur les entités concernées : Sentiments, Ombres, Lune, Création, Richesse, Troyens et Achéens (ces deux derniers pour les deux camps de la guerre de Troie)


#### V3.2.2

- Ajout de Néoptolème et Philoctète, rattachés au thème « Guerriers grecs devant Troie » du mode Liste, ainsi que de Télémaque
  - Ajout/Modification d'entités :
    - Néoptolème (ajout)
    - Philoctète (ajout)
    - Télémaque (ajout)


#### V3.2.1

- Ajout de nouvelles références de culture populaire (Odyssée, Iliade, Ulysse 31, Les Héros de l'Olympe, Les Travaux d'Apollon, Magnus Chase, Les Chroniques de Kane, One Piece, Seven Deadly Sins) sur les entités concernées
- Ajout d'un repère « Astronomie » sur les entités correspondant à une constellation officielle (les 12 signes du zodiaque classiques, et des figures catastérisées : Orion, Persée, Pégase, Hydre, Hercule, Chiron, Ladon, Aigle du Caucase, Licorne, Phénix, Lion de Némée)


### V3.2.0

- Ajout du mode Duel : quiz multijoueur local en pass-and-play, de 2 à 12 joueurs sur le même appareil, accessible depuis une icône dédiée sur l'accueil
- Mise en place : nombre de joueurs, noms, type de question au choix (Classique ou QCM), difficulté commune (Facile/Moyen/Difficile = 10/20/30 questions par joueur), questions identiques pour tous ou différentes selon le choix
- Déroulé en tours : chaque joueur répond à une question à son tour, sur 3 écrans successifs (annonce du joueur et de son score, question, récapitulatif), jusqu'à ce que tout le monde ait répondu à toutes ses questions ; classement final trié par score à l'issue de la partie
- Nouvel écran d'aide dédié au mode Duel
- Volontairement indépendant du système de succès (V3.1.0)


### V3.1.0

- Ajout d'un système de succès : une cinquantaine d'objectifs cachés (répondre à une entité précise, terminer un quiz Classique/QCM sans faute ou totalement à côté, compléter un thème de liste ou un quiz Lieux à moins de 3 fautes...) se débloquent une seule fois et restent acquis lors des mises à jour de l'application (stockage séparé de la base, non affecté par sa purge à chaque mise à jour)
- Un bandeau doré s'affiche 3 secondes en haut de l'écran lors du déblocage d'un succès
- Nouvel écran « Succès » accessible depuis l'accueil (icône trophée) : chaque succès affiche son nom et son emblème (rectangle gris si non obtenu), et sa méthode d'obtention en plus une fois débloqué (rectangle doré)
- Dossier d'images d'emblèmes prêt à l'emploi (un blason générique par défaut, à remplacer individuellement par succès)


#### V3.0.2

- Complétion des tags thématiques manquants pour des entités qui n'avaient hérité que du tag Principal lors de l'assignation heuristique de V3.0.0, faute de correspondance repérée avec un mot-clé
  - Ajout/Modification d'entités :
    - Poséidon : Principal -> Principal, Eau (tags modifiés)
    - Cymopolée : Principal -> Principal, Eau (tags modifiés)
    - Mélinoé : Principal -> Principal, Mort (tags modifiés)
    - Discordia : Principal -> Principal, Guerre (tags modifiés)
    - Janus : Principal -> Principal, Gardien (tags modifiés)
    - Silvanus : Principal -> Principal, Gardien (tags modifiés)
    - Ptah : Principal -> Principal, Artisanat (tags modifiés)
    - Hâpy : Principal -> Principal, Eau, Fertilité (tags modifiés)
    - Taouret : Principal -> Principal, Fertilité (tags modifiés)
    - Ériu : Principal -> Principal, Royauté, Terre (tags modifiés)
    - Ah-Muzen-Cab : Principal -> Principal, Animal (tags modifiés)
    - Camazotz : Principal -> Principal, Animal (tags modifiés)
    - Kali : Principal -> Principal, Mort (tags modifiés)


#### V3.0.1

- L'écran de choix des quiz propose désormais directement les trois types (QCM, Classique, Liste, dans cet ordre) ; le domaine (Entités, Artéfacts, Lieux selon ce qui existe pour le type choisi) se sélectionne ensuite sur un écran dédié, inversant l'ordre précédent (domaine puis mode)
- Le mode QCM affiche désormais, comme le mode Classique, un écran dédié après chaque réponse (juste ou fausse) avec le nom coloré (vert/rouge) et toutes les informations, suivi d'un bouton vers la question suivante — au lieu d'un simple retour visuel bref suivi d'un enchaînement immédiat


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
- Ajout/Modification d'entités :
  - Agamemnon (ajout)
  - Ajax le Petit (ajout)
  - Ajax le Grand : Ajax -> Ajax le Grand (nom modifié)
  - Diomède (ajout)
  - Ménélas (ajout)
  - Nestor (ajout)
  - Patrocle (ajout)
  - Bake Kujira (ajout)
  - Moritasgus (ajout)


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
- Ajout/Modification d'entités :
  - Sariel (ajout)
  - Freyja : Freya -> Freyja (nom modifié)
  - Valkyrie : Valkyries -> Valkyrie (nom modifié)
  - Nephtys : Nephthys -> Nephtys (nom modifié)


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
- Ajout/Modification d'entités :
  - Iapetus : Lapetus -> Iapetus (nom modifié)
  - Poissons : Poisson -> Poissons (nom modifié)
  - Dvalin : Dwalin -> Dvalin (nom modifié)
  - Cottos : Cotos -> Cottos (nom modifié)
  - Goibniu : Goibnu -> Goibniu (nom modifié)
  - Érèbe : Erèbe -> Érèbe (nom modifié)
  - Érinyes : Erinyes -> Érinyes (nom modifié)
  - Éros : Eros -> Éros (nom modifié)
  - Éphialtès : Ephialtès -> Éphialtès (nom modifié)
  - Ériu : Eriu -> Ériu (nom modifié)
  - Étain : Etain -> Étain (nom modifié)
  - Sanglier d'Érymanthe : Sanglier d'Erymanthe -> Sanglier d'Érymanthe (nom modifié)
  - Astréos : Astreos -> Astréos (nom modifié)
  - Coéos : Coeos -> Coéos (nom modifié)
  - Eurymédon : Eurymedon -> Eurymédon (nom modifié)
  - Océanos : Oceanos -> Océanos (nom modifié)
  - Lélaps : Lelaps -> Lélaps (nom modifié)
  - 9 Muses : 9 muses -> 9 Muses (nom modifié)
  - Susanoo : Susano -> Susanoo (nom modifié)
  - Dame du Lac : La dame du Lac -> Dame du Lac (nom modifié)
  - Qilin : Kirin -> Qilin (nom modifié)
  - Ogma : Ogmios -> Ogma (nom modifié)
  - Pele : Pélé -> Pele (nom modifié)
  - Bouddha : Buddha -> Bouddha (nom modifié)
  - Rê : Ré -> Rê (nom modifié)
  - Mélinoé (ajout)
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
  - Ajout/Modification d'entités :
    - Astreos : 1 -> 2 (difficulté modifiée)
    - Circé : 1 -> 2 (difficulté modifiée)
    - Coeos : 1 -> 2 (difficulté modifiée)
    - Erinyes : 1 -> 2 (difficulté modifiée)
    - Grées : 1 -> 2 (difficulté modifiée)
    - Héméra : 1 -> 2 (difficulté modifiée)
    - Médée : 1 -> 2 (difficulté modifiée)
    - Morphée : 1 -> 2 (difficulté modifiée)
    - Orphée : 1 -> 2 (difficulté modifiée)
    - Prométhée : 1 -> 2 (difficulté modifiée)
    - Sanglier d'Erymanthe : 1 -> 2 (difficulté modifiée)
    - Séléné : 1 -> 2 (difficulté modifiée)
    - Aura : 3 -> 2 (difficulté modifiée)
    - Euros : 3 -> 2 (difficulté modifiée)
    - Néphélée : 3 -> 2 (difficulté modifiée)
    - Notos : 3 -> 2 (difficulté modifiée)
    - Aeolus : 1 -> 2 (difficulté modifiée)
    - Faunus : 1 -> 2 (difficulté modifiée)
    - Furies : 1 -> 2 (difficulté modifiée)
    - Luna : 1 -> 2 (difficulté modifiée)
    - Sol : 1 -> 2 (difficulté modifiée)
    - Chac : 2 -> 3 (difficulté modifiée)


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
  - Ajout/Modification d'entités :
    - Erinyes (ajout)
    - Grées (ajout)
    - Valkyries (ajout)


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
