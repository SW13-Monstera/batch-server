package io.csbroker.batchserver.function

import io.csbroker.batchserver.service.RegradingJobLauncherService
import io.csbroker.batchserver.util.log
import org.slf4j.MDC
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

@Component
class RegradingFunction(
    private val regradingJobLauncherService: RegradingJobLauncherService
) {
    @Bean
    fun regradingProblem(): (Long) -> (String) {
        return {
            MDC.put("traceId", UUID.randomUUID().toString())
            log.info("==> re-grading request coming with problem id : $it")
            regradingJobLauncherService.regradingProblem(it, LocalDateTime.now(ZoneId.of("Asia/Seoul")))
            MDC.get("traceId")
        }
    }

}