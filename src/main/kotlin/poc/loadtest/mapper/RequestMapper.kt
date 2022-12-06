package poc.loadtest.mapper

import org.json.JSONArray
import org.json.JSONObject
import org.springframework.stereotype.Component
import poc.loadtest.mapper.K6Mapper.Companion.newLine
import java.util.*
import java.util.logging.Logger

@Component
class RequestMapper(
    private val httpMapper: HttpMapper,
    private val paramsMapper: ParamsMapper,
    private val payloadMapper: PayloadMapper,
    private val checksMapper: ChecksMapper
): K6Mapper {

    val logger: Logger = Logger.getGlobal()

    fun createScript(config: JSONObject): List<String> {
        val requests:JSONArray = config.getJSONArray("requests")
        val createdScript = mutableListOf<String>()
        createdScript.add(startScript(config))

        for(index in 0 until requests.length()) {
            val currentRequest = requests.getJSONObject(index)
            if(!isRequestValid(currentRequest)) {
                logger.warning("INVALID REQUEST: $index")
                continue
            }
            val requestScript = this.map(currentRequest, index)
            createdScript.add(requestScript)
        }
        createdScript.add("}")
        return createdScript
    }

    override fun map(request: JSONObject, requestIndex: Int): String {
        val requestBuilder = StringBuilder()

        if(request.has("params")) {
            val paramsScript = paramsMapper.map(request, requestIndex)
            requestBuilder.append(paramsScript)
        }
        if(request.has("payload")) {
            val payloadScript = payloadMapper.map(request, requestIndex)
            requestBuilder.append(payloadScript)
        }

        val httpScript = httpMapper.map(request, requestIndex)
        requestBuilder.append(httpScript)
        requestBuilder.append(this.trackDataPerURLScript(requestIndex))

        if(request.has("checks")) {
            val checksScript = checksMapper.map(request, requestIndex)
            requestBuilder.append(checksScript)
        }
        requestBuilder.append(sleepScript())

        return requestBuilder.toString()
    }

    private fun startScript(config: JSONObject): String {
        val baseURL = config.getString("baseURL")
        val options = config.getJSONObject("options").toString()
        val trackDataPerURL = this.trackDataPerURLInitScript()

        return  "import http from 'k6/http';" + newLine +
                "import {check, sleep} from 'k6';" +
                trackDataPerURL + newLine + newLine +
                "let baseURL = '$baseURL';" + newLine +
                "export let options = $options;" + newLine + newLine +
                "export default function() {" + newLine
    }

    private fun trackDataPerURLInitScript(): String {
        return """
                import {Counter} from 'k6/metrics';
                                
                export const epDataSent = new Counter('data_sent_endpoint');
                export const epDataRecv = new Counter('data_received_endpoint');
                                
                function sizeOfHeaders(headers) {
                    return Object.keys(headers).reduce((sum, key) => sum + key.length + headers[key].length, 0);
                }
                
                function trackDataMetricsPerURL(res) {
                    epDataSent.add(sizeOfHeaders(res.request.headers) + res.request.body.length, { url: res.url });
                    epDataRecv.add(sizeOfHeaders(res.headers) + res.body.length, { url: res.url });
                }
                """.trimIndent()
    }

    private fun trackDataPerURLScript(requestIndex: Int): String {
        return "trackDataMetricsPerURL(response$requestIndex);$newLine"
    }

    private fun sleepScript(): String {
        val random = Random()
        val duration = random.nextInt(5) + 1
        return "sleep($duration);$newLine"
    }

    private fun isRequestValid(request: JSONObject): Boolean{
        return request.has("type") && request.has("path")
    }
}