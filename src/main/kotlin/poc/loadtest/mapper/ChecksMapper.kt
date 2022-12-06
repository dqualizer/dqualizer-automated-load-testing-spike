package poc.loadtest.mapper

import org.json.JSONObject
import org.springframework.stereotype.Component
import poc.loadtest.mapper.K6Mapper.Companion.newLine

@Component
class ChecksMapper: K6Mapper {

    override fun map(request: JSONObject, requestIndex: Int): String {
        val checks = request.getJSONObject("checks")
        val type = request.getString("type")
        val checkBuilder = StringBuilder()

        if(checks.has("status")) {
            val status = checks.getInt("status")
            val statusScript =
                if(checks.has("OR-status")) {
                    val orStatus = checks.getInt("OR-status")
                    "\t'$type status was $status/$orStatus': x => x.status && (x.status == $status || x.status == $orStatus),$newLine"
                } else
                    "\t'$type status was $status': x => x.status && x.status == $status,$newLine"

            checkBuilder.append(statusScript);
        }

        if(checks.has("body")) {
            val body = checks.getJSONObject("body")
            if(body.has("min-length")) {
                val minLength = body.getInt("min-length")
                val minLengthScript = "\t'$type body size >= $minLength': x => x.body && x.body.length >= $minLength,$newLine"
                checkBuilder.append(minLengthScript)
            }
            if(body.has("includes")) {
                val includes = body.getString("includes")
                val includesScript = "\t'body includes $includes': x => x.body && x.body.includes('$includes'),$newLine"
                checkBuilder.append(includesScript)
            }
        }

        if (checks.has("error_code")) {
            val errorCode = checks.getInt("error_code")
            val errorCodeScript = "\t'error_code was $errorCode': x => x.error_code == $errorCode,$newLine"
            checkBuilder.append(errorCodeScript)
        }
        return "check(response$requestIndex, {$newLine$checkBuilder});$newLine"
    }
}