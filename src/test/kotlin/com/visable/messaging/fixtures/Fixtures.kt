package com.visable.messaging.fixtures

import com.visable.messaging.consts.UNIQUE_NICKNAME_1
import com.visable.messaging.consts.UNIQUE_NICKNAME_2
import com.visable.messaging.controller.dto.request.MessageRequestDTO
import com.visable.messaging.controller.dto.request.UserRequestDTO
import com.visable.messaging.controller.dto.response.MessageResponseDTO
import com.visable.messaging.controller.dto.response.UserResponseDTO
import com.visable.messaging.domain.Message
import com.visable.messaging.domain.User
import java.time.Instant
import java.util.UUID.randomUUID

fun dummyUser(id: String = randomUUID().toString(), nickName: String = "a unique nickName!") = User(id = id, nickName = nickName)

fun dummyMessage(
    id: String = randomUUID().toString(),
    message: String = randomUUID().toString(),
    senderUser: User = dummyUser(UNIQUE_NICKNAME_1),
    receiverUser: User = dummyUser(UNIQUE_NICKNAME_2),
    receivedDateTime: Instant = Instant.now(),
) = Message(id = id, message = message, senderUser = senderUser, receiverUser = receiverUser, receivedDateTime = receivedDateTime)

fun dummyMessageRequestDTO(message: String = randomUUID().toString(), receiverUserId: String = randomUUID().toString()) =
    MessageRequestDTO(message = message, receiverUserId = receiverUserId)

fun dummyMessageResponseDTO(
    id: String = randomUUID().toString(),
    message: String = randomUUID().toString(),
    senderUserId: String = randomUUID().toString(),
    receiverUserId: String = randomUUID().toString(),
    receivedDateTime: Instant = Instant.now(),
) = MessageResponseDTO(
    id = id,
    message = message,
    senderUserId = senderUserId,
    receiverUserId = receiverUserId,
    receivedDateTime = receivedDateTime,
)

fun dummyUserRequestDTO(nickName: String = UNIQUE_NICKNAME_1) = UserRequestDTO(nickName = nickName)

fun dummyUserResponseDTO(id: String = randomUUID().toString(), nickName: String = UNIQUE_NICKNAME_1) = UserResponseDTO(
    id = id,
    nickName = nickName,
)
