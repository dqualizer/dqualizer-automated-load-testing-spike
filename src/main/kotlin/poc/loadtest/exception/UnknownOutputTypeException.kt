package poc.loadtest.exception

import java.lang.RuntimeException

class UnknownOutputTypeException(type: String): RuntimeException("Unknown output type: $type")