package poc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinK6Application

fun main(args: Array<String>) {
	runApplication<KotlinK6Application>(*args)
	println("##### TESTING COMPLETE #####")
}