package io.csbroker.batchserver.config

import io.csbroker.batchserver.client.AIServerClient
import io.csbroker.batchserver.dto.GradingRequestDto
import io.csbroker.batchserver.dto.GradingResponseDto
import io.csbroker.batchserver.entity.GradingHistory
import io.csbroker.batchserver.entity.GradingStandard
import io.csbroker.batchserver.util.log
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.StepExecutionListener
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
            .listener(stepExecutionListener())
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

            val gradingResponseDto = this.sendGradingRequest(it)

            it.score = this.getScore(gradingResponseDto, it.problem.gradingStandards)
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

    @Bean
    @StepScope
    fun stepExecutionListener(): StepExecutionListener {
        return object : StepExecutionListener {
            override fun beforeStep(stepExecution: StepExecution) {
                log.info("==> Before Step Status = ${stepExecution.status}");
            }

            override fun afterStep(stepExecution: StepExecution): ExitStatus {
                log.info("==> After Step Status = ${stepExecution.status}");
                log.info("==> Read Count = ${stepExecution.readCount}");
                return stepExecution.exitStatus;
            }
        }
    }

    private fun sendGradingRequest(gradingHistory: GradingHistory): GradingResponseDto {
        val gradingRequestDto = GradingRequestDto.createGradingRequestDto(gradingHistory)
        return this.aiServerClient.getGrade(gradingRequestDto)
    }

    private fun getScore(gradingResponseDto: GradingResponseDto, gradingStandards: List<GradingStandard>): Double {
        val correctIds = gradingResponseDto.getCorrectGradingStandardIds()

        return correctIds.map {
            gradingStandards.find { gs ->
                gs.id == it
            }?.score ?: 0.0
        }.takeIf {
            it.isNotEmpty()
        }?.reduce { tot, cur ->
            tot + cur
        } ?: 0.0
    }
}
