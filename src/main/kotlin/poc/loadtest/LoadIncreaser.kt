package poc.loadtest

import org.json.JSONArray
import org.json.JSONObject
import org.springframework.stereotype.Component

@Component
class LoadIncreaser {

    fun increaseLoad(config: String): String {
        val configJSON = JSONObject(config)
        val options = configJSON.getJSONObject("options")
        val scenarios = options.getJSONObject("scenarios")
        val scenario = scenarios.getJSONObject("breakpoint")
        val stages = scenario.getJSONArray("stages")

        val newStages = multiplyTargets(stages, 2)
        val newScenario = scenario.put("stages", newStages)
        val newScenarios = scenarios.put("breakpoint", newScenario)
        val newOptions = options.put("scenarios", newScenarios)
        val newConfig = configJSON.put("options", newOptions)

        return newConfig.toString()
    }

    private fun multiplyTargets(array: JSONArray, factor: Int): JSONArray {
        for (i in 0 until array.length()) {
            val stage = array.getJSONObject(i)
            val currentLoad = stage.getInt("target")
            val newLoad = currentLoad * factor

            val newStage = stage.put("target", newLoad)
            array.put(i, newStage)
        }
        return array
    }
}