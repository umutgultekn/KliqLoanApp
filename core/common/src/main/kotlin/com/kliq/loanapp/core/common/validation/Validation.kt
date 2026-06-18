package com.kliq.loanapp.core.common.validation

import com.kliq.loanapp.core.common.text.UiText

/** Result of validating a single field value. */
sealed interface ValidationResult {
    data object Success : ValidationResult
    data class Failure(val message: UiText) : ValidationResult

    val isValid: Boolean get() = this is Success
}

/** Strategy for validating a field value. Composable and reusable across screens. */
fun interface ValidationRule {
    fun validate(input: String): ValidationResult
}

/** Field must be non-blank. */
class RequiredRule(private val error: UiText) : ValidationRule {
    override fun validate(input: String): ValidationResult =
        if (input.isNotBlank()) ValidationResult.Success else ValidationResult.Failure(error)
}

/**
 * Lenient email check (non-blank and contains '@'). Deliberately reproduces the starter's inline
 * check so the migration is behavior-preserving; production would swap in a stricter rule — a
 * one-line change because validation is strategy-based.
 */
class EmailRule(private val error: UiText) : ValidationRule {
    override fun validate(input: String): ValidationResult =
        if (input.isNotBlank() && input.contains('@')) ValidationResult.Success
        else ValidationResult.Failure(error)
}

/** Minimum-length rule (defaults reproduce the starter's >= 6 password check). */
class MinLengthRule(private val min: Int, private val error: UiText) : ValidationRule {
    override fun validate(input: String): ValidationResult =
        if (input.length >= min) ValidationResult.Success else ValidationResult.Failure(error)
}

/** Runs the contained rules in order; the first failure wins. */
class CompositeRule(private val rules: List<ValidationRule>) : ValidationRule {
    constructor(vararg rules: ValidationRule) : this(rules.toList())

    override fun validate(input: String): ValidationResult =
        rules.firstNotNullOfOrNull { it.validate(input) as? ValidationResult.Failure }
            ?: ValidationResult.Success
}
