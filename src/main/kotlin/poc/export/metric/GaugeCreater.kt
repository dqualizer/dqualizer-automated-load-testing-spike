package poc.export.metric

import io.opentelemetry.api.common.Attributes
import io.opentelemetry.sdk.common.InstrumentationScopeInfo
import io.opentelemetry.sdk.metrics.data.MetricData
import io.opentelemetry.sdk.metrics.internal.data.ImmutableDoublePointData
import io.opentelemetry.sdk.metrics.internal.data.ImmutableGaugeData
import io.opentelemetry.sdk.metrics.internal.data.ImmutableMetricData
import io.opentelemetry.sdk.resources.Resource
import org.springframework.stereotype.Component
import java.util.Collections

@Component
class GaugeCreater {

    fun createDoubleGaugeData(name: String, unit: String, attributes: Attributes, metric: Double, epochNanos: Long): MetricData{
        return ImmutableMetricData.createDoubleGauge(
            Resource.empty(),
            InstrumentationScopeInfo.empty(),
            name,
            "DoubleGauge",
            unit,
            ImmutableGaugeData.create(Collections.singleton(
                ImmutableDoublePointData.create(
                    epochNanos, epochNanos, attributes, metric)))
        )
    }
}