package no.nav.helse.sparkel.personinfo

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.skyscreamer.jsonassert.JSONAssert

internal class HentPersoninfoV3Test {
    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `response fra pdl inneholder alle etterspurte attributter`() {
        val løsning = HentPersoninfoV3.oversett(komplettSvar, setOf("aktørId", "folkeregisterident", "fødselsdato"))
        @Language("JSON")
        val forventet = """
            {
              "aktørId": "1234567890123",
              "folkeregisterident": "12345678901",
              "fødselsdato": "1980-04-09"
            }
        """
        JSONAssert.assertEquals(forventet, "$løsning", true)
    }

    @Test
    fun `response fra pdl som mangler fødselsdato`() {
        assertThrows<IllegalStateException> { HentPersoninfoV3.oversett(manglerFødselsdato, setOf("fødselsdato")) }
        val løsning = HentPersoninfoV3.oversett(manglerFødselsdato, setOf("aktørId", "folkeregisterident"))
        @Language("JSON")
        val forventet = """
            {
              "aktørId": "1234567890123",
              "folkeregisterident": "12345678901"
            }
        """
        JSONAssert.assertEquals(forventet, "$løsning", true)
    }

    @Test
    fun `response fra pdl som mangler aktørId`() {
        assertThrows<IllegalStateException> { HentPersoninfoV3.oversett(manglerAktørId, setOf("aktørId")) }
        val løsning = HentPersoninfoV3.oversett(manglerAktørId, setOf("fødselsdato", "folkeregisterident"))
        @Language("JSON")
        val forventet = """
            {
              "folkeregisterident": "12345678901",
              "fødselsdato": "1980-04-09"
            }
        """
        JSONAssert.assertEquals(forventet, "$løsning", true)
    }

    @Test
    fun `response fra pdl som mangler folkeregisterident`() {
        assertThrows<IllegalStateException> { HentPersoninfoV3.oversett(manglerFolkeregisterident, setOf("folkeregisterident")) }
        val løsning = HentPersoninfoV3.oversett(manglerFolkeregisterident, setOf("fødselsdato", "aktørId"))
        @Language("JSON")
        val forventet = """
            {
              "aktørId": "1234567890123",
              "fødselsdato": "1980-04-09"
            }
        """
        JSONAssert.assertEquals(forventet, "$løsning", true)
    }

    @Test
    fun `error response fra pdl`() {
        assertThrows<RuntimeException> { HentPersoninfoV3.oversett(error, setOf("folkeregisterident", "aktørId", "fødsesldato")) }
    }

    @Language("JSON")
    private val komplettSvar = """
    {
      "data": {
        "hentIdenter": {
          "identer": [
            {
              "ident": "12345678901",
              "gruppe": "FOLKEREGISTERIDENT"
            },
            {
              "ident": "1234567890123",
              "gruppe": "AKTORID"
            }
          ]
        },
        "hentPerson": {
          "foedsel": [
            {
              "foedselsdato": "1980-04-09"
            }
          ]
        }
      }
    }
    """.let { objectMapper.readTree(it) }

    @Language("JSON")
    private val manglerFødselsdato = """
    {
      "data": {
        "hentIdenter": {
          "identer": [
            {
              "ident": "12345678901",
              "gruppe": "FOLKEREGISTERIDENT"
            },
            {
              "ident": "1234567890123",
              "gruppe": "AKTORID"
            }
          ]
        }
      }
    }
    """.let { objectMapper.readTree(it) }

    @Language("JSON")
    private val manglerAktørId = """
    {
      "data": {
        "hentIdenter": {
          "identer": [
            {
              "ident": "12345678901",
              "gruppe": "FOLKEREGISTERIDENT"
            }
          ]
        },
        "hentPerson": {
          "foedsel": [
            {
              "foedselsdato": "1980-04-09"
            }
          ]
        }
      }
    }
    """.let { objectMapper.readTree(it) }

    @Language("JSON")
    private val manglerFolkeregisterident = """
    {
      "data": {
        "hentIdenter": {
          "identer": [
            {
              "ident": "1234567890123",
              "gruppe": "AKTORID"
            }
          ]
        },
        "hentPerson": {
          "foedsel": [
            {
              "foedselsdato": "1980-04-09"
            }
          ]
        }
      }
    }
    """.let { objectMapper.readTree(it) }

    @Language("JSON")
    private val error = """
    {
      "errors": ["noe er gæli"]
    }
    """.let { objectMapper.readTree(it) }
}