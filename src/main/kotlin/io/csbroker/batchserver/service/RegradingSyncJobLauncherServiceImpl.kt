package io.csbroker.batchserver.service

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class RegradingSyncJobLauncherServiceImpl(
    private val job: Job,
    private val jobLauncher: JobLauncher
) : RegradingJobLauncherService {
    override fun regradingProblem(problemId: Long, date: LocalDateTime) {
        val jobParameters = JobParametersBuilder()
            .addLong("problemId", problemId)
            .addString("date", date.toString())
            .toJobParameters()

        jobLauncher.run(job, jobParameters)
    }
}
