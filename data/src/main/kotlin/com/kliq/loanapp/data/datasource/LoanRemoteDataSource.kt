package com.kliq.loanapp.data.datasource

import com.kliq.loanapp.data.dto.LoanDto

/**
 * Remote data source for loans — the seam where a real network API plugs in (e.g. a Retrofit
 * `LoanApi`). Returns wire [LoanDto]s; the repository maps them to the domain model and owns the
 * single-source-of-truth / error policy. The production implementation would call an API; here
 * [JsonLoanRemoteDataSource] reads the bundled `loans.json`, so the rest of the data layer is already
 * shaped for a real backend — only this implementation would change.
 */
interface LoanRemoteDataSource {
    suspend fun fetchLoans(): List<LoanDto>
}
