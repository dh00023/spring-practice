package dh0023.kotlinmvc.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/config")
class ConfigController {
    @GetMapping("/")
    fun check(
        @Value("\${message.owner}") messageOwner: String,
        @Value("\${message.content}") messageContent: String
    ): String? {
        return """
            Configuration File's Message Owner: $messageOwner
            Configuration File's Message Content: $messageContent
        """.trimIndent()
    }
}
