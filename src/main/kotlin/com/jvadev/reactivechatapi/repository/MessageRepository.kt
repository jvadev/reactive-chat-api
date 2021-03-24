package com.jvadev.reactivechatapi.repository

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param

interface MessageRepository : CoroutineCrudRepository<Message, String> {

    @Query(
        """
            SELECT * FROM (
            SELECT * FROM MESSAGES
            ORDER BY SENT DESC
            LIMIT 10
        ) AS LIMITED_MESSAGES ORDER BY SENT
    """
    )
    suspend fun findLatest(): List<Message>

    @Query(
        """
        SELECT * FROM (
            SELECT * FROM MESSAGES
            WHERE SENT > (SELECT SENT FROM MESSAGES WHERE ID = :id)
            ORDER BY SENT DESC 
        ) AS MESSAGES_BY_ID ORDER BY SENT
    """
    )
    suspend fun findLatest(@Param("id") id: String): List<Message>
}