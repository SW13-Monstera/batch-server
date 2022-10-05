package io.csbroker.batchserver.service

import java.time.LocalDateTime

interface RegradingJobLauncherService {
    fun regradingProblem(problemId : Long, date: LocalDateTime)
}
