package poc.loadtest

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import poc.config.PathConfig
import poc.config.TestConfig
import poc.loadtest.exception.RunnerFailedException
import poc.util.ProcessLogger
import java.util.logging.Logger

@Component
class CLRunner(
    private val tests: TestConfig,
    private val paths: PathConfig,
    private val loader: ConfigLoader,
    private val parser: ConfigParser,

    private val processLogger: ProcessLogger,

    private var scriptPath: String = paths.getScript(),
    private var outputPath: String = paths.getOutput(tests.getOutputType()),
    private var loggingPath: String = paths.getLogging(),
    private var isBreakpointEnabled: Boolean = tests.getBreakpointConfig(),
    private var maxLoop: Int = tests.getMaxLoops()
) {

    val logger: Logger = Logger.getLogger(this.javaClass.name)

    @EventListener(ApplicationReadyEvent::class)
    fun start() {
        logger.info("### LOAD TEST STARTED ###")
        try {
            this.startLoadTest()
        } catch (e: Exception) {
            logger.severe("### TEST FAILED ###")
            e.printStackTrace()
            throw RunnerFailedException(e.message!!)
        }
    }

    fun startLoadTest() {
        //Not sure, if all failed thresholds return 99, that´s their generic errorCode
        val thresholdHaveFailedErrorCode = 99

        var config = loader.loadConfig()
        for(currentLoop in 0 until maxLoop) {
            parser.parse(config, scriptPath)
            logger.info("### CONFIG WAS PARSED INTO SCRIPT ###")
            var exitCode = this.runCommand()
            //TODO OTel Exporter

            //TODO LoadIncreaser for Breakpoint Testing
            if(exitCode == thresholdHaveFailedErrorCode) break;
        }
    }

    fun runCommand():Int {
        var command = "k6 run $scriptPath"
        if(outputPath.endsWith(".json")) command += " --out json=$outputPath"
        else command += " --out csv=$outputPath"
        val process = Runtime.getRuntime().exec(command)

        processLogger.log(process, loggingPath)
        return process.exitValue()
    }
}