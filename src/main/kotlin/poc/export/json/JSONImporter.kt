package poc.export.json

import io.opentelemetry.sdk.metrics.data.MetricData
import org.json.JSONObject
import org.springframework.stereotype.Component
import poc.export.metric.ResultType
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.stream.Stream

@Component
class JSONImporter(
    private val metricCreater: JSONMetricCreater,
    private val thresholdImporter: ThresholdImporter
    ) {

    fun importMetricData(filePath: String): List<MetricData> {
        val allResults = this.readFile(filePath)

        val http_reqs = allResults.stream()
            .filter { json -> json.getString("type").equals("Point") &&
                json.getString("metric").startsWith("http_req_") }
            .toList()
        val vus = this.filterMetric(allResults, "vus")
        val data_sent = this.filterMetric(allResults, "data_sent")
        val data_received = this.filterMetric(allResults, "data_received")
        val data_sent_endpoint = filterMetric(allResults, "data_sent_endpoint")
        val data_received_endpoint = filterMetric(allResults, "data_received_endpoint")

        val checks = this.filterMetric(allResults, "checks")
        val iteration_duration = filterMetric(allResults, "iteration_duration")
        val iterations = filterMetric(allResults, "iterations")
        val http_req_count = filterMetric(allResults, "http_reqs")

        val requestMetric = metricCreater.createRequestMetric(http_reqs, "ms")
        val vusMetric = metricCreater.createGaugeMetricList(vus, "vus", "1")
        val dataSentMetric = metricCreater.createGaugeMetricList(data_sent, "data_sent", "B")
        val dataReceivedMetric = metricCreater.createGaugeMetricList(data_received, "data_received", "B")
        val dataSentEndpointMetric = metricCreater.createGaugeMetricList(data_sent_endpoint, "data_sent_endpoint", "B")
        val dataReceivedEndpointMetric = metricCreater.createGaugeMetricList(data_received_endpoint, "data_received_endpoint", "B")

        val checksMetric = metricCreater.createSingleGaugeMetric(checks, ResultType.CHECKS)
        val vusMaxMetric = metricCreater.createSingleGaugeMetric(vus, ResultType.MAX_LOAD)
        val iterationMetric = metricCreater.createSingleGaugeMetric(iteration_duration, ResultType.ITERATION_DURATION)
        val iterationsCounterMetric = metricCreater.createSingleGaugeMetric(iterations, ResultType.ITERATIONS)
        val requestCounterMetric = metricCreater.createSingleGaugeMetric(http_req_count, ResultType.HTTP_REQS)
        val thresholds = thresholdImporter.importThreshold(allResults)

        return this.combineData(requestMetric, vusMetric, dataSentMetric, dataReceivedMetric, dataSentEndpointMetric, dataReceivedEndpointMetric,
            checksMetric, vusMaxMetric, iterationMetric, iterationsCounterMetric, requestCounterMetric, thresholds)
    }

    private fun readFile(path: String): List<JSONObject> {
        val strings = File(path).bufferedReader().readLines()
        val objects = strings.stream()
            .map { string -> JSONObject(string) }.toList()

        return objects
    }

    private fun filterMetric(list: List<JSONObject>, filter:String): List<JSONObject> {
        return list.stream()
            .filter { json -> json.getString("type").equals("Point") &&
                        json.getString("metric").equals(filter)
            }.toList()
    }

    private fun combineData(vararg createdData: List<MetricData>): List<MetricData> {
        val combinedData = mutableListOf<MetricData>()
        Stream.of(*createdData).forEach { data -> combinedData.addAll(data) }
        return combinedData
    }
}