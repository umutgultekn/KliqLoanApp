package com.kliq.loanapp.core.common.ui

/**
 * Semantic color intent. The source of truth lives here (pure module) so both presentation and the
 * design system can speak in semantics; the design system maps each [Tone] to a concrete color.
 */
enum class Tone { Neutral, Primary, Active, Overdue, Default, Paid }
