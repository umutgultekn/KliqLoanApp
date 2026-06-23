package com.kliq.loanapp.core.testing

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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

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

/** Emits [loans] (or throws [error]) as a reactive stream, for repository/use-case/VM tests. */
class FakeLoanRepository(
    var loans: List<Loan> = emptyList(),
    var error: Throwable? = null,
) : LoanRepository {
    override fun getLoans(): Flow<List<Loan>> = flow {
        error?.let { throw it }
        emit(loans)
    }
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
