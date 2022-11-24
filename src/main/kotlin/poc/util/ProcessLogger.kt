package poc.util

import org.apache.tomcat.util.http.fileupload.IOUtils
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.util.logging.Logger

/**
 * ProcessLogger writes the console output of a process into a text file
 * If an error occurs, the error message will be written into the console
 */
@Component
class ProcessLogger {
    val logger = Logger.getGlobal()

    fun log(process: Process, file: String) {
        val inputStream = process.inputStream

        val logFile = File(file)
        val outputStream = FileOutputStream(logFile)
        IOUtils.copy(inputStream, outputStream)
        this.waitForProcess(process)

        val exitValue = process.exitValue()
        logger.info("LOAD TEST FINISHED WITH EXIT VALUE $exitValue")
        if(exitValue != 0) this.logError(process)
    }

    fun logError(process: Process) {
        val errorStream = process.errorStream
        val errorMessage = String(errorStream.readAllBytes(), StandardCharsets.UTF_8)
        logger.warning(errorMessage)
    }

    fun waitForProcess(process: Process) {
        while(process.isAlive) {
            Thread.sleep(2000)
            println("...")
        }
    }
}