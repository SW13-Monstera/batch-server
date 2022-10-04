package io.csbroker.batchserver.controller

import io.csbroker.batchserver.util.log
import org.slf4j.MDC
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameter
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersInvalidException
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID
import javax.batch.operations.JobExecutionAlreadyCompleteException
import javax.batch.operations.JobRestartException
import javax.persistence.EntityManager

@RestController
@RequestMapping("/grade")
class RegradingJobLauncherController(
    private val job: Job,
    private val jobLauncher: JobLauncher,
    private val entityManager: EntityManager
) {

    @GetMapping("/{problemId}")
    fun regradingProblem(@PathVariable("problemId") problemId: Long) : String{
        MDC.put("traceId", UUID.randomUUID().toString())
        log.info("==> re-grading request coming with problem id : $problemId")
        try {
            val jobParametersMap = mutableMapOf<String, JobParameter>()
            jobParametersMap["problemId"] = JobParameter(problemId)
            jobParametersMap["date"] = JobParameter(LocalDateTime.now(ZoneId.of("Asia/Seoul")).toString())

            val jobParameters = JobParameters(jobParametersMap)
            val jobExecution = jobLauncher.run(job, jobParameters)

            while (jobExecution.isRunning) {
                log.info("==> running re-grading with problem id : $problemId")
            }
        } catch (e: JobExecutionAlreadyCompleteException) {
            e.printStackTrace()
        } catch (e: JobRestartException) {
            e.printStackTrace()
        } catch (e: JobInstanceAlreadyCompleteException) {
            e.printStackTrace()
        } catch (e: JobParametersInvalidException) {
            e.printStackTrace()
        }

        return "done"
    }
}