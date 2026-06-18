package com.kliq.loanapp.data.di

import com.kliq.loanapp.core.model.LoanType
import com.kliq.loanapp.domain.processing.AutoLoanStrategy
import com.kliq.loanapp.domain.processing.BusinessLoanStrategy
import com.kliq.loanapp.domain.processing.LoanProcessingStrategy
import com.kliq.loanapp.domain.processing.MortgageLoanStrategy
import com.kliq.loanapp.domain.processing.PersonalLoanStrategy
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

/**
 * Map key for binding strategies by [LoanType]. Defined HERE, in the data module — never in the
 * pure-JVM domain module — so no Hilt/Dagger annotation leaks into a framework-free layer.
 */
@MapKey
annotation class LoanTypeKey(val value: LoanType)

/**
 * Binds each [LoanProcessingStrategy] into a `Map<LoanType, LoanProcessingStrategy>`, which
 * `LoanProcessor` injects. Adding a loan type = one new strategy class + one line here (OCP).
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class LoanStrategyModule {

    @Binds
    @IntoMap
    @LoanTypeKey(LoanType.PERSONAL)
    abstract fun personal(impl: PersonalLoanStrategy): LoanProcessingStrategy

    @Binds
    @IntoMap
    @LoanTypeKey(LoanType.MORTGAGE)
    abstract fun mortgage(impl: MortgageLoanStrategy): LoanProcessingStrategy

    @Binds
    @IntoMap
    @LoanTypeKey(LoanType.AUTO)
    abstract fun auto(impl: AutoLoanStrategy): LoanProcessingStrategy

    @Binds
    @IntoMap
    @LoanTypeKey(LoanType.BUSINESS)
    abstract fun business(impl: BusinessLoanStrategy): LoanProcessingStrategy
}
