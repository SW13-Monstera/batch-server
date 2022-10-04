package io.csbroker.batchserver.entity

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "problem")
class Problem(
    @Id
    @Column(name = "problem_id")
    val id: Long,

    @Column(name = "problem_title")
    var title: String,

    @Column(name = "problem_description")
    var description: String,

    @Column(name = "is_active")
    var isActive: Boolean,

    @Column(name = "is_gradable")
    var isGradable: Boolean,

    @Column(name = "dtype")
    var dtype: String,

    @Column(name = "user_id")
    val userId: UUID,

    @Column(name = "score")
    var score: Double,

    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY)
    val gradingHistories: MutableList<GradingHistory>,

    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY)
    val gradingStandards: MutableList<GradingStandard>
)