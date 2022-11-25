package poc.loadtest.mapper

import org.json.JSONObject
import org.springframework.stereotype.Component
import poc.loadtest.mapper.k6Mapper.Companion.newLine

@Component
class ChecksMapper: k6Mapper {

    override fun map(request: JSONObject, requestIndex: Int): String {
        val checks = request.getJSONObject("checks")
        val type = request.getString("type")
        val checkBuilder = StringBuilder()

        if(checks.has("status")) {
            val status = checks.getInt("status")
            val statusScript =
                if(checks.has("OR-status")) {
                    val orStatus = checks.getInt("OR-status")
                    String.format("\t'%s status was %s/%s': x => x.status && (x.status == %s || x.status == %s),%s",
                        type, status, orStatus, status, orStatus, newLine)
                } else
                    String.format("\t'%s status was %s': x => x.status && x.status == %s,%s",
                        type, status, status, newLine)

            checkBuilder.append(statusScript);
        }

        if(checks.has("body")) {
            val body = checks.getJSONObject("body")
            if(body.has("min-length")) {
                val minLength = body.getInt("min-length")
                val minLengthScript = String.format("\t'%s body size >= %d': x => x.body && x.body.length >= %d,%s",
                    type, minLength, minLength, newLine)
                checkBuilder.append(minLengthScript)
            }
            if(body.has("includes")) {
                val includes = body.getString("includes")
                val includesScript = String.format("\t'body includes %s': x => x.body && x.body.includes('%s'),%s",
                    includes, includes, newLine)
                checkBuilder.append(includesScript)
            }
        }

        if (checks.has("error_code")) {
            val errorCode = checks.getInt("error_code")
            val errorCodeScript = String.format("\t'error_code was %d': x => x.error_code == %d,%s",
                errorCode, errorCode, newLine)
            checkBuilder.append(errorCodeScript)
        }

        return String.format("check(response%d, {%s%s});%s",
            requestIndex, newLine, checkBuilder, newLine);
    }
}