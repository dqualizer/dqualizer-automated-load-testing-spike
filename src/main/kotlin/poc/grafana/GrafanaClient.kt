package poc.grafana

import org.json.JSONObject
import org.springframework.stereotype.Component
import java.lang.RuntimeException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Send Dashboards to the Grafana API via Basic Authentication
 * The Authorization header has to be Base64 encoded
 */
@Component
class GrafanaClient {

    private val postDashboardURI = "http://localhost:3030/api/dashboards/db"
    private val encodedBasicAuthorization = "YWRtaW46YWRtaW4="

    fun sendDashboard(file: String): String {
        val uri = URI(postDashboardURI)
        val dashboard = this.getDashboard(file)
        val bodyPublisher = BodyPublishers.ofString(dashboard)

        val request = HttpRequest.newBuilder()
            .uri(uri)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("Authorization", "Basic $encodedBasicAuthorization")
            .POST(bodyPublisher)
            .build()

        val response = HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString())

        val statusCode = response.statusCode()
        if(statusCode != 200) throw RuntimeException("Sending Dashboard failed - Status not 200, but $statusCode")
        return response.body()
    }

    private fun getDashboard(file: String): String {
        val text = this.getFileText(file)
        val json = JSONObject(text)

        val dashboard = JSONObject()
        dashboard.put("dashboard", json)
        dashboard.put("overwrite", true)

        return dashboard.toString()
    }

    private fun getFileText(filename: String): String {
        val projectDirectory = System.getProperty("user.dir")
        val filePath = "$projectDirectory/docker-config/grafana/my-dashboards/$filename"
        val path = Paths.get(filePath)

        return String(Files.readAllBytes(path))
    }
}