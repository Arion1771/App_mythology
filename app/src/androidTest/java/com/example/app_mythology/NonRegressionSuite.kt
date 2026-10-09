package com.example.app_mythology

import com.example.app_mythology.browse.BrowseSearchAndFilterTest
import com.example.app_mythology.database.DatabaseConstraintsTest
import com.example.app_mythology.duel.DuelTest
import com.example.app_mythology.navigation.MenuNavigationTest
import com.example.app_mythology.navigation.TopButtonsTest
import com.example.app_mythology.quiz.QuizClassicTest
import com.example.app_mythology.quiz.QuizListTest
import com.example.app_mythology.quiz.QuizPlaceTest
import com.example.app_mythology.quiz.QuizQcmTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Suite complète de non-régression, dans l'ordre de l'application : navigation
 * (menus, boutons d'angle), Données (recherche, filtre), Quiz (Classique, QCM,
 * Lieux, Liste), Duel, puis base de données. Chaque groupe correspond à un
 * package et peut aussi être lancé seul ; dans chaque classe, les tests
 * s'exécutent dans l'ordre de leur numéro (t01_, t02_...).
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    MenuNavigationTest::class,
    TopButtonsTest::class,
    BrowseSearchAndFilterTest::class,
    QuizClassicTest::class,
    QuizQcmTest::class,
    QuizPlaceTest::class,
    QuizListTest::class,
    DuelTest::class,
    DatabaseConstraintsTest::class,
)
class NonRegressionSuite
