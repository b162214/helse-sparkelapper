package no.nav.helse.sparkel.aareg

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.jackson.JacksonConverter
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.sparkel.aareg.arbeidsforhold.Arbeidsforholdbehovløser
import no.nav.helse.sparkel.aareg.arbeidsforholdV2.AaregClient
import no.nav.helse.sparkel.aareg.arbeidsforholdV2.ArbeidsforholdLøserV2
import no.nav.helse.sparkel.aareg.arbeidsgiverinformasjon.Arbeidsgiverinformasjonsbehovløser
import no.nav.helse.sparkel.aareg.azure.AzureAD
import no.nav.helse.sparkel.aareg.azure.AzureADProps
import no.nav.helse.sparkel.aareg.kodeverk.KodeverkClient
import no.nav.helse.sparkel.aareg.util.Environment
import no.nav.helse.sparkel.aareg.util.setUpEnvironment
import no.nav.helse.sparkel.ereg.EregClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val sikkerlogg: Logger = LoggerFactory.getLogger("tjenestekall")

internal val logger: Logger = LoggerFactory.getLogger("sparkel-aareg")

internal val objectMapper: ObjectMapper = jacksonObjectMapper()
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    .registerModule(JavaTimeModule())

fun main() {
    val environment = setUpEnvironment()
    val app = createApp(environment)
    app.start()
}

internal fun createApp(environment: Environment): RapidsConnection {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            register(ContentType.Application.Json, JacksonConverter(objectMapper))
        }
        expectSuccess = false
    }
    val azureAD = AzureAD(AzureADProps(environment.tokenEndpointURL, environment.clientId, environment.clientSecret, environment.aaregOauthScope))

    val kodeverkClient = KodeverkClient(
        kodeverkBaseUrl = environment.kodeverkBaseUrl,
        appName = environment.appName
    )

    val eregClient = EregClient(environment.organisasjonBaseUrl, environment.appName, httpClient)
    val aaregClient = AaregClient(environment.aaregBaseUrlRest, {azureAD.accessToken()})

    val rapidsConnection = RapidApplication.create(environment.raw)
    Arbeidsgiverinformasjonsbehovløser(rapidsConnection, kodeverkClient, eregClient)
    Arbeidsforholdbehovløser(rapidsConnection, aaregClient, kodeverkClient)
    ArbeidsforholdLøserV2(rapidsConnection, aaregClient)

    return rapidsConnection
}
