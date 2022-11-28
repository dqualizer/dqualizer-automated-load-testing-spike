package poc.export.csv

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import com.opencsv.enums.CSVReaderNullFieldIndicator
import io.opentelemetry.sdk.metrics.data.MetricData
import org.springframework.stereotype.Component
import poc.export.metric.ResultType
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*
import java.util.stream.Stream

@Component
class CSVImporter(private val metricCreater: CSVMetricCreater) {

    fun importMetricData(filePath: String): List<MetricData> {
        val csv = this.readFile(filePath)

        val http_reqs = csv.stream().filter { row -> row[0].startsWith("http_req_") }.toList()
        val vus = this.filterMetric(csv, "vus")
        val data_sent = this.filterMetric(csv, "data_sent")
        val data_received = this.filterMetric(csv, "data_received")

        val checks = this.filterMetric(csv, "checks")
        val iteration_duration = filterMetric(csv, "iteration_duration")
        val iterations = filterMetric(csv, "iterations")
        val http_req_count = filterMetric(csv, "http_reqs")

        val requestMetric = metricCreater.createRequestMetric(http_reqs, "ms")
        val vusMetric = metricCreater.createGaugeMetricList(vus, "vus", "1")
        val dataSentMetric = metricCreater.createGaugeMetricList(data_sent, "data_sent", "B")
        val dataReceivedMetric = metricCreater.createGaugeMetricList(data_received, "data_received", "B")

        val checksMetric = metricCreater.createSingleGaugeMetric(checks, ResultType.CHECKS)
        val vusMaxMetric = metricCreater.createSingleGaugeMetric(vus, ResultType.MAX_LOAD)
        val iterationMetric = metricCreater.createSingleGaugeMetric(iteration_duration, ResultType.ITERATION_DURATION)
        val iterationsCounterMetric = metricCreater.createSingleGaugeMetric(iterations, ResultType.ITERATIONS)
        val requestCounterMetric = metricCreater.createSingleGaugeMetric(http_req_count, ResultType.HTTP_REQS)

        return this.combineData(requestMetric, vusMetric, dataSentMetric, dataReceivedMetric,
            checksMetric, vusMaxMetric, iterationMetric, iterationsCounterMetric, requestCounterMetric)

    }

    private fun readFile(path: String): List<Array<String>> {
        val stream = FileInputStream(path)
        val streamReader = InputStreamReader(stream)

        val parser = CSVParserBuilder()
            .withSeparator(',')
            .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_QUOTES)
            .withIgnoreLeadingWhiteSpace(true)
            .build()
        val reader = CSVReaderBuilder(streamReader)
            .withSkipLines(1)
            .withCSVParser(parser)
            .build()
        val csv = reader.readAll()
        reader.close()
        return csv
    }

    private fun filterMetric(csv: List<Array<String>>, filter:String): List<Array<String>> {
        return csv.stream().filter { row -> row[0] == filter }.toList()
    }

    private fun combineData(vararg createdData: List<MetricData>): List<MetricData> {
        val combinedData = mutableListOf<MetricData>()
        Stream.of(*createdData).forEach { data -> combinedData.addAll(data) }
        return combinedData
    }
}