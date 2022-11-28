package poc.export.json

import org.json.JSONObject
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit

@Component
class JSONMetricCreaterHelper {

    fun getAverage(results: List<JSONObject>): Double {
        val sum = this.getAmount(results)
        val count = results.size
        val average = sum/count

        if(average.isNaN()) throw IllegalArgumentException("Average value is not a number (NaN)")
        else return average
    }

    fun getMaxLoad(results: List<JSONObject>): Double {
        val maxLoad = results.stream()
            .map { result -> result.getJSONObject("data").getDouble("value") }.toList()
            .maxOrNull()

        return maxLoad ?: 0.0
    }

    fun getAmount(results: List<JSONObject>): Double {
        val sum = results.stream()
            .map { result -> result.getJSONObject("data").getDouble("value") }.toList()
            .sum()
        return sum
    }

    fun getEpochNanos(time: String): Long {
        val timestamp = OffsetDateTime.parse(time).toEpochSecond()
        val epochNanos = TimeUnit.SECONDS.toNanos(timestamp)

        return epochNanos
    }
}