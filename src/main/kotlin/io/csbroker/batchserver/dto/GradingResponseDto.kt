package io.csbroker.batchserver.dto

data class GradingResponseDto(
    val problem_id: Long,
    val correct_keywords: List<CorrectKeyword>,
    val correct_contents: List<CorrectContent>
) {
    data class CorrectKeyword(
        val id: Long,
        val keyword: String,
        val predict_keyword_position: List<Int>,
        val predict_keyword: String
    )

    data class CorrectContent(
        val id: Long,
        val content: String
    )

    fun getCorrectGradingStandardIds(): List<Long> {
        return correct_contents.map { it.id } + correct_keywords.map { it.id }
    }
}
