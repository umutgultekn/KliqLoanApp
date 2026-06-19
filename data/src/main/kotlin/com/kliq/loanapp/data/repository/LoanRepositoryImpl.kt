package com.kliq.loanapp.data.repository

import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.kliq.loanapp.core.common.dispatcher.DispatcherProvider
import com.kliq.loanapp.core.common.log.Logger
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
    private val logger: Logger,
) : LoanRepository {

    // Each known data-source failure is deliberately translated to a typed AppError — the original is
    // not propagated because the AppError taxonomy is the contract the rest of the app consumes. The
    // cause is logged here (the boundary) so the swallowed detail stays observable.
    @Suppress("SwallowedException", "TooGenericExceptionCaught")
    override suspend fun getLoans(): Result<List<Loan>> = withContext(dispatchers.io) {
        try {
            Result.success(service.fetchLoans())
        } catch (e: CancellationException) {
            throw e
        } catch (e: FileNotFoundException) {
            logger.warn("loans.json asset missing", e)
            Result.failure(AppError.AssetMissing)
        } catch (e: JsonSyntaxException) {
            logger.warn("loans.json is malformed", e)
            Result.failure(AppError.ParseFailure)
        } catch (e: JsonParseException) {
            logger.warn("loans.json is malformed", e)
            Result.failure(AppError.ParseFailure)
        } catch (e: IOException) {
            logger.warn("loans.json read failed", e)
            Result.failure(AppError.Io)
        } catch (e: Throwable) {
            logger.error("Unexpected failure loading loans", e)
            Result.failure(e.toAppError())
        }
    }
}
