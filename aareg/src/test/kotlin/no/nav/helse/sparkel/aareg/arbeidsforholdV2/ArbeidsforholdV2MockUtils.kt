package no.nav.helse.sparkel.aareg.arbeidsforholdV2

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.mockk.every
import io.mockk.mockk
import org.intellij.lang.annotations.Language


val mockGenerator = mockk<ResponseGenerator>(relaxed = true).apply {
    every { arbeidsforholdV2() }.returns(arbeidsforholdV2Response())
}

fun aregMockClient(mockResponseGenerator: ResponseGenerator) = HttpClient(MockEngine) {
    engine {
        addHandler { request ->
            when {
                request.url.fullPath.startsWith("/v1/arbeidstaker/arbeidsforhold") -> {
                    respond(mockResponseGenerator.arbeidsforholdV2())
                }
                else -> error("Endepunktet finnes ikke ${request.url.fullPath}")
            }
        }
    }
}

interface ResponseGenerator {
    fun arbeidsforholdV2() = arbeidsforholdV2Response()
}

@Language("JSON")
fun arbeidsforholdV2Response() = """
    [
  {
    "ansettelsesperiode": {
      "bruksperiode": {
        "fom": "2015-01-06T21:44:04.748",
        "tom": "2015-12-06T19:45:04"
      },
      "periode": {
        "fom": "2014-07-01",
        "tom": "2015-12-31"
      },
      "sporingsinformasjon": {
        "endretAv": "Z990693",
        "endretKilde": "AAREG",
        "endretKildereferanse": "referanse-fra-kilde",
        "endretTidspunkt": "2018-09-19T12:11:20.79",
        "opprettetAv": "srvappserver",
        "opprettetKilde": "EDAG",
        "opprettetKildereferanse": "22a26849-aeef-4b81-9174-e238c11e1081",
        "opprettetTidspunkt": "2018-09-19T12:10:58.059"
      },
      "varslingskode": "ERKONK"
    },
    "antallTimerForTimeloennet": [
      {
        "antallTimer": 37.5,
        "periode": {
          "fom": "2014-07-01",
          "tom": "2015-12-31"
        },
        "rapporteringsperiode": "2018-05",
        "sporingsinformasjon": {
          "endretAv": "Z990693",
          "endretKilde": "AAREG",
          "endretKildereferanse": "referanse-fra-kilde",
          "endretTidspunkt": "2018-09-19T12:11:20.79",
          "opprettetAv": "srvappserver",
          "opprettetKilde": "EDAG",
          "opprettetKildereferanse": "22a26849-aeef-4b81-9174-e238c11e1081",
          "opprettetTidspunkt": "2018-09-19T12:10:58.059"
        }
      }
    ],
    "arbeidsavtaler": [
      {
        "antallTimerPrUke": 37.5,
        "arbeidstidsordning": "ikkeSkift",
        "beregnetAntallTimerPrUke": 37.5,
        "bruksperiode": {
          "fom": "2015-01-06T21:44:04.748",
          "tom": "2015-12-06T19:45:04"
        },
        "gyldighetsperiode": {
          "fom": "2014-07-01",
          "tom": "2015-12-31"
        },
        "sistLoennsendring": "string",
        "sistStillingsendring": "string",
        "sporingsinformasjon": {
          "endretAv": "Z990693",
          "endretKilde": "AAREG",
          "endretKildereferanse": "referanse-fra-kilde",
          "endretTidspunkt": "2018-09-19T12:11:20.79",
          "opprettetAv": "srvappserver",
          "opprettetKilde": "EDAG",
          "opprettetKildereferanse": "22a26849-aeef-4b81-9174-e238c11e1081",
          "opprettetTidspunkt": "2018-09-19T12:10:58.059"
        },
        "stillingsprosent": 49.5,
        "yrke": 2130123
      }
    ],
    "arbeidsforholdId": "abc-321",
    "arbeidsgiver": {
      "type": "Organisasjon",
      "organisasjonsnummer": "organisasjonsnummer"
    },
    "arbeidstaker": {
      "type": "Person",
      "aktoerId": 1234567890,
      "offentligIdent": 31126700000
    },
    "innrapportertEtterAOrdningen": true,
    "navArbeidsforholdId": 123456,
    "opplysningspliktig": {
      "type": "Organisasjon",
      "organisasjonsnummer": "organisasjonsnummer"
    },
    "permisjonPermitteringer": [
      {
        "periode": {
          "fom": "2014-07-01",
          "tom": "2015-12-31"
        },
        "permisjonPermitteringId": "123-xyz",
        "prosent": 50.5,
        "sporingsinformasjon": {
          "endretAv": "Z990693",
          "endretKilde": "AAREG",
          "endretKildereferanse": "referanse-fra-kilde",
          "endretTidspunkt": "2018-09-19T12:11:20.79",
          "opprettetAv": "srvappserver",
          "opprettetKilde": "EDAG",
          "opprettetKildereferanse": "22a26849-aeef-4b81-9174-e238c11e1081",
          "opprettetTidspunkt": "2018-09-19T12:10:58.059"
        },
        "type": "permisjonMedForeldrepenger",
        "varslingskode": "string"
      }
    ],
    "registrert": "2018-09-18T11:12:29",
    "sistBekreftet": "2018-09-19T12:10:31",
    "sporingsinformasjon": {
      "endretAv": "Z990693",
      "endretKilde": "AAREG",
      "endretKildereferanse": "referanse-fra-kilde",
      "endretTidspunkt": "2018-09-19T12:11:20.79",
      "opprettetAv": "srvappserver",
      "opprettetKilde": "EDAG",
      "opprettetKildereferanse": "22a26849-aeef-4b81-9174-e238c11e1081",
      "opprettetTidspunkt": "2018-09-19T12:10:58.059"
    },
    "type": "ordinaertArbeidsforhold",
    "utenlandsopphold": [
      {
        "landkode": "JPN",
        "periode": {
          "fom": "2014-07-01",
          "tom": "2015-12-31"
        },
        "rapporteringsperiode": "2017-12",
        "sporingsinformasjon": {
          "endretAv": "Z990693",
          "endretKilde": "AAREG",
          "endretKildereferanse": "referanse-fra-kilde",
          "endretTidspunkt": "2018-09-19T12:11:20.79",
          "opprettetAv": "srvappserver",
          "opprettetKilde": "EDAG",
          "opprettetKildereferanse": "22a26849-aeef-4b81-9174-e238c11e1081",
          "opprettetTidspunkt": "2018-09-19T12:10:58.059"
        }
      }
    ]
  }
]
"""
