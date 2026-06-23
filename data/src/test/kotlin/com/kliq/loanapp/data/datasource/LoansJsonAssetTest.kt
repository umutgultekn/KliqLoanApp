package com.kliq.loanapp.data.datasource

import com.kliq.loanapp.data.dto.LoanDto
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Guards the bundled `loans.json` against silent drift: a renamed `@SerialName`, malformed record, or
 * wrong count would otherwise pass every fake-remote test and only surface as a runtime crash. Parses
 * the REAL shipped asset with the same [Json] config the data source uses.
 */
class LoansJsonAssetTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `bundled loans json parses to 29 well-formed records`() {
        val text = File("src/main/assets/loans.json").readText()

        val loans = json.decodeFromString<List<LoanDto>>(text)

        assertEquals(29, loans.size)
        // Every record carries the fields the domain mapper requires; a broken @SerialName would null these.
        assertTrue(loans.all { !it.name.isNullOrBlank() && !it.type.isNullOrBlank() })
    }
}
