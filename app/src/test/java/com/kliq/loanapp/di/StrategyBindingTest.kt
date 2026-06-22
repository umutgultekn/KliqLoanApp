package com.kliq.loanapp.di

import com.kliq.loanapp.core.model.LoanType
import com.kliq.loanapp.domain.processing.LoanProcessingStrategy
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

/**
 * Integration test for the @IntoMap strategy multibinding: proves the REAL Hilt graph (not a
 * hand-built map) resolves a strategy for every [LoanType], correctly keyed. The ViewModel/processor
 * unit tests assume a correct map; this is the one test that verifies Hilt actually assembles it.
 */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class StrategyBindingTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var strategies: Map<LoanType, @JvmSuppressWildcards LoanProcessingStrategy>

    @Before
    fun inject() = hiltRule.inject()

    @Test
    fun `every loan type has exactly one correctly-keyed strategy`() {
        assertEquals(LoanType.entries.toSet(), strategies.keys)
        strategies.forEach { (key, strategy) -> assertEquals(key, strategy.type) }
    }
}
