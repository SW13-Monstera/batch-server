package io.csbroker.batchserver.config

import io.csbroker.batchserver.client.AIServerClient
import io.csbroker.batchserver.dto.GradingRequestDto
import io.csbroker.batchserver.entity.GradingHistory
import io.csbroker.batchserver.util.log
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime
import javax.persistence.EntityManagerFactory

@Configuration
class BatchConfig(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val entityManagerFactory: EntityManagerFactory,
    private val aiServerClient: AIServerClient
) {

    @Bean
    fun gradingJob(): Job {
        return jobBuilderFactory.get("reGradingJob")
            .start(gradingStep())
            .build()
    }

    @Bean
    @JobScope
    fun gradingStep(): Step {
        return stepBuilderFactory.get("reGradingStep")
            .chunk<GradingHistory, GradingHistory>(10)
            .reader(reader(0L, ""))
            .processor(processor(0L, ""))
            .writer(writer(0L, ""))
            .build()
    }

    @Bean
    @StepScope
    fun reader(
        @Value("#{jobParameters[problemId]}") problemId: Long,
        @Value("#{jobParameters[date]}") date: String
    ): JpaPagingItemReader<GradingHistory> {
        log.info("==> reader date $date, problem id : $problemId")

        val parameterValues = mutableMapOf<String, Any>()
        parameterValues["problemId"] = problemId

        return JpaPagingItemReaderBuilder<GradingHistory>()
            .pageSize(100)
            .parameterValues(parameterValues)
           .queryString("SELECT DISTINCT gh FROM GradingHistory gh join fetch gh.problem p WHERE gh.problem.id = :problemId")
            .entityManagerFactory(entityManagerFactory)
            .name("JpaPagingItemReader")
            .build()
    }

    @Bean
    @StepScope
    fun processor(
        @Value("#{jobParameters[problemId]}") problemId: Long,
        @Value("#{jobParameters[date]}") date: String
    ): ItemProcessor<GradingHistory, GradingHistory> {
        return ItemProcessor<GradingHistory, GradingHistory> {
            log.info("==> processor gradingHistoryId : ${it.gradingHistoryId}")
            log.info("==> processor date $date, problem id : $problemId")
            log.info("==> processor score before : ${it.score}")

            var newScore = 0.0
            val gradingStandards = it.problem.gradingStandards

            val gradingRequestDto = GradingRequestDto.createGradingRequestDto(
                problemId,
                it.userAnswer,
                gradingStandards
            )

            val correctIds = this.aiServerClient.getGrade(gradingRequestDto).getCorrectGradingStandardIds()

            correctIds.map {
                val gradingStandard = gradingStandards.find { gs ->
                    gs.id == it
                }

                newScore += gradingStandard?.score ?: 0.0
            }

            it.score = newScore
            it.updatedAt = LocalDateTime.now()

            log.info("==> processor score after : ${it.score}")
            it
        }
    }

    @Bean
    @StepScope
    fun writer(
        @Value("#{jobParameters[problemId]}") problemId: Long,
        @Value("#{jobParameters[date]}") date: String
    ): JpaItemWriter<GradingHistory> {
        log.info("==> writer date $date, problem id : $problemId")

        return JpaItemWriterBuilder<GradingHistory>()
            .entityManagerFactory(entityManagerFactory)
            .build()
    }
}
