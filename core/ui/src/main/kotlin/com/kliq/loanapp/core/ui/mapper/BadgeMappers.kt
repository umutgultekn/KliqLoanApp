package com.kliq.loanapp.core.ui.mapper

import com.kliq.loanapp.core.common.text.UiText
import com.kliq.loanapp.core.common.ui.Tone
import com.kliq.loanapp.core.designsystem.component.BadgeConfig
import com.kliq.loanapp.core.model.LoanStatus
import com.kliq.loanapp.core.model.LoanType
import com.kliq.loanapp.core.ui.R

/**
 * Each domain value maps to its [BadgeConfig] (label + tone + a11y description), keeping badge rules
 * in one place. Label and description are [UiText] resources, so both localize.
 */
fun LoanStatus.toBadgeConfig(): BadgeConfig {
    val label = UiText.res(
        when (this) {
            LoanStatus.ACTIVE -> R.string.kliq_loan_status_active
            LoanStatus.OVERDUE -> R.string.kliq_loan_status_overdue
            LoanStatus.DEFAULT -> R.string.kliq_loan_status_default
            LoanStatus.PAID -> R.string.kliq_loan_status_paid
        },
    )
    return BadgeConfig(
        label = label,
        tone = when (this) {
            LoanStatus.ACTIVE -> Tone.Active
            LoanStatus.OVERDUE -> Tone.Overdue
            LoanStatus.DEFAULT -> Tone.Default
            LoanStatus.PAID -> Tone.Paid
        },
        contentDescription = UiText.res(R.string.kliq_a11y_loan_status, label),
    )
}

fun LoanType.toBadgeConfig(): BadgeConfig {
    val label = UiText.res(
        when (this) {
            LoanType.PERSONAL -> R.string.kliq_loan_type_personal
            LoanType.MORTGAGE -> R.string.kliq_loan_type_mortgage
            LoanType.AUTO -> R.string.kliq_loan_type_auto
            LoanType.BUSINESS -> R.string.kliq_loan_type_business
        },
    )
    return BadgeConfig(
        label = label,
        tone = when (this) {
            LoanType.PERSONAL -> Tone.TypePersonal
            LoanType.MORTGAGE -> Tone.TypeMortgage
            LoanType.AUTO -> Tone.TypeAuto
            LoanType.BUSINESS -> Tone.TypeBusiness
        },
        contentDescription = UiText.res(R.string.kliq_a11y_loan_type, label),
    )
}
