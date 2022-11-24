package poc.loadtest

import org.json.JSONObject
import org.springframework.stereotype.Component
import poc.loadtest.exception.InvalidConfigurationException
import poc.loadtest.mapper.RequestMapper
import java.io.FileWriter

@Component
class ConfigParser(private val mapper: RequestMapper) {

    fun parse(config: String, scriptPath: String) {
        val configJSON = JSONObject(config)
        if(!isConfigValid(configJSON)) throw InvalidConfigurationException()

        val scriptCode:List<String> = mapper.createScript(configJSON)
        val writer = FileWriter(scriptPath)

        for(line in scriptCode) writer.write(line)
        writer.close()
    }

    fun isConfigValid(config: JSONObject): Boolean {
        return config.has("baseURL") && config.has("options") && config.has("requests")
    }
}