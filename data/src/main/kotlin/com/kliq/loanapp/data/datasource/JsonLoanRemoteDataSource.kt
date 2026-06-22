package com.kliq.loanapp.data.datasource

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kliq.loanapp.data.dto.LoanDto
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * [LoanRemoteDataSource] implementation that reads the bundled `loans.json` asset (the mandated data
 * source) and returns wire DTOs. Stands in for a network API; DTO→domain mapping and error
 * translation are the repository's job, so this stays a thin parse-only source.
 */
class JsonLoanRemoteDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
) : LoanRemoteDataSource {

    override suspend fun fetchLoans(): List<LoanDto> {
        val json = context.assets.open(ASSET_FILE).bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<LoanDto>>() {}.type
        return gson.fromJson(json, listType) ?: emptyList()
    }

    private companion object {
        const val ASSET_FILE = "loans.json"
    }
}
