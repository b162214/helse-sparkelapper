package no.nav.helse.sparkel.oppgaveendret

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.time.Duration
import no.nav.helse.rapids_rivers.RapidsConnection
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.LoggerFactory

internal class OppgaveEndretConsumer(
    private val rapidConnection: RapidsConnection,
    private val kafkaConsumer: KafkaConsumer<String, String>,
    private val oppgaveEndretProducer: OppgaveEndretProducer,
    private val objectMapper: ObjectMapper
) : AutoCloseable, Runnable {
    private var konsumerer = true
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun run() {
        logger.info("OppgaveEndretConsumer starter opp")
        try {
            while (konsumerer) {
                kafkaConsumer.poll(Duration.ofMillis(100)).forEach { consumerRecord ->
                    val record = consumerRecord.value()
                    logger.info("record: " + record)
                    val oppgave: Oppgave = objectMapper.readValue(record)
                    logger.info("oppgave: " + oppgave)
                    logger.info("Mottatt oppgave_endret {}", oppgave.id)

                    // TODO håndtere at meldinger bare blir lest en gang?
                    oppgaveEndretProducer.onPacket(oppgave)
                }
            }
        } catch (e: Exception) {
            logger.error("Feilet under konsumering av oppgave_endret", e)
        } finally {
            close()
            rapidConnection.stop()
        }
    }

    override fun close() {
        konsumerer = false
    }

}