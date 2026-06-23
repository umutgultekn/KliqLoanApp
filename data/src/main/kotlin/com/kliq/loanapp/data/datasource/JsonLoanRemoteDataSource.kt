package com.kliq.loanapp.data.datasource

import android.content.Context
import com.kliq.loanapp.core.common.dispatcher.DispatcherProvider
import com.kliq.loanapp.data.dto.LoanDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

/** Reads the bundled `loans.json` asset and returns wire DTOs — a thin, parse-only [LoanRemoteDataSource]. */
class JsonLoanRemoteDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val json: Json,
    private val dispatchers: DispatcherProvider,
) : LoanRemoteDataSource {

    // Self-contained: pins blocking asset I/O to the IO dispatcher rather than trusting the caller's context.
    override suspend fun fetchLoans(): List<LoanDto> = withContext(dispatchers.io) {
        val text = context.assets.open(ASSET_FILE).bufferedReader().use { it.readText() }
        json.decodeFromString<List<LoanDto>>(text)
    }

    private companion object {
        const val ASSET_FILE = "loans.json"
    }
}
