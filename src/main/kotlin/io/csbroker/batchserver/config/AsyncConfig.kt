package io.csbroker.batchserver.config

import io.csbroker.batchserver.util.LoggingTaskDecorator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
class AsyncConfig {

    @Bean
    fun taskExecutor(): TaskExecutor {
        val taskExecutor = SimpleAsyncTaskExecutor()
        taskExecutor.concurrencyLimit = 10
        taskExecutor.setTaskDecorator(LoggingTaskDecorator())

        return taskExecutor
    }
}