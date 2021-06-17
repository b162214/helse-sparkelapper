package no.nav.helse.sparkel.vilkarsproving.opptjening

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.helse.rapids_rivers.RapidsConnection
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

internal class OpptjeningLøserTest {
    private val objectMapper = jacksonObjectMapper()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .registerModule(JavaTimeModule())

    private lateinit var sendtMelding: JsonNode

    private val rapid = object : RapidsConnection() {
        fun sendTestMessage(message: String) {
            notifyMessage(message, this)
        }

        override fun publish(message: String) { sendtMelding = objectMapper.readTree(message) }

        override fun publish(key: String, message: String) {}

        override fun start() {}

        override fun stop() {}
    }

    @Test
    internal fun `løser opptjeningbehov`() {
        val behov = """{"@id": "${UUID.randomUUID()}", "@behov":["${OpptjeningLøser.behov}"], "fødselsnummer": "fnr", "vedtaksperiodeId": "id" }"""
        val mockAaregClient = AaregClient(
            baseUrl = "http://baseUrl.local",
            stsRestClient = mockStsRestClient,
            httpClient = aregMockClient(mockGenerator)
        )
        OpptjeningLøser(rapid, mockAaregClient)
        rapid.sendTestMessage(behov)
        val løsning = sendtMelding.løsning()
        assertTrue(løsning.isNotEmpty())
    }

    private fun JsonNode.løsning(): List<Arbeidsforhold> =
        this.path("@løsning")
            .path(OpptjeningLøser.behov)
            .map {
                Arbeidsforhold(
                    it["orgnummer"].asText(),
                    it["ansattSiden"].asLocalDate(),
                    it["ansattTil"].asOptionalLocalDate()
                )
            }
}
