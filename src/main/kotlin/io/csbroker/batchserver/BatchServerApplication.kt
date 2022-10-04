package io.csbroker.batchserver

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableBatchProcessing
@EnableFeignClients
@SpringBootApplication
class BatchServerApplication

fun main(args: Array<String>) {
    runApplication<BatchServerApplication>(*args)
}
