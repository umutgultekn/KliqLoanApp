package com.kliq.loanapp.data.repository

import com.kliq.loanapp.core.common.dispatcher.DispatcherProvider
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
import kotlinx.serialization.SerializationException
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject

/**
 * Exposes domain loans as a reactive [Flow]: maps wire DTOs to the domain model (dropping malformed
 * records) and translates data-source failures to the typed [AppError] taxonomy, off the main thread.
 */
class LoanRepositoryImpl @Inject constructor(
    private val remoteDataSource: LoanRemoteDataSource,
    private val dispatchers: DispatcherProvider,
) : LoanRepository {

    override fun getLoans(): Flow<List<Loan>> = flow {
        val dtos = remoteDataSource.fetchLoans()
        emit(dtos.mapNotNull { it.toDomainOrNull() })
    }.catch { cause ->
        if (cause is CancellationException) throw cause
        throw cause.toLoanLoadError()
    }.flowOn(dispatchers.io)

    // Library-specific failures are mapped to the AppError taxonomy here, at the data boundary, so
    // they never reach the presentation layer.
    private fun Throwable.toLoanLoadError(): AppError = when (this) {
        is AppError -> this
        is FileNotFoundException -> AppError.AssetMissing
        is SerializationException -> AppError.ParseFailure
        is IOException -> AppError.Io
        else -> toAppError()
    }
}
