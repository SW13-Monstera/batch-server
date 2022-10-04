package io.csbroker.batchserver.entity

import io.csbroker.batchserver.enum.GradingStandardType
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "grading_standard")
class GradingStandard(
    @Id
    @Column(name = "grading_standard_id")
    val id: Long,

    @Column(name = "content")
    var content: String,

    @Column(name = "score")
    var score: Double,

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    var type: GradingStandardType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    val problem: Problem,

    @Column(name = "created_at")
    var createdAt: LocalDateTime,

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime
)