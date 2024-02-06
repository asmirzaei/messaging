package com.visable.messaging.controller.dto.request

import com.visable.messaging.domain.Message
import com.visable.messaging.domain.User
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.UUID.randomUUID

data class MessageRequestDTO(
    @field:NotBlank(message = "The message shouldn't be blank.")
    @field:Size(max = 3000, message = "The message shouldn't be more than 3000 characters.")
    val message: String,
    @field:NotBlank(message = "The message shouldn't be blank.")
    @field:Size(max = 36, message = "The receiver userId should be 36 characters.")
    @field:Size(min = 36, message = "The receiver userId should be 36 characters.")
    val receiverUserId: String,
)

fun MessageRequestDTO.toMessage(senderUser: User, receiverUser: User) = Message(
    id = randomUUID().toString(),
    message = message,
    senderUser = senderUser,
    receiverUser = receiverUser,
    receivedDateTime = Instant.now(),
)
