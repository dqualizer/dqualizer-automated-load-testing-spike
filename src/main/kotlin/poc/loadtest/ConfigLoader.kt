package poc.loadtest

import org.springframework.stereotype.Component
import java.lang.RuntimeException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

@Component
class ConfigLoader {

    fun loadConfig(): String {
        val serverURI = "http://127.0.0.1:8080/config"
        val uri = URI(serverURI)
        val request = HttpRequest.newBuilder()
            .uri(uri)
            .GET()
            .build()

        val response = HttpClient.newHttpClient()
            .send(request, BodyHandlers.ofString())

        val statusCode = response.statusCode()
        if(statusCode != 200) throw RuntimeException("Loading config failed - Status not 200, but $statusCode")
        return response.body()
    }
}