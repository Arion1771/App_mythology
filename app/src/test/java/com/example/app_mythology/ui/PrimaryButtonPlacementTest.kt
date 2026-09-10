package com.example.app_mythology.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Verrou de cohérence du placement des trois boutons principaux (branche
 * Test-Unit).
 *
 * Tous les écrans de menu à trois boutons doivent afficher la colonne
 * partagée `view_primary_buttons` de la même façon — centrée, sans marge —
 * pour que les trois boutons tombent au même pixel d'un écran à l'autre.
 * Analyse statique des layouts XML (pas de rendu), donc exécutable en test
 * JVM sans émulateur.
 */
class PrimaryButtonPlacementTest {

    private val screens = listOf(
        "fragment_home",
        "fragment_browse_choice",
        "fragment_quiz_choice",
        "fragment_quiz_place_choice",
        "fragment_quiz_domain_choice",
    )

    private fun layout(name: String): Element {
        val f = listOf("src/main/res/layout/$name.xml", "app/src/main/res/layout/$name.xml")
            .map(::File).firstOrNull { it.exists() }
            ?: error("layout $name introuvable (cwd=${File(".").absolutePath})")
        val factory = DocumentBuilderFactory.newInstance().apply { isNamespaceAware = false }
        return factory.newDocumentBuilder().parse(f).documentElement
    }

    private fun Element.childElements(): List<Element> {
        val out = ArrayList<Element>()
        val kids = childNodes
        for (i in 0 until kids.length) (kids.item(i) as? Element)?.let(out::add)
        return out
    }

    private fun Element.descendants(): List<Element> =
        childElements() + childElements().flatMap { it.descendants() }

    private fun Element.androidAttrs(): Map<String, String> {
        val out = LinkedHashMap<String, String>()
        val a = attributes
        for (i in 0 until a.length) {
            val n = a.item(i)
            if (n.nodeName.startsWith("android:")) out[n.nodeName] = n.nodeValue
        }
        return out
    }

    // ── La colonne partagée ────────────────────────────────────────────────

    @Test
    fun `view_primary_buttons stacks three identical ImageButtons`() {
        val root = layout("view_primary_buttons")
        assertEquals("LinearLayout", root.tagName)
        assertEquals("vertical", root.getAttribute("android:orientation"))

        val buttons = root.childElements().filter { it.tagName == "ImageButton" }
        assertEquals("la colonne doit contenir exactement 3 boutons", 3, buttons.size)
        assertEquals(
            listOf("@+id/btn_primary_1", "@+id/btn_primary_2", "@+id/btn_primary_3"),
            buttons.map { it.getAttribute("android:id") },
        )

        val shape = buttons.map {
            listOf(
                it.getAttribute("android:layout_width"),
                it.getAttribute("android:layout_height"),
                it.getAttribute("android:scaleType"),
                it.getAttribute("android:adjustViewBounds"),
                it.getAttribute("android:padding"),
                it.getAttribute("android:layout_marginBottom"),
            )
        }
        assertEquals("les 3 boutons doivent avoir des attributs de forme identiques", 1, shape.toSet().size)
        val s = shape.first()
        assertEquals("match_parent", s[0])
        assertEquals("wrap_content", s[1])
        assertEquals("fitCenter", s[2])
        assertEquals("true", s[3])
        assertEquals("0dp", s[4])
    }

    // ── Chaque écran inclut la colonne de la même façon ────────────────────

    @Test
    fun `every three-button screen is a FrameLayout including the shared column, centered and marginless`() {
        val signatures = HashSet<Map<String, String>>()

        for (name in screens) {
            val root = layout(name)
            assertEquals("$name : la racine doit être un FrameLayout", "FrameLayout", root.tagName)

            val includes = root.descendants().filter { it.tagName == "include" }
                .filter { it.getAttribute("layout") == "@layout/view_primary_buttons" }
            assertEquals("$name : doit inclure view_primary_buttons exactement une fois", 1, includes.size)

            val inc = includes.first().androidAttrs()
            assertEquals("$name : la colonne doit être centrée", "center", inc["android:layout_gravity"])
            assertEquals("$name : largeur d'inclusion", "match_parent", inc["android:layout_width"])
            assertEquals("$name : hauteur d'inclusion", "wrap_content", inc["android:layout_height"])
            assertTrue(
                "$name : aucune marge ne doit décaler la colonne (${inc.keys})",
                inc.keys.none { it.startsWith("android:layout_margin") },
            )
            signatures.add(inc)
        }

        assertEquals(
            "les 5 écrans doivent inclure la colonne avec exactement les mêmes attributs",
            1, signatures.size,
        )
    }
}
