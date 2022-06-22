package no.nav.helse.sparkel.identer

import no.nav.helse.rapids_rivers.RapidsConnection
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.LoggerFactory
import java.time.Duration

internal class AktørConsumer(
    private val rapidConnection: RapidsConnection,
    private val kafkaConsumer: KafkaConsumer<ByteArray, GenericRecord>,
    private val identifikatorDao: IdentifikatorDao,
) : AutoCloseable, Runnable {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val sikkerlogg = LoggerFactory.getLogger("tjenestekall")
    private var konsumerer = true

    override fun run() {
        log.info("AktørConsumer starter opp")
        try {
            while (konsumerer) {
                val records = kafkaConsumer.poll(Duration.ofMillis(100))
                records.forEach {
                    val key = String(it.key())
                    sikkerlogg.info("Mottok melding, key=$key, unicodes=${it.key().map { String.format("u+%04x", Char(it.toInt()).code) }.joinToString("|")}")
                    it.value()?.also { genericRecord ->
                        val aktørV2 = parseAktørMessage(genericRecord, key)
                        aktørV2.gjeldendeFolkeregisterident()?.also { folkeregisterident ->
                            sikkerlogg.info("Mottok melding der gjeldende FNR/DNR=$folkeregisterident")
                            identifikatorDao.lagreAktør(aktørV2)
                        } ?: sikkerlogg.info("Fant ikke FNR/DNR på melding med key=$key, ignorerer melding.")
                    } ?: sikkerlogg.info("Value var null på melding med key=$key, ignorerer melding.")
                }
            }
        } catch (e: Exception) {
            log.error("Feilet under konsumering av aktørhendelse", e)
            throw e
        } finally {
            close()
            rapidConnection.stop()
        }
    }

    override fun close() {
        konsumerer = false
    }
}

fun parseAktørMessage(record: GenericRecord, key: String): AktørV2 {
    val identifikatorRecords = record.get("identifikatorer") as List<GenericRecord>
    return AktørV2(
        identifikatorer = identifikatorRecords.map {
            Identifikator(
                idnummer = it.get("idnummer").toString(),
                type = Type.valueOf(it.get("type").toString()),
                gjeldende = it.get("gjeldende").toString().toBoolean()
            )
        }.toList(),
        key = key
            .replace("\u0000", "")
            .replace("\u003e", "")
            .replace("\u001a", "")
    )
}
