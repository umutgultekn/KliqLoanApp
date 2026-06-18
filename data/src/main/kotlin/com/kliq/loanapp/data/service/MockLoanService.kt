package com.kliq.loanapp.data.service

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.data.dto.LoanDto
import com.kliq.loanapp.data.mapper.toDomainOrNull
import com.kliq.loanapp.domain.service.LoanService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/** Reads loans from the bundled `loans.json` asset (the mandated data source) and maps to domain. */
class MockLoanService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
) : LoanService {

    override suspend fun fetchLoans(): List<Loan> {
        val json = context.assets.open(ASSET_FILE).bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<LoanDto>>() {}.type
        val dtos: List<LoanDto> = gson.fromJson(json, listType) ?: emptyList()

        val loans = dtos.mapNotNull { it.toDomainOrNull() }
        val dropped = dtos.size - loans.size
        if (dropped > 0) Log.w(TAG, "Dropped $dropped malformed loan record(s)")
        return loans
    }

    private companion object {
        const val ASSET_FILE = "loans.json"
        const val TAG = "MockLoanService"
    }
}
