package io.csbroker.batchserver.util

import org.slf4j.MDC
import org.springframework.core.task.TaskDecorator

class LoggingTaskDecorator : TaskDecorator {
    override fun decorate(runnable: Runnable): Runnable {
        val copyOfContextMap = MDC.getCopyOfContextMap()
        return Runnable {
            try {
                MDC.setContextMap(copyOfContextMap)
                runnable.run()
            } finally {
                MDC.clear()
            }
        }
    }
}