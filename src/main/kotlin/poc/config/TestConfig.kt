package poc.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class TestConfig(
    @Value("\${test.output:json}")
    private val outputType: String,
    @Value("\${test.breakpoint:false}")
    private val breakpointConfig: Boolean,
    @Value("\${test.loops:1}")
    private val loops: Int
) {

    fun getOutputType(): String {
        return outputType
    }

    fun getBreakpointConfig(): Boolean {
        return breakpointConfig
    }

    fun getMaxLoops():Int {
        return if(loops < 1) 1
        else loops
    }


}