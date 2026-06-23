package com.kliq.loanapp.data.datasource

import com.kliq.loanapp.data.dto.LoanDto

/**
 * Remote data source for loans — the seam where a real network API would plug in. Returns wire
 * [LoanDto]s; only this implementation changes for a real backend (here it reads the bundled JSON).
 */
interface LoanRemoteDataSource {
    suspend fun fetchLoans(): List<LoanDto>
}
