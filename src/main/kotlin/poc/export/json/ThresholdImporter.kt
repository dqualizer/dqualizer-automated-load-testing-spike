package poc.export.json

import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.sdk.metrics.data.MetricData
import org.json.JSONObject
import org.springframework.stereotype.Component
import poc.export.metric.GaugeCreater
import java.util.concurrent.TimeUnit

@Component
class ThresholdImporter(
    private val helper: JSONMetricCreaterHelper,
    private val gaugeCreater: GaugeCreater
) {

    fun importThreshold(allResults: List<JSONObject>): List<MetricData> {
        val thresholdData = allResults.stream()
            .filter { result -> result.getString("type").equals("Metric") }
            .map { result -> result.getJSONObject("data") }
            .filter { data -> data.getJSONArray("thresholds").length() > 0 }.toList()

        if(thresholdData.isEmpty()) return emptyList()

        val metrics = mutableListOf<MetricData>()
        val startEpochNanos = this.getStartEpochNanos(allResults)

        for(data in thresholdData) {
            val metric = this.createThresholdMetric(data, startEpochNanos)
            metrics.addAll(metric)
        }
        return metrics
    }

    private fun createThresholdMetric(data: JSONObject, startEpochNanos: Long): List<MetricData> {
        val name = "thresholds"
        val type = data.getString("type")
        val unit: String = this.getUnit(type)
        val thresholdType = data.getString("name")
        val thresholds = data.getJSONArray("thresholds")
        val endEpochNanos = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis())
        val metrics = mutableListOf<MetricData>()

        for(index in 0 until thresholds.length()) {
            val thresholdObject = thresholds.get(index)

            var threshold = ""
            if(thresholdObject is JSONObject) threshold = thresholdObject.getString("threshold")
            else if(thresholdObject is String) threshold = thresholdObject

            //remove logical operator and everything after
            val aggregation = threshold.replace("(?=<=|<|>|>=|!=|==)([^=].*)".toRegex(), "").trim()
            //remove logical operator and everything before
            val valueString = threshold.replace("(.*)(?<=<=|<|>|>=|!=|==)".toRegex(), "").trim()

            if(aggregation.isEmpty() || valueString.isEmpty()) continue

            val value = valueString.toDouble()
            val attributes = Attributes.builder()
                .put(AttributeKey.stringKey("threshold_type"), thresholdType)
                .put(AttributeKey.stringKey("aggregation"), aggregation)
                .build()

            //Create two DataPoints so a line can be drawn in visualization
            val metricStart = gaugeCreater.createDoubleGaugeData(name, unit, attributes, value, startEpochNanos)
            val metricEnd = gaugeCreater.createDoubleGaugeData(name, unit, attributes, value, endEpochNanos)
            metrics.add(metricStart)
            metrics.add(metricEnd)
        }
        return metrics
    }

    /**
     * Get first timestamp in JSON list
     */
    private fun getStartEpochNanos(allResults: List<JSONObject>): Long {
        val time = allResults.stream()
            .filter { result -> result.getString("type").equals("Point") }
            .findFirst().get()
            .getJSONObject("data")
            .getString("time")

        return helper.getEpochNanos(time)
    }

    private fun getUnit(type: String): String {
        return when (type) {
            "trend" -> "ms"
            "counter", "gauge" -> "1"
            "rate" -> "%"
            else -> throw IllegalStateException("Unexpected threshold type: $type")
        }
    }
}