package io.csbroker.batchserver.repository

import io.csbroker.batchserver.entity.GradingHistory
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.nio.ByteBuffer
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.UUID

@Repository
class NotificationRepository(
    private val jdbcTemplate: JdbcTemplate
) {
    fun insertBulkNotifications(gradingHistories: List<GradingHistory>) {
        val sql = """
            INSERT INTO notification
            (content, link, is_read, user_id, created_at, updated_at)
            VALUES
            (?, ?, ?, ?, ?, ?)
        """.trimIndent()

        jdbcTemplate.batchUpdate(
            sql,
            object : BatchPreparedStatementSetter {
                override fun setValues(ps: PreparedStatement, i: Int) {
                    val gradingHistory = gradingHistories[i]
                    ps.setString(1, "${gradingHistory.problem.id}번 문제가 재채점 되었습니다.")
                    ps.setString(2, "https://csbroker.io/problem/long/${gradingHistory.problem.id}")
                    ps.setBoolean(3, false)
                    ps.setBytes(4, uuidToByte(gradingHistory.userId))
                    ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()))
                    ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()))
                }

                override fun getBatchSize(): Int {
                    return gradingHistories.size
                }
            }
        )
    }


    private fun uuidToByte(uuid: UUID?): ByteArray? {
        if (uuid == null) {
            return null
        }
        val byteBufferWrapper = ByteBuffer.wrap(ByteArray(16))
        byteBufferWrapper.putLong(uuid.mostSignificantBits)
        byteBufferWrapper.putLong(uuid.leastSignificantBits)
        return byteBufferWrapper.array()
    }
}
