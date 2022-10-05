package io.csbroker.batchserver.controller

import io.csbroker.batchserver.dto.RegradingResponseDto
import io.csbroker.batchserver.service.RegradingJobLauncherService
import io.csbroker.batchserver.util.log
import org.slf4j.MDC
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.ZoneId

@RestController
@RequestMapping("/grade")
class RegradingJobLauncherController(
    private val regradingJobLauncherService: RegradingJobLauncherService
) {

    @GetMapping("/{problemId}")
    fun regradingProblem(@PathVariable("problemId") problemId: Long): RegradingResponseDto {
        log.info("==> re-grading request coming with problem id : $problemId")

        regradingJobLauncherService.regradingProblem(problemId, LocalDateTime.now(ZoneId.of("Asia/Seoul")))

        return RegradingResponseDto(
            "#$problemId problem re-grading batch job is started",
            MDC.get("traceId")
        )
    }
}
