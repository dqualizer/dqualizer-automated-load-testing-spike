package poc.loadtest.mapper

import org.json.JSONObject
import org.springframework.stereotype.Component
import poc.loadtest.exception.UnknownRequestTypeException
import poc.loadtest.mapper.K6Mapper.Companion.newLine
import java.util.*

@Component
class HttpMapper: K6Mapper {

    override fun map(request: JSONObject, requestIndex: Int): String {
        val path = request.getString("path")
        val type = request.getString("type").uppercase(Locale.getDefault())

        val method = when(type) {
            "GET" -> "get"
            "POST" -> "post"
            "PUT"-> "put"
            "DELETE" -> "del"
            else -> throw UnknownRequestTypeException(type)
        }

        var extraParams = ""
        if(request.has("payload") || request.has("params")) {
            extraParams =
                if(request.has("payload") && request.has("params"))
                    ", JSON.stringify(payload$requestIndex), params$requestIndex"
                else if(request.has("payload"))
                    ", JSON.stringify(payload$requestIndex)"
                else
                    ", params$requestIndex"
        }

        return "${newLine}let response$requestIndex = http.$method(baseURL + '$path'$extraParams);$newLine"
    }
}