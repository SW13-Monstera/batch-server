package io.csbroker.batchserver.entity

import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "grading_history")
class GradingHistory(
    @Id
    @Column(name = "grading_history_id")
    val gradingHistoryId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    val problem: Problem,

    @Column(name = "user_id")
    val userId: UUID,

    @Column(name = "user_answer", columnDefinition = "VARCHAR(300)")
    val userAnswer: String,

    @Column(name = "score")
    var score: Double,

    @Column(name = "created_at")
    var createdAt: LocalDateTime,

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime
)
