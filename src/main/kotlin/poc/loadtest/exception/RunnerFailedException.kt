package poc.loadtest.exception

import java.lang.RuntimeException

class RunnerFailedException(message: String):RuntimeException(message)