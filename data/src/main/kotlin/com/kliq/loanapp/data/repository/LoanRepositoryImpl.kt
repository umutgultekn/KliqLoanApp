package com.kliq.loanapp.data.repository

import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.kliq.loanapp.core.common.dispatcher.DispatcherProvider
import com.kliq.loanapp.core.common.result.AppError
import com.kliq.loanapp.core.common.result.toAppError
import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.domain.repository.LoanRepository
import com.kliq.loanapp.domain.service.LoanService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject

/** Provides raw domain loans, mapping data-source failures to the [AppError] taxonomy. */
class LoanRepositoryImpl @Inject constructor(
    private val service: LoanService,
    private val dispatchers: DispatcherProvider,
) : LoanRepository {

    override suspend fun getLoans(): Result<List<Loan>> = withContext(dispatchers.io) {
        try {
            Result.success(service.fetchLoans())
        } catch (e: CancellationException) {
            throw e
        } catch (e: FileNotFoundException) {
            Result.failure(AppError.AssetMissing)
        } catch (e: JsonSyntaxException) {
            Result.failure(AppError.ParseFailure)
        } catch (e: JsonParseException) {
            Result.failure(AppError.ParseFailure)
        } catch (e: IOException) {
            Result.failure(AppError.Io)
        } catch (e: Throwable) {
            Result.failure(e.toAppError())
        }
    }
}
