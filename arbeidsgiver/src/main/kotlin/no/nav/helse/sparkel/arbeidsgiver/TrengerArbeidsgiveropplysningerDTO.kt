package no.nav.helse.sparkel.arbeidsgiver

import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.asLocalDate
import no.nav.helse.rapids_rivers.asLocalDateTime

internal data class TrengerArbeidsgiveropplysningerDTO(
    val type: Meldingstype,
    val fødselsnummer: String,
    val organisasjonsnummer: String,
    val vedtaksperiodeId: UUID,
    val fom: LocalDate,
    val tom: LocalDate,
    val forespurteOpplysninger: List<ForespurtOpplysning>,
    val opprettet: LocalDateTime = LocalDateTime.now()
) {
    val meldingstype get() = type.name.lowercase().toByteArray()
    constructor(message: JsonMessage) : this(
        type = Meldingstype.TRENGER_OPPLYSNINGER_FRA_ARBEIDSGIVER,
        fødselsnummer = message["fødselsnummer"].asText(),
        organisasjonsnummer = message["organisasjonsnummer"].asText(),
        vedtaksperiodeId = UUID.fromString(message["vedtaksperodeId"].asText()),
        fom = message["fom"].asLocalDate(),
        tom = message["tom"].asLocalDate(),
        forespurteOpplysninger = message["forespurteOpplysninger"].asForespurteOpplysninger(),
        opprettet = message["@opprettet"].asLocalDateTime()
    )
}

private fun JsonNode.asForespurteOpplysninger(): List<ForespurtOpplysning> {
    this.map { opplysning ->
        when (opplysning["opplysningstype"].asText()) {
            "Arbeidsgiverperiode" -> Arbeidsgiverperiode(opplysning),
            "Refusjon" -> Refusjon,

        }
    }
}


internal sealed class ForespurtOpplysning

internal data class Arbeidsgiverperiode(val forslag: List<Map<String, LocalDate>>): ForespurtOpplysning()
internal object Refusjon: ForespurtOpplysning()
internal object Inntekt: ForespurtOpplysning()

internal enum class Meldingstype {
    TRENGER_OPPLYSNINGER_FRA_ARBEIDSGIVER
}
