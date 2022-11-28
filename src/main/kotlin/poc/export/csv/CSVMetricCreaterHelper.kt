package poc.export.csv

import org.springframework.stereotype.Component

@Component
class CSVMetricCreaterHelper {

    fun getAverage(csv: List<Array<String>>): Double {
        val sum = this.getAmount(csv)
        val count = csv.size
        val average = sum/count

        if(average.isNaN()) throw IllegalArgumentException("Average value is not a number (NaN)")
        else return average
    }

    fun getMaxLoad(csv: List<Array<String>>): Double {
        val maxLoad = csv.stream()
            .map { row -> row[2].toDouble() }.toList()
            .maxOrNull()

        return maxLoad ?: 0.0
    }

    fun getAmount(csv: List<Array<String>>): Double {
        val sum = csv.stream()
            .map { row -> row[2].toDouble() }.toList()
            .sum()
        return sum
    }
}