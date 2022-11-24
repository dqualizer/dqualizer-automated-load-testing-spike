package poc.loadtest.mapper

import org.json.JSONObject

internal interface k6Mapper {

    fun map(request: JSONObject, requestIndex: Int): String

    companion object {
        val newLine = System.lineSeparator()
    }
}