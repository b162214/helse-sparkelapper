package no.nav.helse.sparkel.norg

import java.nio.file.Files
import java.nio.file.Paths
import java.util.Base64

private val serviceuserBasePath = Paths.get("/var/run/secrets/nais.io/service_user")

fun readServiceUserCredentials() = ServiceUser(
    username = Files.readString(serviceuserBasePath.resolve("username")),
    password = Files.readString(serviceuserBasePath.resolve("password"))
)

class ServiceUser(
    val username: String,
    val password: String
) {
    val basicAuth = "Basic ${Base64.getEncoder().encodeToString("$username:$password".toByteArray())}"
}

fun readEnvironment() = Environment(
    norg2BaseUrl = System.getenv("NORG2_BASE_URL"),
    pdlUrl = System.getenv("PDL_URL"),
    securityTokenServiceUrl = System.getenv("SECURITY_TOKEN_SERVICE_URL")
)

data class Environment(
    val norg2BaseUrl: String,
    val pdlUrl: String,
    val securityTokenServiceUrl: String
)
