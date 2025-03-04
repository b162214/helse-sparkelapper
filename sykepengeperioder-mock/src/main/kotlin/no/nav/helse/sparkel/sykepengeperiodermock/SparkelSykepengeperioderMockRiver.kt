package no.nav.helse.sparkel.sykepengeperiodermock

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageProblems
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.helse.rapids_rivers.asLocalDate
import org.slf4j.LoggerFactory
import no.nav.helse.rapids_rivers.MessageContext

internal class SparkelSykepengeperioderMockRiver(
    private val rapidsConnection: RapidsConnection,
    private val svar: Map<String, List<Sykepengehistorikk>>
) : River.PacketListener {

    private val log = LoggerFactory.getLogger("SparkelSykepengeperioderMockRiver")
    private val sikkerlogg = LoggerFactory.getLogger("tjenestekall")

    companion object {
        const val behov = "Sykepengehistorikk"
    }

    init {
        River(rapidsConnection).apply {
            validate { it.demandAll("@behov", listOf(behov)) }
            validate { it.rejectKey("@løsning") }
            validate { it.requireKey("@id") }
            validate { it.requireKey("fødselsnummer") }
            validate { it.require("$behov.historikkFom", JsonNode::asLocalDate) }
            validate { it.require("$behov.historikkTom", JsonNode::asLocalDate) }
        }.register(this)
    }

    override fun onError(problems: MessageProblems, context: MessageContext) {
        sikkerlogg.error("forstod ikke $behov med melding\n${problems.toExtendedReport()}")
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        sikkerlogg.info("mottok melding: ${packet.toJson()}")
        log.info("besvarer behov for sykepengehistorikk på id: ${packet["@id"].textValue()}")
        val fødselsnummer = packet["fødselsnummer"].asText()
        val utbetalteSykeperiode = svar.getOrDefault(
            fødselsnummer, emptyList<Sykepengehistorikk>()
                .also { log.info("Fant ikke forhåndskonfigurert sykepengehistorikk. Defaulter til en som er tom") }
        )
        packet["@løsning"] = mapOf(
            behov to objectMapper.convertValue(utbetalteSykeperiode, ArrayNode::class.java)
        )
        context.publish(packet.toJson())
    }
}
