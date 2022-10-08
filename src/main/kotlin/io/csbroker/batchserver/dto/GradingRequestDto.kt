package io.csbroker.batchserver.dto

import io.csbroker.batchserver.entity.GradingHistory
import io.csbroker.batchserver.enum.GradingStandardType

data class GradingRequestDto(
    val problem_id: Long,
    val user_answer: String,
    val keyword_standards: List<GradingKeyword>,
    val content_standards: List<GradingPrompt>
) {
    data class GradingPrompt(
        val id: Long,
        val content: String
    )

    data class GradingKeyword(
        val id: Long,
        val content: String
    )

    companion object {
        fun createGradingRequestDto(
            gradingHistory: GradingHistory
        ): GradingRequestDto {
            return GradingRequestDto(
                gradingHistory.problem.id,
                gradingHistory.userAnswer,
                gradingHistory.problem.gradingStandards.filter {
                    it.type == GradingStandardType.KEYWORD
                }.map {
                    GradingKeyword(
                        it.id,
                        it.content
                    )
                },
                gradingHistory.problem.gradingStandards.filter {
                    it.type == GradingStandardType.CONTENT
                }.map {
                    GradingPrompt(
                        it.id,
                        it.content
                    )
                }
            )
        }
    }
}
