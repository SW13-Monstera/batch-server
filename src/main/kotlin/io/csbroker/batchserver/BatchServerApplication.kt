package io.csbroker.batchserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BatchServerApplication

fun main(args: Array<String>) {
    runApplication<BatchServerApplication>(*args)
}
