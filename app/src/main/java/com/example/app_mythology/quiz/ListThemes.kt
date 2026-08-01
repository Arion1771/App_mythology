package com.example.app_mythology.quiz

import com.example.app_mythology.database.ArtifactEntity
import com.example.app_mythology.database.EntiteEntity

/** Un groupe de cartes affiché sous un même sous-titre (null = pas de sous-titre). */
data class ThemeGroup<T>(val header: String?, val items: List<T>)

/**
 * Un thème du mode Liste : soit un thème d'entités, soit (un seul cas : « Artefacts »)
 * un thème d'artéfacts. [maxErrors] est le palier d'essais autorisés déduit de la taille
 * totale du thème (<20 → 3, <50 → 5, sinon → 10).
 */
sealed class ListTheme(val id: String, val title: String, val maxErrors: Int) {

    class EntityTheme(
        id: String, title: String, maxErrors: Int,
        val group: (List<EntiteEntity>) -> List<ThemeGroup<EntiteEntity>>
    ) : ListTheme(id, title, maxErrors)

    class ArtifactTheme(
        id: String, title: String, maxErrors: Int,
        val group: (List<ArtifactEntity>) -> List<ThemeGroup<ArtifactEntity>>
    ) : ListTheme(id, title, maxErrors)
}

object ListThemeCatalog {

    private fun <T> List<T>.asSingleGroup(): List<ThemeGroup<T>> = listOf(ThemeGroup(null, this))

    private fun hasListTheme(e: EntiteEntity, theme: String): Boolean =
        e.listThemes?.split(",")?.map { it.trim() }?.contains(theme) == true

    private fun byMythology(entites: List<EntiteEntity>, vararg mythologies: String) =
        entites.filter { it.mythology in mythologies }.asSingleGroup()

    private fun byRace(entites: List<EntiteEntity>, race: String) =
        entites.filter { it.race == race }.asSingleGroup()

    private fun byRaceAndMythology(entites: List<EntiteEntity>, race: String, mythology: String) =
        entites.filter { it.race == race && it.mythology == mythology }.asSingleGroup()

    private fun byGodTypeAndMythology(entites: List<EntiteEntity>, godType: String, mythology: String) =
        entites.filter { it.godType == godType && it.mythology == mythology }.asSingleGroup()

    private fun byListTheme(entites: List<EntiteEntity>, theme: String) =
        entites.filter { hasListTheme(it, theme) }.asSingleGroup()

    val all: List<ListTheme> = listOf(
        ListTheme.EntityTheme("mythologie_grecque", "Mythologie grecque", 10) {
            byMythology(it, "Grecque")
        },
        ListTheme.EntityTheme("mythologie_romaine", "Mythologie romaine", 10) {
            byMythology(it, "Romaine")
        },
        ListTheme.EntityTheme("mythologie_hindoue", "Mythologie hindoue", 3) {
            byMythology(it, "Hindouisme")
        },
        ListTheme.EntityTheme("mythologie_chinoise", "Mythologie chinoise", 5) {
            byMythology(it, "Chinoise")
        },
        ListTheme.EntityTheme("mythologie_shinto", "Mythologie shinto", 3) {
            byMythology(it, "Shinto")
        },
        ListTheme.EntityTheme("mythologie_amerique_sud", "Mythologie d'Amérique du Sud", 3) {
            byMythology(it, "Maya", "Aztèque")
        },
        ListTheme.EntityTheme("dieux", "Dieux", 10) {
            byRace(it, "God")
        },
        ListTheme.EntityTheme("monstres", "Monstres", 10) {
            byRace(it, "Monster")
        },
        ListTheme.EntityTheme("heros", "Héros", 5) {
            byRace(it, "Heroes")
        },
        ListTheme.ArtifactTheme("artefacts", "Artefacts", 5) { artifacts ->
            artifacts.asSingleGroup()
        },
        ListTheme.EntityTheme("entites", "Entités", 10) {
            it.asSingleGroup()
        },
        ListTheme.EntityTheme("muses", "Muses", 3) { entites ->
            listOf(
                ThemeGroup("Classiques", entites.filter { it.museType == "Grecque" && it.mythology == "Grecque" }),
                ThemeGroup("Béotiennes", entites.filter { it.museType == "Beotienne" })
            )
        },
        ListTheme.EntityTheme("olympiens_grecs", "Olympiens grecs", 3) {
            byGodTypeAndMythology(it, "Olympien", "Grecque")
        },
        ListTheme.EntityTheme("olympiens_romains", "Olympiens romains", 3) {
            byGodTypeAndMythology(it, "Olympien", "Romaine")
        },
        ListTheme.EntityTheme("geants_grecs", "Géants grecs", 3) {
            byRaceAndMythology(it, "Giant", "Grecque")
        },
        ListTheme.EntityTheme("enfants_gaia_ouranos", "Enfants de Gaïa et Ouranos", 5) { entites ->
            val grecques = entites.filter { it.mythology == "Grecque" }
            listOf(
                ThemeGroup("Titans", grecques.filter { it.race == "Titan" }),
                ThemeGroup("Cyclopes", grecques.filter { it.race == "Cyclope" }),
                ThemeGroup("Hécatonchires", grecques.filter { it.race == "Hecatoncheires" })
            )
        },
        ListTheme.EntityTheme("monstres_12_travaux", "Monstres des 12 travaux", 3) {
            byListTheme(it, "Monstres des 12 travaux")
        },
        ListTheme.EntityTheme("guerriers_troie", "Guerriers grecs devant Troie", 3) {
            byListTheme(it, "Guerriers grecs devant Troie")
        },
        ListTheme.EntityTheme("archanges_demons", "Archanges et démons", 3) { entites ->
            listOf(
                ThemeGroup("Archanges", entites.filter { it.race == "Archangels" }),
                ThemeGroup("Démons", entites.filter { it.race == "Demon_Prince" })
            )
        },
        ListTheme.EntityTheme("zodiaque", "Signes du zodiaque", 5) { entites ->
            listOf(
                ThemeGroup("Classiques", entites.filter { it.zodiacType == "Classique" }),
                ThemeGroup("Chinois", entites.filter { it.zodiacType == "Chinois" })
            )
        },
        ListTheme.EntityTheme("grees", "Grées", 3) {
            byRace(it, "Grées")
        },
        ListTheme.EntityTheme("erinyes", "Érinyes", 3) {
            byRace(it, "Erinyes")
        },
        ListTheme.EntityTheme("yokais", "Yokais", 3) {
            byListTheme(it, "Yokais")
        },
        ListTheme.EntityTheme("argonautes", "Argonautes", 3) {
            byListTheme(it, "Argonautes")
        },
        ListTheme.EntityTheme("chevaliers_table_ronde", "Chevaliers de la table ronde", 3) {
            byListTheme(it, "Chevaliers de la table ronde")
        },
        ListTheme.EntityTheme("grands_dieux_egypte", "Grands dieux d'Égypte", 3) {
            byListTheme(it, "Grands dieux d'Égypte")
        },
        ListTheme.EntityTheme("monstres_arbre_monde", "Monstres de l'arbre monde", 3) {
            byListTheme(it, "Monstres de l'arbre monde")
        },
    )

    fun byId(id: String): ListTheme? = all.firstOrNull { it.id == id }
}

/** Représentation unifiée d'une carte du mode Liste, qu'elle vienne d'une entité ou d'un artéfact. */
data class ListItem(val id: Int, val name: String, val detailText: String)

private fun entityDetailText(e: EntiteEntity) = buildString {
    appendLine("Mythologie : ${e.mythology}")
    e.domain?.let { appendLine("Domaine : $it") }
    e.godType?.let { appendLine("Type divin : $it") }
    e.fatherName?.let { appendLine("Père : $it") }
    e.motherName?.let { appendLine("Mère : $it") }
    e.equivalentName?.let { appendLine("Équivalent : $it") }
    e.giantType?.let { appendLine("Type de géant : $it") }
    e.opponentName?.let { appendLine("Opposant : $it") }
    e.story?.let { appendLine("Histoire : $it") }
    e.killer?.let { appendLine("Tué par : $it") }
    e.ascendantName?.let { appendLine("Ascendant : $it") }
    e.monsterType?.let { appendLine("Type : $it") }
    e.description?.let { appendLine("Description : $it") }
    e.museType?.let { appendLine("Type de muse : $it") }
    e.role?.let { appendLine("Rôle : $it") }
    e.death?.let { appendLine("Mort : $it") }
    e.zodiacType?.let { appendLine("Zodiaque : $it") }
    e.popularCulture?.let { appendLine("Culture populaire : $it") }
}.trimEnd()

private fun artifactDetailText(a: ArtifactEntity) = buildString {
    appendLine("Mythologie : ${a.mythology}")
    appendLine("Type : ${a.artifactType}")
    a.ownerName?.let { appendLine("Propriétaire : $it") }
    a.creatorName?.let { appendLine("Créateur : $it") }
    a.power?.let { appendLine("Pouvoir : $it") }
    a.story?.let { appendLine("Histoire : $it") }
    a.description?.let { appendLine("Description : $it") }
}.trimEnd()

/** Résout un thème en groupes de [ListItem], quelle que soit sa source (entités ou artéfacts). */
fun ListTheme.resolveGroups(entites: List<EntiteEntity>, artifacts: List<ArtifactEntity>): List<ThemeGroup<ListItem>> =
    when (this) {
        is ListTheme.EntityTheme -> group(entites).map { g ->
            ThemeGroup(g.header, g.items.map { ListItem(it.id, it.name, entityDetailText(it)) })
        }
        is ListTheme.ArtifactTheme -> group(artifacts).map { g ->
            ThemeGroup(g.header, g.items.map { ListItem(it.id, it.name, artifactDetailText(it)) })
        }
    }
