package no.nav.helse.sparkel.personinfo.leesah

import no.nav.helse.rapids_rivers.RapidApplication
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.LoggerFactory
import java.time.Duration

class PersonhendelseConsumer(
    private val rapidApplication: RapidApplication,
    private val kafkaConsumer: KafkaConsumer<ByteArray, GenericRecord>,
    private val personhendelseRiver: PersonhendelseRiver
): AutoCloseable, Runnable {
    private val log = LoggerFactory.getLogger("personhendelse-konsumer")
    private var konsumerer = true

    override fun run() {
        try {
            while (konsumerer) {
                val records = kafkaConsumer.poll(Duration.ofMillis(100))
                records.forEach {
                    val record = it.value()
                    personhendelseRiver.onPackage(record)
                }
            }
        } catch (e: Exception) {
            log.error("Feilet under konsumering av personhendelse", e)
        } finally {
            close()
            rapidApplication.stop()
        }
    }

    override fun close() {
        konsumerer = false
    }
}
