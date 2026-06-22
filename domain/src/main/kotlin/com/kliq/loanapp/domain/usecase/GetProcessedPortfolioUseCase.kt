package com.kliq.loanapp.domain.usecase

import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.domain.processing.LoanProcessor
import com.kliq.loanapp.domain.repository.LoanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Streams the loan portfolio with the processing pipeline applied. Composes a repository [Flow] with
 * the [LoanProcessor] domain service — meaningful orchestration, not a pass-through. Processing runs
 * on each freshly-emitted raw list, so the (deliberately non-idempotent) pipeline yields a stable
 * result per emission.
 */
class GetProcessedPortfolioUseCase @Inject constructor(
    private val repository: LoanRepository,
    private val processor: LoanProcessor,
) {
    operator fun invoke(): Flow<List<Loan>> =
        repository.getLoans().map { processor.process(it) }
}
