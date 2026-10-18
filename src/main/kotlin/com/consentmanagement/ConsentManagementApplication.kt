package com.consentmanagement

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ConsentManagementApplication

fun main(args: Array<String>) {
    runApplication<ConsentManagementApplication>(*args)
}
