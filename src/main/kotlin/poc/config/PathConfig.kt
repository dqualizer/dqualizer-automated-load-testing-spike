package poc.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import poc.loadtest.exception.UnknownOutputTypeException

@Component
class PathConfig(
    @Value("\${path.config:config/exampleConfig.json}")
    private val config: String,
    private val script: String = "scripts/createdScript.js",
    private val outputCSV: String = "output/output.csv",
    private val outputJSON: String = "output/output.json",
    private val logging: String = "output/logging.txt"
) {
    private val resources = this.getResourcePath()

    fun getConfig(): String { return resources + config }

    fun getScript(): String { return resources + script }

    fun getOutput(outputType: String): String {
        return when(outputType) {
            "json" -> resources + outputJSON
            "csv" -> resources + outputCSV
            else -> throw UnknownOutputTypeException(outputType)
        }
    }

    fun getLogging(): String { return resources + logging }

    private fun getResourcePath(): String {
        return this.javaClass.classLoader
            .getResource("")
            .file
            .substring(1) //remove '/' at the beginning of the string
    }
}