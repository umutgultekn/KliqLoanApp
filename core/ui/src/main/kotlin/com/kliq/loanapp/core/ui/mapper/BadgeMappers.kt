package com.kliq.loanapp.core.ui.mapper

import com.kliq.loanapp.core.common.ui.Tone
import com.kliq.loanapp.core.designsystem.component.BadgeConfig
import com.kliq.loanapp.core.model.LoanStatus
import com.kliq.loanapp.core.model.LoanType

/**
 * Each domain value pre-configures its own [BadgeConfig] (label + semantic tone + a11y description).
 * Keeps badge presentation rules in one place instead of being hand-built at each call site.
 */
fun LoanStatus.toBadgeConfig(): BadgeConfig = BadgeConfig(
    label = name,
    tone = when (this) {
        LoanStatus.ACTIVE -> Tone.Active
        LoanStatus.OVERDUE -> Tone.Overdue
        LoanStatus.DEFAULT -> Tone.Default
        LoanStatus.PAID -> Tone.Paid
    },
    contentDescription = "Status: ${name.lowercase()}",
)

fun LoanType.toBadgeConfig(): BadgeConfig = BadgeConfig(
    label = name,
    tone = when (this) {
        LoanType.PERSONAL -> Tone.TypePersonal
        LoanType.MORTGAGE -> Tone.TypeMortgage
        LoanType.AUTO -> Tone.TypeAuto
        LoanType.BUSINESS -> Tone.TypeBusiness
    },
    contentDescription = "Loan type: ${name.lowercase()}",
)
