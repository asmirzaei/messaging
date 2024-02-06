package com.visable.messaging.controller.dto.request

import com.visable.messaging.consts.ID
import com.visable.messaging.consts.UNIQUE_NICKNAME_1
import com.visable.messaging.consts.UNIQUE_NICKNAME_2
import com.visable.messaging.fixtures.dummyMessage
import com.visable.messaging.fixtures.dummyUser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID.randomUUID

class MessageRequestDTOTest {
    @Test
    fun `maps messageRequestDTO to message`() {
        // given
        val senderUser = dummyUser(nickName = UNIQUE_NICKNAME_1)
        val receiverUser = dummyUser(nickName = UNIQUE_NICKNAME_2)
        val messageRequestDTO = MessageRequestDTO(message = randomUUID().toString(), receiverUserId = receiverUser.id)

        // when
        val message = messageRequestDTO.toMessage(senderUser = senderUser, receiverUser = receiverUser)

        // then
        val expectedMessage = dummyMessage(message = messageRequestDTO.message, senderUser = senderUser, receiverUser = receiverUser)
        assertThat(message).usingRecursiveComparison().ignoringFields(ID, "receivedDateTime").isEqualTo(expectedMessage)
    }
}
