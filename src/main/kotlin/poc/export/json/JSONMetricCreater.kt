package poc.export.json

import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.sdk.metrics.data.MetricData
import org.json.JSONObject
import org.springframework.stereotype.Component
import poc.export.metric.GaugeCreater
import poc.export.metric.ResultType
import poc.export.metric.ResultType.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

@Component
class JSONMetricCreater(
    private val helper: JSONMetricCreaterHelper,
    private val gaugeCreater: GaugeCreater
    ) {

    fun createRequestMetric(results: List<JSONObject>, unit: String): List<MetricData> {
        val data = mutableListOf<MetricData>()
        val groupedResults = results.stream()
            .collect(Collectors.groupingBy { json -> json.getString("metric") })

        for(resultGroup in groupedResults.values) {
            for(result in resultGroup) {
                val name = result.getString("metric")
                val dataObject = result.getJSONObject("data")
                val url = dataObject.getJSONObject("tags").getString("url")
                val method = dataObject.getJSONObject("tags").getString("method")
                val attributes = Attributes.builder()
                    .put(AttributeKey.stringKey("endpoint"), url)
                    .put(AttributeKey.stringKey("http_method"), method)
                    .build()

                val metric = dataObject.getDouble("value")
                val time = dataObject.getString("time")
                val epochNanos = helper.getEpochNanos(time)

                val metricData = gaugeCreater.createDoubleGaugeData(name, unit, attributes, metric, epochNanos)
                data.add(metricData)
            }
        }
        return data
    }

    fun createGaugeMetricList(results: List<JSONObject>, name: String, unit: String): List<MetricData> {
        val data = mutableListOf<MetricData>()

        for(result in results) {
            val attributes = Attributes.empty()
            val dataObject = result.getJSONObject("data")
            val metric = dataObject.getDouble("value")
            val time = dataObject.getString("time")
            val epochNanos = helper.getEpochNanos(time)

            val metricData = gaugeCreater.createDoubleGaugeData(name, unit, attributes, metric, epochNanos)
            data.add(metricData)
        }
        return data
    }

    fun createSingleGaugeMetric(results: List<JSONObject>, type: ResultType): List<MetricData> {
        if(results.isEmpty()) return emptyList()

        val timestamp = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis())
        val name = type.toString().lowercase(Locale.getDefault())
        val attributes = Attributes.empty()
        var metric = 0.0
        var unit = "1"

        when(type) {
            MAX_LOAD -> metric = helper.getMaxLoad(results)
            ITERATIONS, HTTP_REQS -> metric = helper.getAmount(results)
            CHECKS -> {
                metric = helper.getAverage(results)
                unit = "%"
            }
            ITERATION_DURATION -> {
                metric = helper.getAverage(results)
                unit = "ms"
            }
        }

        val metricData = gaugeCreater.createDoubleGaugeData(name, unit, attributes, metric, timestamp)
        return Collections.singletonList(metricData)
    }
}