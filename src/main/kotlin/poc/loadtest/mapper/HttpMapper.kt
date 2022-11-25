package poc.loadtest.mapper

import org.json.JSONObject
import org.springframework.stereotype.Component
import poc.loadtest.exception.UnknownRequestTypeException
import poc.loadtest.mapper.k6Mapper.Companion.newLine
import java.util.*

@Component
class HttpMapper: k6Mapper {

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
                if(request.has("payload") && request.has("params")) {
                    String.format(", JSON.stringify(payload%d), params%d",
                        requestIndex, requestIndex);
                } else if(request.has("payload"))
                    String.format(", JSON.stringify(payload%d)", requestIndex);
                else
                    String.format(", params%d", requestIndex);
        }

        return String.format("%slet response%d = http.%s(baseURL + '%s'%s);%s",
            newLine, requestIndex, method, path, extraParams, newLine);
    }
}