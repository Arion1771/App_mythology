package com.example.app_mythology

import com.example.app_mythology.achievements.AchievementEmblemAssetsTest
import com.example.app_mythology.nonregression.PrepopulateDataRegressionTest
import com.example.app_mythology.quiz.AnswerNormalizationTest
import com.example.app_mythology.quiz.ListThemeCatalogTest
import com.example.app_mythology.quiz.ListThemeCompletenessTest
import com.example.app_mythology.quiz.QcmDecoysTest
import com.example.app_mythology.ui.PrimaryButtonPlacementTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Suite complète des tests unitaires (JVM, sans appareil), dans un ordre
 * cohérent avec l'application : données de prepopulate.json, accueil (boutons
 * principaux, blasons des succès), puis Quizz dans l'ordre du menu (leurres
 * du QCM, comparaison des réponses du Classique, thèmes du mode Liste). Dans
 * chaque classe, les tests s'exécutent dans l'ordre de leur numéro (t01...).
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    PrepopulateDataRegressionTest::class,
    PrimaryButtonPlacementTest::class,
    AchievementEmblemAssetsTest::class,
    QcmDecoysTest::class,
    AnswerNormalizationTest::class,
    ListThemeCatalogTest::class,
    ListThemeCompletenessTest::class,
)
class UnitTestSuite
