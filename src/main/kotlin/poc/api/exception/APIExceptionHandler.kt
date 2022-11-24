package poc.api.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.io.IOException

@ControllerAdvice
class APIExceptionHandler {

    @ExceptionHandler
    fun handleIOException(exception: IOException): ResponseEntity<String?>? {
        return ResponseEntity(exception.message, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler
    fun handleNullPointerException(exception: NullPointerException): ResponseEntity<String?>? {
        return ResponseEntity(exception.message, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}