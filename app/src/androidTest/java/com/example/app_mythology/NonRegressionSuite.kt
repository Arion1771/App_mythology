package com.example.app_mythology

import com.example.app_mythology.browse.BrowseListAndDetailTest
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
 * Suite complète de non-régression, dans l'ordre des menus de l'application :
 * verrouillage portrait, navigation (menus, boutons d'angle), Données
 * (défilement des listes et fiche détaillée, puis recherche et filtre ;
 * Entités, Lieux, Artéfacts), Quizz (QCM, Classique, Lieux, Liste), Duel
 * (Classique, QCM), puis base de données. Chaque groupe correspond à un
 * package et peut aussi être lancé seul ; dans chaque classe, les tests
 * s'exécutent dans l'ordre de leur numéro (t01_, t02_...).
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    OrientationLockTest::class,
    MenuNavigationTest::class,
    TopButtonsTest::class,
    BrowseListAndDetailTest::class,
    BrowseSearchAndFilterTest::class,
    QuizQcmTest::class,
    QuizClassicTest::class,
    QuizPlaceTest::class,
    QuizListTest::class,
    DuelTest::class,
    DatabaseConstraintsTest::class,
)
class NonRegressionSuite
