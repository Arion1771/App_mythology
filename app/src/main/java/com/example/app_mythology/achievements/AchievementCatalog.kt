package com.example.app_mythology.achievements

import com.example.app_mythology.quiz.ListThemeCatalog

/** Catalogue statique de tous les succès de l'application. */
object AchievementCatalog {

    // Complément grammatical de "Connaisseur " pour chaque thème du mode Liste.
    private val listThemeNameSuffix: Map<String, String> = mapOf(
        "mythologie_grecque" to "de la mythologie grecque",
        "mythologie_romaine" to "de la mythologie romaine",
        "mythologie_hindoue" to "de la mythologie hindoue",
        "mythologie_chinoise" to "de la mythologie chinoise",
        "mythologie_shinto" to "de la mythologie shinto",
        "mythologie_amerique_sud" to "de la mythologie d'Amérique du Sud",
        "dieux" to "des dieux",
        "monstres" to "des monstres",
        "heros" to "des héros",
        "artefacts" to "des artéfacts",
        "entites" to "des entités",
        "muses" to "des muses",
        "olympiens_grecs" to "des olympiens grecs",
        "olympiens_romains" to "des olympiens romains",
        "geants_grecs" to "des géants grecs",
        "enfants_gaia_ouranos" to "des enfants de Gaïa et Ouranos",
        "monstres_12_travaux" to "des monstres des 12 travaux",
        "guerriers_troie" to "des guerriers grecs devant Troie",
        "archanges_demons" to "des archanges et démons",
        "zodiaque" to "des signes du zodiaque",
        "grees" to "des Grées",
        "erinyes" to "des érinyes",
        "yokais" to "des yokais",
        "argonautes" to "des Argonautes",
        "chevaliers_table_ronde" to "des chevaliers de la table ronde",
        "grands_dieux_egypte" to "des grands dieux d'Égypte",
        "monstres_arbre_monde" to "des monstres de l'arbre monde"
    )

    /** Un succès par thème du mode Liste (id `list_<themeId>`), plus le succès méta `list_all`. */
    private val listAchievements: List<Achievement> = ListThemeCatalog.all.map { theme ->
        val suffix = listThemeNameSuffix[theme.id] ?: "de ${theme.title.lowercase()}"
        Achievement(
            id = "list_${theme.id}",
            name = "Connaisseur $suffix",
            method = "Complétez le thème « ${theme.title} » du quiz Liste sans dépasser la limite d'erreurs",
            category = "Listes"
        )
    }

    val all: List<Achievement> = buildList {
        // ── Entités — cibles nommées ─────────────────────────────────────
        add(Achievement("entity_heimdall", "Toi tu prends ton ban", "Répondez correctement à Heimdall dans un quiz Classique ou QCM", "Entités"))
        add(Achievement("entity_olorun", "Unique soleil du mid", "Répondez correctement à Olorun dans un quiz Classique ou QCM", "Entités"))
        add(Achievement("entity_bakekujira", "T'appelles ça un soutien ?", "Répondez correctement à Bake Kujira dans un quiz Classique ou QCM", "Entités"))
        add(Achievement("entity_ganesh", "Éléphant en ballon", "Répondez correctement à Ganesh dans un quiz Classique ou QCM", "Entités"))
        add(Achievement("entity_moritasgus", "Blaireau Divin", "Répondez correctement à Moritasgus dans un quiz Classique ou QCM", "Entités"))

        // ── Entités — paliers de score parfait ───────────────────────────
        add(Achievement("classic_entity_10", "Mortel du classique", "Terminez un quiz Classique Entités niveau Facile sans faute (score 10/10)", "Entités"))
        add(Achievement("classic_entity_30", "Demi-dieu du classique", "Terminez un quiz Classique Entités niveau Moyen sans faute (score 30/30)", "Entités"))
        add(Achievement("classic_entity_60", "Dieu du classique", "Terminez un quiz Classique Entités niveau Difficile sans faute (score 60/60)", "Entités"))
        add(Achievement("qcm_entity_10", "C'est un bon début", "Terminez un quiz QCM Entités niveau Facile sans faute (score 10/10)", "Entités"))
        add(Achievement("qcm_entity_30", "Imposteur", "Terminez un quiz QCM Entités niveau Moyen sans faute (score 30/30)", "Entités"))
        add(Achievement("qcm_entity_60", "Usurpateur", "Terminez un quiz QCM Entités niveau Difficile sans faute (score 60/60)", "Entités"))

        // ── Global ────────────────────────────────────────────────────────
        add(Achievement("all_wrong", "C'est très très mauvais", "Terminez un quiz Classique ou QCM (Entités ou Artéfacts) sans une seule bonne réponse", "Général"))

        // ── Lieux ─────────────────────────────────────────────────────────
        add(Achievement("place_underworld", "T'as déjà visité ?", "Terminez le quiz Lieux « Le Royaume des Morts » avec moins de 3 erreurs", "Lieux"))
        add(Achievement("place_rivers", "Prenons le tajine dorée !", "Terminez le quiz Lieux « Fleuves de l'Enfer » avec moins de 3 erreurs", "Lieux"))
        add(Achievement("place_yggdrasil", "Paysagiste Divin", "Terminez le quiz Lieux « Arbre Monde » avec moins de 3 erreurs", "Lieux"))
        add(Achievement("place_all", "Dora l'exploratrice", "Obtenez les trois succès de lieux ci-dessus", "Lieux"))

        // ── Artéfacts — paliers de score parfait ─────────────────────────
        add(Achievement("classic_artifact_10", "Goblin du classique", "Terminez un quiz Classique Artéfacts niveau Facile sans faute (score 10/10)", "Artéfacts"))
        add(Achievement("classic_artifact_30", "Griffon du classique", "Terminez un quiz Classique Artéfacts niveau Moyen sans faute (score 30/30)", "Artéfacts"))
        add(Achievement("classic_artifact_60", "Dragon du classique", "Terminez un quiz Classique Artéfacts niveau Difficile sans faute (score 60/60)", "Artéfacts"))
        add(Achievement("qcm_artifact_10", "Pilleur", "Terminez un quiz QCM Artéfacts niveau Facile sans faute (score 10/10)", "Artéfacts"))
        add(Achievement("qcm_artifact_30", "Faussaire", "Terminez un quiz QCM Artéfacts niveau Moyen sans faute (score 30/30)", "Artéfacts"))
        add(Achievement("qcm_artifact_60", "Trafiquant", "Terminez un quiz QCM Artéfacts niveau Difficile sans faute (score 60/60)", "Artéfacts"))

        // ── Listes ────────────────────────────────────────────────────────
        addAll(listAchievements)
        add(Achievement("list_all", "Faut sortir la", "Complétez les 27 thèmes du quiz Liste", "Listes"))
    }

    fun byId(id: String): Achievement? = all.firstOrNull { it.id == id }
}
