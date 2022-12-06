package poc.loadtest.mapper

import org.json.JSONObject
import org.springframework.stereotype.Component
import poc.loadtest.mapper.K6Mapper.Companion.newLine

@Component
class ParamsMapper: K6Mapper {

    override fun map(request: JSONObject, requestIndex: Int): String {
        val params = request.getJSONObject("params")
        val paramsString = params.toString()

        return "${newLine}var params$requestIndex = $paramsString$newLine"
    }
}