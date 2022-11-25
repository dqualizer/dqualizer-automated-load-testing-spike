package poc.loadtest.mapper

import org.json.JSONObject
import org.springframework.stereotype.Component
import poc.loadtest.mapper.k6Mapper.Companion.newLine

@Component
class ParamsMapper: k6Mapper {

    override fun map(request: JSONObject, requestIndex: Int): String {
        val params = request.getJSONObject("params")
        val payloadString = params.toString()

        return String.format("%svar params%d = %s%s",
            newLine, requestIndex, payloadString, newLine)
    }
}