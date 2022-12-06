package poc.loadtest.mapper

import org.json.JSONObject

internal interface K6Mapper {

    fun map(request: JSONObject, requestIndex: Int): String

    companion object {
        val newLine: String = System.lineSeparator()
    }
}