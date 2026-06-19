package com.kliq.loanapp.core.testing

import com.kliq.loanapp.core.common.log.Logger
import com.kliq.loanapp.core.common.navigation.NavCommand
import com.kliq.loanapp.core.common.navigation.Navigator
import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.core.model.LoanType
import com.kliq.loanapp.domain.processing.AutoLoanStrategy
import com.kliq.loanapp.domain.processing.BusinessLoanStrategy
import com.kliq.loanapp.domain.processing.LoanProcessor
import com.kliq.loanapp.domain.processing.MortgageLoanStrategy
import com.kliq.loanapp.domain.processing.PersonalLoanStrategy
import com.kliq.loanapp.domain.repository.AuthRepository
import com.kliq.loanapp.domain.repository.LoanRepository
import com.kliq.loanapp.domain.repository.SessionRepository
import com.kliq.loanapp.domain.service.LoanService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/** Configurable fake data source: returns [loans], or throws [error] if set. */
class FakeLoanService(
    private val loans: List<Loan> = emptyList(),
    private val error: Throwable? = null,
) : LoanService {
    override suspend fun fetchLoans(): List<Loan> {
        error?.let { throw it }
        return loans
    }
}

/** In-memory session for asserting login/logout transitions. */
class FakeSessionRepository(initial: Boolean = false) : SessionRepository {
    private val state = MutableStateFlow(initial)
    override val isLoggedIn: Flow<Boolean> = state.asStateFlow()
    override suspend fun setLoggedIn(value: Boolean) { state.value = value }
    val current: Boolean get() = state.value
}

/** Records navigation commands for assertions. */
class FakeNavigator : Navigator {
    private val _commands = MutableSharedFlow<NavCommand>(extraBufferCapacity = 16)
    override val commands: Flow<NavCommand> = _commands.asSharedFlow()
    val received = mutableListOf<NavCommand>()
    override suspend fun navigate(command: NavCommand) {
        received += command
        _commands.emit(command)
    }
    val last: NavCommand? get() = received.lastOrNull()
}

/** Configurable auth that opens the given [session] on a successful [result]. */
class FakeAuthRepository(
    var result: Result<Unit> = Result.success(Unit),
    val session: FakeSessionRepository = FakeSessionRepository(),
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<Unit> {
        if (result.isSuccess) session.setLoggedIn(true)
        return result
    }
    override suspend fun logout() = session.setLoggedIn(false)
}

/** Returns a fixed [result] (or a success list) for repository tests. */
class FakeLoanRepository(
    var result: Result<List<Loan>> = Result.success(emptyList()),
) : LoanRepository {
    override suspend fun getLoans(): Result<List<Loan>> = result
}

/** Captures log calls so tests can assert the observability seam fired (or just stay silent). */
class RecordingLogger : Logger {
    val warnings = mutableListOf<String>()
    val errors = mutableListOf<String>()
    override fun warn(message: String, throwable: Throwable?) { warnings += message }
    override fun error(message: String, throwable: Throwable?) { errors += message }
}

/** A fully-wired [LoanProcessor] with all four strategies, for use-case/ViewModel tests. */
fun testLoanProcessor(): LoanProcessor = LoanProcessor(
    mapOf(
        LoanType.PERSONAL to PersonalLoanStrategy(),
        LoanType.MORTGAGE to MortgageLoanStrategy(),
        LoanType.AUTO to AutoLoanStrategy(),
        LoanType.BUSINESS to BusinessLoanStrategy(),
    ),
)
