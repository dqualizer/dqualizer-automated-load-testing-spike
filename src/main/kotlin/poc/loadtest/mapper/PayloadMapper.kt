package poc.loadtest.mapper

import org.json.JSONObject
import org.springframework.stereotype.Component
import poc.loadtest.mapper.K6Mapper.Companion.newLine

@Component
class PayloadMapper: K6Mapper {

    override fun map(request: JSONObject, requestIndex: Int): String {
        val payload = request.getJSONObject("payload")
        val payloadString = payload.toString()

        return "${newLine}var payload$requestIndex = $payloadString$newLine"
    }
}