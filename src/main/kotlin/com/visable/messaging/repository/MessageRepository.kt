package com.visable.messaging.repository

import com.visable.messaging.domain.Message
import org.springframework.data.jpa.repository.JpaRepository

interface MessageRepository : JpaRepository<Message, String> {
    fun findMessagesByReceiverUserId(receiverUserId: String): List<Message>

    fun findMessagesBySenderUserId(senderUserId: String): List<Message>

    fun findMessagesByReceiverUserIdAndSenderUserId(receiverUserId: String, senderUserId: String): List<Message>
}
