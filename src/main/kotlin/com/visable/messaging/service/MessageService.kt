package com.visable.messaging.service

import com.visable.messaging.controller.dto.request.MessageRequestDTO
import com.visable.messaging.controller.dto.request.toMessage
import com.visable.messaging.controller.dto.response.MessageResponseDTO
import com.visable.messaging.controller.dto.response.MessageResponseListDTO
import com.visable.messaging.controller.dto.response.toMessageResponseDTO
import com.visable.messaging.controller.dto.response.toMessageResponseListDTO
import com.visable.messaging.exception.MessageValidationException
import com.visable.messaging.repository.MessageRepository
import mu.KotlinLogging.logger
import org.springframework.stereotype.Service

@Service
class MessageService(
    private val messageRepository: MessageRepository,
    private val userService: UserService,
) {
    fun saveMessage(senderUserId: String, messageRequestDTO: MessageRequestDTO): MessageResponseDTO {
        validateMessageRequestDTO(senderUserId = senderUserId, messageRequestDTO = messageRequestDTO)
        val messageToSave =
            messageRequestDTO.toMessage(
                senderUser = userService.findUser(senderUserId),
                receiverUser = userService.findUser(messageRequestDTO.receiverUserId),
            )
        val savedMessage = messageRepository.save(messageToSave)
        logger.info { "a new message saved to the DB = $savedMessage" }
        return savedMessage.toMessageResponseDTO()
    }

    fun getReceivedMessages(receiverUserId: String): MessageResponseListDTO {
        validateReceiverUserId(receiverUserId)
        return messageRepository.findMessagesByReceiverUserId(receiverUserId).toMessageResponseListDTO()
    }

    fun getSentMessages(senderUserId: String): MessageResponseListDTO {
        validateSenderUserId(senderUserId)
        return messageRepository.findMessagesBySenderUserId(senderUserId).toMessageResponseListDTO()
    }

    fun getReceivedMessagesFrom(receiverUserId: String, senderUserId: String): MessageResponseListDTO {
        validateSenderUserId(senderUserId)
        validateReceiverUserId(receiverUserId)
        return messageRepository.findMessagesByReceiverUserIdAndSenderUserId(receiverUserId = receiverUserId, senderUserId = senderUserId)
            .toMessageResponseListDTO()
    }

    private fun validateMessageRequestDTO(senderUserId: String, messageRequestDTO: MessageRequestDTO) {
        senderAndReceiverUserShouldNotBeSame(senderUserId = senderUserId, receiverUserId = messageRequestDTO.receiverUserId)
        validateSenderUserId(senderUserId)
        validateReceiverUserId(messageRequestDTO.receiverUserId)
    }

    private fun senderAndReceiverUserShouldNotBeSame(senderUserId: String, receiverUserId: String) {
        if (senderUserId == receiverUserId) {
            throw MessageValidationException(MESSAGE_SENDER_AND_RECEIVER_USER_SAME_ERROR)
        }
    }

    private fun validateSenderUserId(senderUserId: String) {
        if (!userService.isUserIdValid(senderUserId)) {
            throw MessageValidationException(MESSAGE_SENDER_USER_NOT_FOUND_ERROR)
        }
    }

    private fun validateReceiverUserId(receiverUserId: String) {
        if (!userService.isUserIdValid(receiverUserId)) {
            throw MessageValidationException(MESSAGE_RECEIVER_USER_NOT_FOUND_ERROR)
        }
    }

    companion object {
        private const val MESSAGE_SENDER_AND_RECEIVER_USER_SAME_ERROR = "The sender userId and the receiver userId shouldn't be same."
        private const val MESSAGE_SENDER_USER_NOT_FOUND_ERROR = "The sender userId is not found."
        private const val MESSAGE_RECEIVER_USER_NOT_FOUND_ERROR = "The receiver userId is not found."
        private val logger = logger {}
    }
}
