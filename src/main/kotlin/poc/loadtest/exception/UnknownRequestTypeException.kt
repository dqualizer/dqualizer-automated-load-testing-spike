package poc.loadtest.exception

import java.lang.RuntimeException

class UnknownRequestTypeException(type: String):RuntimeException("Unknown request type: $type")