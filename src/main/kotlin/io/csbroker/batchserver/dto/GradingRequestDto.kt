package io.csbroker.batchserver.dto

import io.csbroker.batchserver.entity.GradingStandard
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
            problemId: Long,
            answer: String,
            gradingStandards: List<GradingStandard>
        ): GradingRequestDto {
            return GradingRequestDto(
                problemId,
                answer,
                gradingStandards.filter {
                    it.type == GradingStandardType.KEYWORD
                }.map {
                    GradingKeyword(
                        it.id!!,
                        it.content
                    )
                },
                gradingStandards.filter {
                    it.type == GradingStandardType.PROMPT
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
