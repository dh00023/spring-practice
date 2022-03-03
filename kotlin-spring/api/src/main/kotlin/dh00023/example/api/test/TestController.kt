package dh00023.example.api.test

import dh00023.example.common.domain.TestService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class TestController(private val service: TestService) {
    @GetMapping("/{id}")
    fun findOne(@PathVariable id: Long) = service.getOne(id)
}
