package com.kliq.loanapp.core.common.validation

import com.kliq.loanapp.core.common.text.UiText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationRuleTest {

    private val err = UiText.of("error")

    @Test
    fun `email rule rejects blank and missing at-sign`() {
        val rule = EmailRule(err)
        assertFalse(rule.validate("").isValid)
        assertFalse(rule.validate("abc").isValid)
        assertTrue(rule.validate("a@b.com").isValid)
    }

    @Test
    fun `min length rule enforces boundary`() {
        val rule = MinLengthRule(min = 6, error = err)
        assertFalse(rule.validate("12345").isValid)
        assertTrue(rule.validate("123456").isValid)
    }

    @Test
    fun `required rule rejects whitespace only`() {
        val rule = RequiredRule(err)
        assertFalse(rule.validate("   ").isValid)
        assertTrue(rule.validate("x").isValid)
    }

    @Test
    fun `composite rule returns first failure`() {
        val first = UiText.of("required")
        val second = UiText.of("email")
        val rule = CompositeRule(RequiredRule(first), EmailRule(second))

        val failure = rule.validate("") as ValidationResult.Failure
        assertEquals(first, failure.message)
        assertTrue(rule.validate("a@b.com").isValid)
    }
}
