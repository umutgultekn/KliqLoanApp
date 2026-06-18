package com.kliq.loanapp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
data class Loan(
    val name: String,
    var principal_amount: Double,
    var interest_rate: Double,
    var status: String,
    var due_in: Int,
    val type: String
)
interface LoanService {
    fun fetchLoans(): List<Loan>
    fun persistLoans(loans: List<Loan>)
}
class MockLoanService(private val context: Context) : LoanService {
    override fun fetchLoans(): List<Loan> {
        val json = context.assets.open("loans.json")
            .bufferedReader()
            .use { it.readText() }
        val type = object : TypeToken<List<Loan>>() {}.type
        return Gson().fromJson(json, type)
    }

    override fun persistLoans(loans: List<Loan>) {
        println("Persisted ${loans.size} loans")
    }
}
class LoanRepository(private val service: LoanService) {

    fun processAndUpdateLoans(): List<Loan> {
        val loans = service.fetchLoans().toMutableList()

        for (i in loans.indices) {
            if (loans[i].type == "personal") {
                if (loans[i].status == "active") {
                    if (loans[i].due_in > 0) {
                        loans[i].interest_rate += 0.3
                    } else {
                        if (loans[i].principal_amount > 10000) {
                            loans[i].interest_rate += 1.2
                            loans[i].status = "overdue"
                        } else {
                            loans[i].interest_rate += 0.6
                        }
                    }
                } else if (loans[i].status == "overdue") {
                    loans[i].interest_rate += 1.5
                    if (loans[i].principal_amount > 20000) {
                        loans[i].status = "default"
                    }
                }
            } else if (loans[i].type == "mortgage") {
                if (loans[i].status == "active") {
                    if (loans[i].due_in > 0) {
                        loans[i].interest_rate += 0.1
                    } else {
                        loans[i].interest_rate += 0.4
                        loans[i].status = "overdue"
                    }
                } else if (loans[i].status == "overdue") {
                    loans[i].interest_rate += 0.8
                    if (loans[i].due_in < -60) {
                        loans[i].status = "default"
                    }
                }
            } else if (loans[i].type == "auto") {
                if (loans[i].status == "active") {
                    if (loans[i].due_in > 0) {
                        loans[i].interest_rate += 0.4
                    } else {
                        loans[i].interest_rate += 0.9
                        loans[i].status = "overdue"
                    }
                } else if (loans[i].status == "overdue") {
                    loans[i].interest_rate += 1.8
                    if (loans[i].principal_amount > 50000) {
                        loans[i].status = "default"
                    }
                }
            } else if (loans[i].type == "business") {
                if (loans[i].status == "active") {
                    if (loans[i].due_in > 0) {
                        loans[i].interest_rate += 0.5
                    } else {
                        loans[i].interest_rate += 1.0
                        loans[i].status = "overdue"
                    }
                } else if (loans[i].status == "overdue") {
                    loans[i].interest_rate += 2.0
                    if (loans[i].principal_amount > 100000) {
                        loans[i].status = "default"
                    }
                }
            }

            loans[i].due_in -= 1

            if (loans[i].due_in < -90) {
                if (loans[i].status != "paid") {
                    loans[i].status = "default"
                }
            }

            if (loans[i].principal_amount <= 0) {
                loans[i].status = "paid"
            }
        }

        service.persistLoans(loans)
        return loans
    }
}
