package com.kliq.loanapp.domain.usecase

import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.domain.processing.LoanProcessor
import com.kliq.loanapp.domain.repository.LoanRepository
import javax.inject.Inject

/**
 * Fetches raw loans and applies the processing pipeline. Composes a repository with the
 * [LoanProcessor] domain service — meaningful orchestration, not a pass-through.
 *
 * Processing always runs on the freshly-fetched raw list, never on already-processed output, so the
 * (deliberately non-idempotent) pipeline yields a stable result across re-invocations.
 */
class GetProcessedPortfolioUseCase @Inject constructor(
    private val repository: LoanRepository,
    private val processor: LoanProcessor,
) {
    suspend operator fun invoke(): Result<List<Loan>> =
        repository.getLoans().map { processor.process(it) }
}
