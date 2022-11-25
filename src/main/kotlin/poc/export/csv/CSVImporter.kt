package poc.export.csv

import io.opentelemetry.sdk.metrics.data.MetricData
import org.springframework.stereotype.Component
import java.util.*

@Component
class CSVImporter {

    fun importMetricData(filePath: String): List<MetricData> {
        return Collections.emptyList()
    }
}