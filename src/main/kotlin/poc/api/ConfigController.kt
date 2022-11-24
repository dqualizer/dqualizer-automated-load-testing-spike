package poc.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import poc.config.PathConfig
import java.nio.file.Files
import java.nio.file.Paths

@RestController
@RequestMapping("/config")
class ConfigController @Autowired constructor(val paths: PathConfig) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getConfig():String {
        val configPath = paths.getConfig()
        val configText = Files.readString(Paths.get(configPath))

        return configText
    }
}