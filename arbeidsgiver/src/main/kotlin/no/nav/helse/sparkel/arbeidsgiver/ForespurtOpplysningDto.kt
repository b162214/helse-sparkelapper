package no.nav.helse.sparkel.arbeidsgiver

import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDate
import java.time.YearMonth
import no.nav.helse.rapids_rivers.asLocalDate
import no.nav.helse.rapids_rivers.asOptionalLocalDate
import no.nav.helse.rapids_rivers.asYearMonth
import org.slf4j.LoggerFactory

private val sikkerlogg = LoggerFactory.getLogger("tjenestekall")

internal sealed class ForespurtOpplysning() {

    companion object {
        fun List<ForespurtOpplysning>.toJsonMap() = map { forespurtOpplysning ->
            when (forespurtOpplysning) {
                is Arbeidsgiverperiode -> mapOf(
                    "opplysningstype" to "Arbeidsgiverperiode",
                    "forslag" to forespurtOpplysning.forslag.map { forslag ->
                        mapOf(
                            "fom" to forslag["fom"],
                            "tom" to forslag["tom"]
                        )
                    }
                )

                is FastsattInntekt -> mapOf(
                    "opplysningstype" to "FastsattInntekt",
                    "fastsattInntekt" to forespurtOpplysning.fastsattInntekt
                )

                is Inntekt -> mapOf(
                    "opplysningstype" to "Inntekt",
                    "forslag" to mapOf("beregningsmåneder" to forespurtOpplysning.forslag.beregningsmåneder)
                )

                is Refusjon -> mapOf(
                    "opplysningstype" to "Refusjon",
                    "forslag" to forespurtOpplysning.forslag.map {
                        mapOf(
                            "fom" to it.fom,
                            "tom" to it.tom,
                            "beløp" to it.beløp
                        )
                    }
                )
            }
        }
    }
}

internal data class Refusjonsforslag(val fom: LocalDate, val tom: LocalDate?, val beløp: Double)
internal data class Refusjon(val forslag: List<Refusjonsforslag>) : ForespurtOpplysning()
internal data class Arbeidsgiverperiode(val forslag: List<Map<String, LocalDate>>) : ForespurtOpplysning()
internal data class FastsattInntekt(val fastsattInntekt: Double) : ForespurtOpplysning()

internal data class Inntektsforslag(val beregningsmåneder: List<YearMonth>)
internal data class Inntekt(val forslag: Inntektsforslag) : ForespurtOpplysning()

internal fun JsonNode.asForespurteOpplysninger(): List<ForespurtOpplysning> =
    mapNotNull(JsonNode::asForespurtOpplysning)

internal fun JsonNode.asForespurtOpplysning() = when (val opplysningstype = this["opplysningstype"].asText()) {
    "Inntekt" -> Inntekt(forslag = this["forslag"].asInntektsforslag())
    "Refusjon" -> Refusjon(forslag = this["forslag"].asRefusjonsforslag())
    "Arbeidsgiverperiode" -> Arbeidsgiverperiode(forslag = this["forslag"].asArbeidsgiverperiodeforslag())
    "FastsattInntekt" -> FastsattInntekt(fastsattInntekt = this["fastsattInntekt"].asDouble())
    else -> {
        sikkerlogg.error("Mottok et trenger_opplysninger_fra_arbeidsgiver-event med ukjent opplysningtype: $opplysningstype")
        null
    }
}

private fun JsonNode.asArbeidsgiverperiodeforslag(): List<Map<String, LocalDate>> = map { periode ->
    mapOf(
        "fom" to periode["fom"].asLocalDate(),
        "tom" to periode["tom"].asLocalDate()
    )
}

private fun JsonNode.asInntektsforslag() =
    Inntektsforslag(beregningsmåneder = this["beregningsmåneder"].map(JsonNode::asYearMonth))

private fun JsonNode.asRefusjonsforslag() = map {
    Refusjonsforslag(
        it["fom"].asLocalDate(),
        it["tom"].asOptionalLocalDate(),
        it["beløp"].asDouble(),
    )
}