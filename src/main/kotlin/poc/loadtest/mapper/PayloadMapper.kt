package poc.loadtest.mapper

import org.json.JSONObject
import org.springframework.stereotype.Component
import poc.loadtest.mapper.k6Mapper.Companion.newLine

@Component
class PayloadMapper: k6Mapper {

    override fun map(request: JSONObject, requestIndex: Int): String {
        val payload = request.getJSONObject("payload")
        val payloadString = payload.toString()

        return String.format("%svar payload%d = %s%s",
            newLine, requestIndex, payloadString, newLine)
    }
}