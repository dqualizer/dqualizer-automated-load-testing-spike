package poc.loadtest.mapper

import org.json.JSONArray
import org.json.JSONObject
import org.springframework.stereotype.Component
import poc.loadtest.mapper.k6Mapper.Companion.newLine
import java.util.*
import java.util.logging.Logger

@Component
class RequestMapper(
    private val httpMapper: HttpMapper,
    private val paramsMapper: ParamsMapper,
    private val payloadMapper: PayloadMapper,
    private val checksMapper: ChecksMapper
): k6Mapper {

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

        if(request.has("checks")) {
            val checksScript = checksMapper.map(request, requestIndex)
            requestBuilder.append(checksScript)
        }
        requestBuilder.append(sleepScript())

        return requestBuilder.toString()
    }

    fun startScript(config: JSONObject): String {
        val baseURL = config.getString("baseURL")
        val options = config.getJSONObject("options").toString()
        return  "import http from 'k6/http';" + newLine +
                "import {check, sleep} from 'k6';" + newLine +
                "let baseURL = '$baseURL';" + newLine +
                "export let options = $options;" + newLine + newLine +
                "export default function() {" + newLine
    }

    private fun sleepScript(): String {
        val random = Random()
        val duration = random.nextInt(5) + 1
        return String.format("sleep(%d);%s",
            duration, newLine)
    }

    private fun isRequestValid(request: JSONObject): Boolean{
        return request.has("type") && request.has("path")
    }
}