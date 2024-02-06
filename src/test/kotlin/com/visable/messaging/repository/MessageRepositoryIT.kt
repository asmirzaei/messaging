package com.visable.messaging.repository

import com.visable.messaging.IntegrationTestParent
import com.visable.messaging.consts.UNIQUE_NICKNAME_1
import com.visable.messaging.consts.UNIQUE_NICKNAME_2
import com.visable.messaging.consts.UNIQUE_NICKNAME_3
import com.visable.messaging.domain.Message
import com.visable.messaging.domain.User
import com.visable.messaging.fixtures.dummyMessage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID.randomUUID

class MessageRepositoryIT(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val messageRepository: MessageRepository,
) : IntegrationTestParent() {
    @Test
    fun `returns all the messages received by the given userId`() {
        // given
        val users = prepareUsers()
        val messages = prepareMessages(users)

        // when
        val receivedMessages = messageRepository.findMessagesByReceiverUserId(users[1].id)

        // then
        val expectedMessages = listOf(messages[0], messages[1], messages[10], messages[11])
        assertThat(receivedMessages).containsExactlyInAnyOrderElementsOf(expectedMessages)
    }

    @Test
    fun `returns all the messages sent by the given userId`() {
        // given
        val users = prepareUsers()
        val messages = prepareMessages(users)

        // when
        val receivedMessages = messageRepository.findMessagesBySenderUserId(users[1].id)

        // then
        val expectedMessages = listOf(messages[4], messages[5], messages[6], messages[7])
        assertThat(receivedMessages).containsExactlyInAnyOrderElementsOf(expectedMessages)
    }

    @Test
    fun `returns all the messages that a given userId received from an other given userId`() {
        // given
        val users = prepareUsers()
        val messages = prepareMessages(users)

        // when
        val receivedMessages =
            messageRepository.findMessagesByReceiverUserIdAndSenderUserId(
                receiverUserId = users[0].id,
                senderUserId = users[2].id,
            )

        // then
        val expectedMessages = listOf(messages[8], messages[9])
        assertThat(receivedMessages).containsExactlyInAnyOrderElementsOf(expectedMessages)
    }

    private fun prepareMessages(users: List<User>): List<Message> {
        val messages =
            listOf(
                dummyMessage(senderUser = users[0], receiverUser = users[1]),
                dummyMessage(senderUser = users[0], receiverUser = users[1]),
                dummyMessage(senderUser = users[0], receiverUser = users[2]),
                dummyMessage(senderUser = users[0], receiverUser = users[2]),
                dummyMessage(senderUser = users[1], receiverUser = users[0]),
                dummyMessage(senderUser = users[1], receiverUser = users[0]),
                dummyMessage(senderUser = users[1], receiverUser = users[2]),
                dummyMessage(senderUser = users[1], receiverUser = users[2]),
                dummyMessage(senderUser = users[2], receiverUser = users[0]),
                dummyMessage(senderUser = users[2], receiverUser = users[0]),
                dummyMessage(senderUser = users[2], receiverUser = users[1]),
                dummyMessage(senderUser = users[2], receiverUser = users[1]),
            )
        messageRepository.saveAll(messages)
        return messages
    }

    private fun prepareUsers(): List<User> {
        val users =
            listOf(
                User(id = randomUUID().toString(), nickName = UNIQUE_NICKNAME_1),
                User(id = randomUUID().toString(), nickName = UNIQUE_NICKNAME_2),
                User(id = randomUUID().toString(), nickName = UNIQUE_NICKNAME_3),
            )
        userRepository.saveAll(users)
        return users
    }
}
