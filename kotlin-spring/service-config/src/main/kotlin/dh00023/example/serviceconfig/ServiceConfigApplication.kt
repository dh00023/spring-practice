package dh00023.example.serviceconfig

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.config.server.EnableConfigServer

@SpringBootApplication
@EnableConfigServer
class ServiceConfigApplication

fun main(args: Array<String>) {
    runApplication<ServiceConfigApplication>(*args)
}
