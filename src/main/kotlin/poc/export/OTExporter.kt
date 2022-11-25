package poc.export

import io.opentelemetry.exporter.otlp.http.metrics.OtlpHttpMetricExporter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import poc.export.csv.CSVImporter
import poc.export.json.JSONImporter

@Component
class OTExporter(
    private val csvImporter: CSVImporter,
    private val jsonImporter: JSONImporter,
    @Value("\${otel.host:localhost}")
    private val host: String,
    private var exporter: OtlpHttpMetricExporter = OtlpHttpMetricExporter.getDefault()
) {

    fun export(outputPath: String) {
        this.buildExporter()
        if(outputPath.endsWith(".json")) this.exportJSONMetricData(outputPath)
        else this.exportCSVMetricData(outputPath)
    }

    private fun exportCSVMetricData(csvPath: String) {
        val metrics = csvImporter.importMetricData(csvPath)
        exporter.export(metrics)
    }

    private fun exportJSONMetricData(jsonPath: String) {
        val metrics = jsonImporter.importMetricData(jsonPath)
        exporter.export(metrics)
    }

    private fun buildExporter() {
        this.exporter = OtlpHttpMetricExporter
            .builder()
            .setEndpoint("http://$host:4318/v1/metrics")
            .build()
    }
}