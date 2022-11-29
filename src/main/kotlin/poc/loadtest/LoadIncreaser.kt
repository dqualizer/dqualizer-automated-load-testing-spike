package poc.loadtest

import org.json.JSONArray
import org.json.JSONObject
import org.springframework.stereotype.Component

/**
 * LoadIncreaser copies the first stage and inserts it at the start of the JSONArray
 * Then all stages (except the copied one) will be multiplied by a specific factor
 * The copying ensures that the load will not increase too drastically
 */
@Component
class LoadIncreaser {

    fun increaseLoad(config: String): String {
        val configJSON = JSONObject(config)
        val stages = configJSON
            .getJSONObject("options")
            .getJSONObject("scenarios")
            .getJSONObject("breakpoint")
            .getJSONArray("stages")

        this.multiplyTargets(stages, 2)

        return configJSON.toString()
    }

    private fun multiplyTargets(array: JSONArray, factor: Int): JSONArray {
        //Iterate from end to the start
        for (i in array.length() downTo 1) {
            val stage = array.getJSONObject(i-1)
            val currentLoad = stage.getInt("target")
            val newLoad = currentLoad * factor

            val newStage = JSONObject(stage.toMap())
            newStage.put("target", newLoad)
            array.put(i, newStage)
        }
        return array
    }
}