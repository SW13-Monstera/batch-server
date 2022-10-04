package io.csbroker.batchserver.controller

import io.csbroker.batchserver.dto.RegradingResponseDto
import io.csbroker.batchserver.util.log
import org.slf4j.MDC
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameter
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.launch.support.SimpleJobLauncher
import org.springframework.boot.autoconfigure.batch.BasicBatchConfigurer
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.core.task.TaskExecutor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

@RestController
@RequestMapping("/grade")
class RegradingJobLauncherController(
    private val job: Job,
    private val taskExecutor: TaskExecutor,
    private val basicBatchConfigurer: BasicBatchConfigurer
) {

    @GetMapping("/{problemId}")
    fun regradingProblem(@PathVariable("problemId") problemId: Long): RegradingResponseDto {
        log.info("==> re-grading request coming with problem id : $problemId")
        val jobParametersMap = mutableMapOf<String, JobParameter>()
        jobParametersMap["problemId"] = JobParameter(problemId)
        jobParametersMap["date"] = JobParameter(LocalDateTime.now(ZoneId.of("Asia/Seoul")).toString())

        val jobParameters = JobParameters(jobParametersMap)

        val jobLauncher = basicBatchConfigurer.jobLauncher as SimpleJobLauncher

        jobLauncher.setTaskExecutor(taskExecutor)

        jobLauncher.run(job, jobParameters)

        return RegradingResponseDto(
            "#$problemId problem re-grading batch job is started",
            MDC.get("traceId")
        )
    }
}