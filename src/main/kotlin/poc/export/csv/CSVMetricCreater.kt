package poc.export.csv

import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.common.AttributeKey.stringKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.sdk.metrics.data.MetricData
import org.json.JSONObject
import org.springframework.stereotype.Component
import poc.export.metric.GaugeCreater
import poc.export.metric.ResultType
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

@Component
class CSVMetricCreater(
    private val helper: CSVMetricCreaterHelper,
    private val gaugeCreater: GaugeCreater
) {

    fun createRequestMetric(csv: List<Array<String>>, unit: String): List<MetricData> {
        val data = mutableListOf<MetricData>()
        val groupedRequests = csv.stream()
            .collect(Collectors.groupingBy { row -> row[0] })

        for(rows in groupedRequests.values) {
            for(row in rows) {
                val name = row[0]
                val url = row[16]
                val method = row[8]
                val attributes = Attributes.builder()
                    .put(stringKey("endpoint"), url)
                    .put(stringKey("http_method"), method)
                    .build()

                val metric = row[2].toDouble()
                val timestamp = row[1].toLong()
                val epochNanos = TimeUnit.SECONDS.toNanos(timestamp)

                val metricData = gaugeCreater.createDoubleGaugeData(name, unit, attributes, metric, epochNanos)
                data.add(metricData)
            }
        }
        return data
    }

    fun createGaugeMetricList(csv: List<Array<String>>, name: String, unit: String): List<MetricData> {
        val data = mutableListOf<MetricData>()

        for(row in csv) {
            val attributes = Attributes.empty()
            val metric = row[2].toDouble()
            val timestamp = row[1].toLong()
            val epochNanos = TimeUnit.SECONDS.toNanos(timestamp)

            val metricData = gaugeCreater.createDoubleGaugeData(name, unit, attributes, metric, epochNanos)
            data.add(metricData)
        }
        return data
    }

    fun createSingleGaugeMetric(csv: List<Array<String>>, type: ResultType): List<MetricData> {
        if(csv.isEmpty()) return emptyList()

        val timestamp = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis())
        val name = type.toString().lowercase(Locale.getDefault())
        val attributes = Attributes.empty()
        var metric = 0.0
        var unit = "1"

        when(type) {
            ResultType.MAX_LOAD -> metric = helper.getMaxLoad(csv)
            ResultType.ITERATIONS, ResultType.HTTP_REQS -> metric = helper.getAmount(csv)
            ResultType.CHECKS -> {
                metric = helper.getAverage(csv)
                unit = "%"
            }
            ResultType.ITERATION_DURATION -> {
                metric = helper.getAverage(csv)
                unit = "ms"
            }
        }

        val metricData = gaugeCreater.createDoubleGaugeData(name, unit, attributes, metric, timestamp)
        return Collections.singletonList(metricData)
    }
}