package com.visable.messaging.controller.dto.response

import com.visable.messaging.domain.Message
import java.time.Instant

data class MessageResponseDTO(
    val id: String,
    val message: String,
    val senderUserId: String,
    val receiverUserId: String,
    val receivedDateTime: Instant,
)

data class MessageResponseListDTO(
    val messages: List<MessageResponseDTO>,
)

fun List<Message>.toMessageResponseListDTO() = MessageResponseListDTO(messages = map { it.toMessageResponseDTO() })

fun Message.toMessageResponseDTO() = MessageResponseDTO(
    id = id,
    message = message,
    senderUserId = senderUser.id,
    receiverUserId = receiverUser.id,
    receivedDateTime = receivedDateTime,
)
