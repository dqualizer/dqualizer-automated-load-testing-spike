package poc.export.json

import io.opentelemetry.sdk.metrics.data.MetricData
import org.springframework.stereotype.Component
import java.util.*

@Component
class JSONImporter {

    fun importMetricData(filePath: String): List<MetricData> {
        return Collections.emptyList()
    }
}