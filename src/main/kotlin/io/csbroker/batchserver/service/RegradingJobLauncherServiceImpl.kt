package io.csbroker.batchserver.service

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.support.SimpleJobLauncher
import org.springframework.boot.autoconfigure.batch.BasicBatchConfigurer
import org.springframework.core.task.TaskExecutor
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class RegradingJobLauncherServiceImpl(
    private val job: Job,
    private val taskExecutor: TaskExecutor,
    private val basicBatchConfigurer: BasicBatchConfigurer
) : RegradingJobLauncherService {
    override fun regradingProblem(problemId: Long, date: LocalDateTime) {
        val jobParameters = JobParametersBuilder()
            .addLong("problemId", problemId)
            .addString("date", date.toString())
            .toJobParameters()

        val jobLauncher = basicBatchConfigurer.jobLauncher as SimpleJobLauncher
        jobLauncher.setTaskExecutor(taskExecutor)
        jobLauncher.run(job, jobParameters)
    }
}
