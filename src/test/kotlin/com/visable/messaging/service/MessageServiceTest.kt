package com.visable.messaging.service

import com.visable.messaging.consts.ID
import com.visable.messaging.consts.MESSAGE_RECEIVER_USER_NOT_FOUND_ERROR
import com.visable.messaging.consts.MESSAGE_SENDER_AND_RECEIVER_USER_SAME_ERROR
import com.visable.messaging.consts.MESSAGE_SENDER_USER_NOT_FOUND_ERROR
import com.visable.messaging.consts.RECEIVED_DATE_TIME
import com.visable.messaging.controller.dto.response.MessageResponseDTO
import com.visable.messaging.controller.dto.response.MessageResponseListDTO
import com.visable.messaging.domain.Message
import com.visable.messaging.exception.MessageValidationException
import com.visable.messaging.fixtures.dummyMessage
import com.visable.messaging.fixtures.dummyMessageRequestDTO
import com.visable.messaging.fixtures.dummyMessageResponseDTO
import com.visable.messaging.fixtures.dummyUser
import com.visable.messaging.repository.MessageRepository
import com.visable.messaging.repository.UserRepository
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MessageServiceTest {
    private val messageRepository: MessageRepository = mockk(relaxed = true)
    private val userRepository: UserRepository = mockk(relaxed = true)
    private val userService = UserService(userRepository = userRepository)
    private val messageService = MessageService(messageRepository = messageRepository, userService = userService)

    @Test
    fun `saves a new message`() {
        // given
        val senderUser = dummyUser()
        val receiverUser = dummyUser()
        val messageRequestDTO = dummyMessageRequestDTO(receiverUserId = receiverUser.id)
        every { userService.isUserIdValid(senderUser.id) } returns true
        every { userService.isUserIdValid(messageRequestDTO.receiverUserId) } returns true
        every { userService.findUser(senderUser.id) } returns senderUser
        every { userService.findUser(receiverUser.id) } returns receiverUser
        val message = dummyMessage(senderUser = senderUser, receiverUser = receiverUser, message = messageRequestDTO.message)
        every { messageRepository.save(any()) } returns message

        // when
        val result = messageService.saveMessage(senderUserId = senderUser.id, messageRequestDTO = messageRequestDTO)

        // then
        val expectedMessageResponseDTO =
            dummyMessageResponseDTO(
                senderUserId = senderUser.id,
                receiverUserId = receiverUser.id,
                message = messageRequestDTO.message,
            )
        assertThat(result).usingRecursiveComparison().ignoringFields(ID, RECEIVED_DATE_TIME).isEqualTo(expectedMessageResponseDTO)
    }

    @Test
    fun `throws MessageValidationException when trys to save a new message that sender user is same as receiver user`() {
        // given
        val senderUser = dummyUser()
        val messageRequestDTO = dummyMessageRequestDTO(receiverUserId = senderUser.id)
        every { userService.findUser(senderUser.id) } returns senderUser
        val message = dummyMessage(senderUser = senderUser, receiverUser = senderUser, message = messageRequestDTO.message)
        every { messageRepository.save(any()) } returns message

        // when then
        val thrownException =
            assertThrows<MessageValidationException> {
                messageService.saveMessage(senderUserId = senderUser.id, messageRequestDTO = messageRequestDTO)
            }
        assertThat(thrownException.message).isEqualTo(MESSAGE_SENDER_AND_RECEIVER_USER_SAME_ERROR)
        verify { messageRepository.save(any()) wasNot called }
    }

    @Test
    fun `throws MessageValidationException when trys to save a new message that sender user id is not exist in the DB`() {
        // given
        val senderUser = dummyUser()
        val receiverUser = dummyUser()
        val messageRequestDTO = dummyMessageRequestDTO(receiverUserId = receiverUser.id)
        every { userService.isUserIdValid(senderUser.id) } returns false
        every { userService.isUserIdValid(messageRequestDTO.receiverUserId) } returns true
        every { userService.findUser(senderUser.id) } returns senderUser
        every { userService.findUser(receiverUser.id) } returns receiverUser
        val message = dummyMessage(senderUser = senderUser, receiverUser = receiverUser, message = messageRequestDTO.message)
        every { messageRepository.save(any()) } returns message

        // when then
        val thrownException =
            assertThrows<MessageValidationException> {
                messageService.saveMessage(senderUserId = senderUser.id, messageRequestDTO = messageRequestDTO)
            }
        assertThat(thrownException.message).isEqualTo(MESSAGE_SENDER_USER_NOT_FOUND_ERROR)
        verify { messageRepository.save(any()) wasNot called }
    }

    @Test
    fun `throws MessageValidationException when trys to save a new message that receiver user id is not exist in the DB`() {
        // given
        val senderUser = dummyUser()
        val receiverUser = dummyUser()
        val messageRequestDTO = dummyMessageRequestDTO(receiverUserId = receiverUser.id)
        every { userService.isUserIdValid(senderUser.id) } returns true
        every { userService.isUserIdValid(messageRequestDTO.receiverUserId) } returns false
        every { userService.findUser(senderUser.id) } returns senderUser
        every { userService.findUser(receiverUser.id) } returns receiverUser
        val message = dummyMessage(senderUser = senderUser, receiverUser = receiverUser, message = messageRequestDTO.message)
        every { messageRepository.save(any()) } returns message

        // when then
        val thrownException =
            assertThrows<MessageValidationException> {
                messageService.saveMessage(senderUserId = senderUser.id, messageRequestDTO = messageRequestDTO)
            }
        assertThat(thrownException.message).isEqualTo(MESSAGE_RECEIVER_USER_NOT_FOUND_ERROR)
        verify { messageRepository.save(any()) wasNot called }
    }

    @Test
    fun `returns received messages by the given userId`() {
        // given
        val senderUser1 = dummyUser()
        val senderUser2 = dummyUser()
        val receiverUser = dummyUser()
        every { userService.isUserIdValid(receiverUser.id) } returns true
        val messages =
            listOf(
                dummyMessage(senderUser = senderUser1, receiverUser = receiverUser),
                dummyMessage(senderUser = senderUser2, receiverUser = receiverUser),
                dummyMessage(senderUser = senderUser1, receiverUser = receiverUser),
            )
        every { messageRepository.findMessagesByReceiverUserId(receiverUser.id) } returns messages

        // when
        val result = messageService.getReceivedMessages(receiverUser.id)

        // then
        val expectedResult =
            MessageResponseListDTO(
                messages = messages.map { message -> message.toMessageResponseDTO() },
            )
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `throws MessageValidationException when trys to get the messages of the receiver user id which is not exist in the DB`() {
        // given
        val receiverUser = dummyUser()
        every { userService.isUserIdValid(receiverUser.id) } returns false

        // when then
        val thrownException =
            assertThrows<MessageValidationException> {
                messageService.getReceivedMessages(receiverUser.id)
            }
        assertThat(thrownException.message).isEqualTo(MESSAGE_RECEIVER_USER_NOT_FOUND_ERROR)
        verify { messageRepository wasNot called }
    }

    @Test
    fun `returns sent messages by the given userId`() {
        // given
        val senderUser = dummyUser()
        val receiverUser2 = dummyUser()
        val receiverUser1 = dummyUser()
        every { userService.isUserIdValid(senderUser.id) } returns true
        val messages =
            listOf(
                dummyMessage(senderUser = senderUser, receiverUser = receiverUser1),
                dummyMessage(senderUser = senderUser, receiverUser = receiverUser2),
                dummyMessage(senderUser = senderUser, receiverUser = receiverUser1),
            )
        every { messageRepository.findMessagesBySenderUserId(senderUser.id) } returns messages

        // when
        val result = messageService.getSentMessages(senderUser.id)

        // then
        val expectedResult =
            MessageResponseListDTO(
                messages = messages.map { message -> message.toMessageResponseDTO() },
            )
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `throws MessageValidationException when trys to get the messages that sent by the sender user id which is not exist in the DB`() {
        // given
        val senderUser = dummyUser()
        every { userService.isUserIdValid(senderUser.id) } returns false

        // when then
        val thrownException =
            assertThrows<MessageValidationException> {
                messageService.getSentMessages(senderUser.id)
            }
        assertThat(thrownException.message).isEqualTo(MESSAGE_SENDER_USER_NOT_FOUND_ERROR)
        verify { messageRepository wasNot called }
    }

    @Test
    fun `returns the messages that received by a specific user and send by another specific user`() {
        // given
        val senderUser = dummyUser()
        val receiverUser = dummyUser()
        every { userService.isUserIdValid(senderUser.id) } returns true
        every { userService.isUserIdValid(receiverUser.id) } returns true
        val messages =
            listOf(
                dummyMessage(senderUser = senderUser, receiverUser = receiverUser),
                dummyMessage(senderUser = senderUser, receiverUser = receiverUser),
                dummyMessage(senderUser = senderUser, receiverUser = receiverUser),
            )
        every {
            messageRepository.findMessagesByReceiverUserIdAndSenderUserId(senderUserId = senderUser.id, receiverUserId = receiverUser.id)
        } returns messages

        // when
        val result = messageService.getReceivedMessagesFrom(senderUserId = senderUser.id, receiverUserId = receiverUser.id)

        // then
        val expectedResult =
            MessageResponseListDTO(
                messages = messages.map { message -> message.toMessageResponseDTO() },
            )
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `throws MessageValidationException when trys to get the messages that received by a specific user and send by another specific user which the sender user is not exist in the DB`() {
        // given
        val senderUser = dummyUser()
        val receiverUser = dummyUser()
        every { userService.isUserIdValid(senderUser.id) } returns false
        every { userService.isUserIdValid(receiverUser.id) } returns true

        // when then
        val thrownException =
            assertThrows<MessageValidationException> {
                messageService.getReceivedMessagesFrom(senderUserId = senderUser.id, receiverUserId = receiverUser.id)
            }
        assertThat(thrownException.message).isEqualTo(MESSAGE_SENDER_USER_NOT_FOUND_ERROR)
        verify { messageRepository wasNot called }
    }

    @Test
    fun `throws MessageValidationException when trys to get the messages that received by a specific user and send by another specific user which the receiver user is not exist in the DB`() {
        // given
        val senderUser = dummyUser()
        val receiverUser = dummyUser()
        every { userService.isUserIdValid(senderUser.id) } returns true
        every { userService.isUserIdValid(receiverUser.id) } returns false

        // when then
        val thrownException =
            assertThrows<MessageValidationException> {
                messageService.getReceivedMessagesFrom(senderUserId = senderUser.id, receiverUserId = receiverUser.id)
            }
        assertThat(thrownException.message).isEqualTo(MESSAGE_RECEIVER_USER_NOT_FOUND_ERROR)
        verify { messageRepository wasNot called }
    }

    private fun Message.toMessageResponseDTO() = MessageResponseDTO(
        id = id,
        senderUserId = senderUser.id,
        receiverUserId = receiverUser.id,
        message = message,
        receivedDateTime = receivedDateTime,
    )
}
