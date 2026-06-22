package com.kliq.loanapp.data.repository

import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.kliq.loanapp.core.common.dispatcher.DispatcherProvider
import com.kliq.loanapp.core.common.log.Logger
import com.kliq.loanapp.core.common.result.AppError
import com.kliq.loanapp.core.common.result.toAppError
import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.data.datasource.LoanRemoteDataSource
import com.kliq.loanapp.data.mapper.toDomainOrNull
import com.kliq.loanapp.domain.repository.LoanRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject

/**
 * Coordinates the loan data source(s) and exposes domain loans as a reactive [Flow] — the production
 * data-layer shape. Maps wire DTOs to the domain model (dropping malformed records, logged once) and
 * translates data-source failures to the typed [AppError] taxonomy, so library-specific exceptions
 * (e.g. Gson) never leak past the data layer. All work runs on the IO dispatcher.
 */
class LoanRepositoryImpl @Inject constructor(
    private val remoteDataSource: LoanRemoteDataSource,
    private val dispatchers: DispatcherProvider,
    private val logger: Logger,
) : LoanRepository {

    override fun getLoans(): Flow<List<Loan>> = flow {
        val dtos = remoteDataSource.fetchLoans()
        val loans = dtos.mapNotNull { it.toDomainOrNull() }
        val dropped = dtos.size - loans.size
        if (dropped > 0) logger.warn("Dropped $dropped malformed loan record(s)")
        emit(loans)
    }.catch { cause ->
        if (cause is CancellationException) throw cause
        val error = cause.toLoanLoadError()
        logger.error("Loading loans failed", error)
        throw error
    }.flowOn(dispatchers.io)

    // Library-specific failures are mapped to the AppError taxonomy here, at the data boundary, so
    // they never reach the presentation layer.
    private fun Throwable.toLoanLoadError(): AppError = when (this) {
        is AppError -> this
        is FileNotFoundException -> AppError.AssetMissing
        is JsonSyntaxException, is JsonParseException -> AppError.ParseFailure
        is IOException -> AppError.Io
        else -> toAppError()
    }
}
