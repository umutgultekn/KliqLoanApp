package com.kliq.loanapp.domain.usecase

import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.domain.processing.LoanProcessor
import com.kliq.loanapp.domain.repository.LoanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Streams the loan portfolio with the processing pipeline applied — composes the repository [Flow]
 * with [LoanProcessor] (real orchestration, not a pass-through), re-processing each emitted raw list.
 */
class GetProcessedLoansUseCase @Inject constructor(
    private val repository: LoanRepository,
    private val processor: LoanProcessor,
) {
    operator fun invoke(): Flow<List<Loan>> =
        repository.getLoans().map { processor.process(it) }
}
