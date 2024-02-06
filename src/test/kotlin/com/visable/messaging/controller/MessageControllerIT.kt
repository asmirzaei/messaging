package com.visable.messaging.controller

import com.visable.messaging.IntegrationTestParent
import com.visable.messaging.consts.ID
import com.visable.messaging.consts.MESSAGES_ENDPOINT
import com.visable.messaging.consts.RECEIVED_DATE_TIME
import com.visable.messaging.consts.RECEIVED_FROM
import com.visable.messaging.consts.RECEIVER_USER_NOT_FOUND_ERROR
import com.visable.messaging.consts.SENDER_AND_RECEIVER_USER_SAME_ERROR
import com.visable.messaging.consts.SENDER_USER_NOT_FOUND_ERROR
import com.visable.messaging.consts.SENT
import com.visable.messaging.consts.UNIQUE_NICKNAME_1
import com.visable.messaging.consts.UNIQUE_NICKNAME_2
import com.visable.messaging.consts.UNIQUE_NICKNAME_3
import com.visable.messaging.consts.USER_ID
import com.visable.messaging.controller.dto.response.MessageResponseDTO
import com.visable.messaging.controller.dto.response.MessageResponseListDTO
import com.visable.messaging.controller.dto.response.toMessageResponseListDTO
import com.visable.messaging.domain.Message
import com.visable.messaging.domain.User
import com.visable.messaging.fixtures.dummyMessage
import com.visable.messaging.fixtures.dummyMessageRequestDTO
import com.visable.messaging.fixtures.dummyMessageResponseDTO
import com.visable.messaging.repository.MessageRepository
import com.visable.messaging.repository.UserRepository
import io.restassured.RestAssured
import io.restassured.http.ContentType.JSON
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import java.util.UUID.randomUUID

class MessageControllerIT(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val messageRepository: MessageRepository,
) : IntegrationTestParent() {
    @Test
    fun `a user sends a message to another user`() {
        // given
        val users = prepareUsers()
        val messageRequestDTO = dummyMessageRequestDTO(receiverUserId = users[1].id)

        // when
        val sentMessage =
            RestAssured.given()
                .contentType(JSON)
                .`when`()
                .header(USER_ID, users[0].id)
                .body(objectMapper.writeValueAsString(messageRequestDTO))
                .post(MESSAGES_ENDPOINT)
                .then()
                .log().ifValidationFails()
                .statusCode(CREATED.value())
                .extract()
                .body()
                .`as`(MessageResponseDTO::class.java)

        // then
        val expectedMessageResponseDTO =
            dummyMessageResponseDTO(
                message = messageRequestDTO.message,
                senderUserId = users[0].id,
                receiverUserId = users[1].id,
            )
        assertThat(sentMessage).usingRecursiveComparison().ignoringFields(ID, RECEIVED_DATE_TIME).isEqualTo(expectedMessageResponseDTO)
        val savedMessages = messageRepository.findAll()
        assertThat(savedMessages).hasSize(1)
        val expectedMessage =
            dummyMessage(
                message = messageRequestDTO.message,
                senderUser = users[0],
                receiverUser = users[1],
            )
        assertThat(savedMessages.first()).usingRecursiveComparison().ignoringFields(ID, RECEIVED_DATE_TIME).isEqualTo(expectedMessage)
    }

    @Test
    fun `returns 409(CONFLICT) when a user trys to send a message to itself`() {
        // given
        val users = prepareUsers()
        val messageRequestDTO = dummyMessageRequestDTO(receiverUserId = users[0].id)

        // when
        val sentMessageResponse =
            RestAssured.given()
                .contentType(JSON)
                .`when`()
                .header(USER_ID, users[0].id)
                .body(objectMapper.writeValueAsString(messageRequestDTO))
                .post(MESSAGES_ENDPOINT)
                .then()
                .log().ifValidationFails()
                .statusCode(CONFLICT.value())
                .extract()
                .asString()

        // then
        assertThat(sentMessageResponse).isEqualTo(SENDER_AND_RECEIVER_USER_SAME_ERROR)
    }

    @Test
    fun `returns 409(CONFLICT) when a non-exist userId trys to send a message`() {
        // given
        val users = prepareUsers()
        val messageRequestDTO = dummyMessageRequestDTO(receiverUserId = users[0].id)

        // when
        val sentMessageResponse =
            RestAssured.given()
                .contentType(JSON)
                .`when`()
                .header(USER_ID, randomUUID().toString())
                .body(objectMapper.writeValueAsString(messageRequestDTO))
                .post(MESSAGES_ENDPOINT)
                .then()
                .log().ifValidationFails()
                .statusCode(CONFLICT.value())
                .extract()
                .asString()

        // then
        assertThat(sentMessageResponse).isEqualTo(SENDER_USER_NOT_FOUND_ERROR)
    }

    @Test
    fun `returns 409(CONFLICT) when a user trys to send a message to a non-exist userId`() {
        // given
        val users = prepareUsers()
        val messageRequestDTO = dummyMessageRequestDTO(receiverUserId = randomUUID().toString())

        // when
        val sentMessageResponse =
            RestAssured.given()
                .contentType(JSON)
                .`when`()
                .header(USER_ID, users[0].id)
                .body(objectMapper.writeValueAsString(messageRequestDTO))
                .post(MESSAGES_ENDPOINT)
                .then()
                .log().ifValidationFails()
                .statusCode(CONFLICT.value())
                .extract()
                .asString()

        // then
        assertThat(sentMessageResponse).isEqualTo(RECEIVER_USER_NOT_FOUND_ERROR)
    }

    @Test
    fun `returns 400(BAD_REQUEST) in case of trying to send a message with invalid messageRequestDTO`() {
        // given
        val users = prepareUsers()
        val messageRequestDTO = "{\"invalid_message_field_name\":\"cdb4a36c-9566-4ec2-a600-13d42c4edd67\",\"receiverUserId\":\"6b196a75-a4d2-4c5c-9b4e-cd9bf9587879\"}"

        // when
        RestAssured.given()
            .contentType(JSON)
            .`when`()
            .header(USER_ID, users[0].id)
            .body(messageRequestDTO)
            .post(MESSAGES_ENDPOINT)
            .then()
            .log().ifValidationFails()
            .statusCode(BAD_REQUEST.value())
    }

    @Test
    fun `returns all messages that a user received`() {
        // given
        val users = prepareUsers()
        val messages = prepareMessages(users)

        // when
        val receivedMessages =
            RestAssured.given()
                .contentType(JSON)
                .`when`()
                .header(USER_ID, users[0].id)
                .get(MESSAGES_ENDPOINT)
                .then()
                .log().ifValidationFails()
                .statusCode(OK.value())
                .extract()
                .body()
                .`as`(MessageResponseListDTO::class.java)

        // then
        val expectedMessages = listOf(messages[4], messages[5], messages[8], messages[9])
        val expectedMessageResponseListDTO = expectedMessages.toMessageResponseListDTO()
        assertThat(receivedMessages).isEqualTo(expectedMessageResponseListDTO)
    }

    @Test
    fun `returns 409(CONFLICT) when a non-exist userId trys to get all messages that the user received`() {
        // given
        val users = prepareUsers()
        prepareMessages(users)

        // when
        val receivedMessagesResponse =
            RestAssured.given()
                .contentType(JSON)
                .`when`()
                .header(USER_ID, randomUUID().toString())
                .get(MESSAGES_ENDPOINT)
                .then()
                .log().ifValidationFails()
                .statusCode(CONFLICT.value())
                .extract()
                .asString()

        // then
        assertThat(receivedMessagesResponse).isEqualTo(RECEIVER_USER_NOT_FOUND_ERROR)
    }

    @Test
    fun `returns all messages that a user sent`() {
        // given
        val users = prepareUsers()
        val messages = prepareMessages(users)

        // when
        val sentMessages =
            RestAssured.given()
                .contentType(JSON)
                .`when`()
                .header(USER_ID, users[0].id)
                .get("$MESSAGES_ENDPOINT$SENT")
                .then()
                .log().ifValidationFails()
                .statusCode(OK.value())
                .extract()
                .body()
                .`as`(MessageResponseListDTO::class.java)

        // then
        val expectedMessages = listOf(messages[0], messages[1], messages[2], messages[3])
        val expectedMessageResponseListDTO = expectedMessages.toMessageResponseListDTO()
        assertThat(sentMessages).isEqualTo(expectedMessageResponseListDTO)
    }

    @Test
    fun `returns 409(CONFLICT) when a non-exist userId trys to get all messages that the user sent`() {
        // given
        val users = prepareUsers()
        prepareMessages(users)

        // when
        val receivedMessagesResponse =
            RestAssured.given()
                .contentType(JSON)
                .`when`()
                .header(USER_ID, randomUUID().toString())
                .get("$MESSAGES_ENDPOINT$SENT")
                .then()
                .log().ifValidationFails()
                .statusCode(CONFLICT.value())
                .extract()
                .asString()

        // then
        assertThat(receivedMessagesResponse).isEqualTo(SENDER_USER_NOT_FOUND_ERROR)
    }

    @Test
    fun `returns all the messages that a userId received from an other given userId`() {
        // given
        val users = prepareUsers()
        val messages = prepareMessages(users)

        // when
        val receivedMessages =
            RestAssured.given()
                .contentType(JSON)
                .`when`()
                .header(USER_ID, users[0].id)
                .get("$MESSAGES_ENDPOINT$RECEIVED_FROM/${users[1].id}")
                .then()
                .log().ifValidationFails()
                .statusCode(OK.value())
                .extract()
                .body()
                .`as`(MessageResponseListDTO::class.java)

        // then
        val expectedMessages = listOf(messages[4], messages[5])
        val expectedMessageResponseListDTO = expectedMessages.toMessageResponseListDTO()
        assertThat(receivedMessages).isEqualTo(expectedMessageResponseListDTO)
    }

    @Test
    fun `returns 409(CONFLICT) when a non-exist userId trys to get all messages that the user received from a specific user`() {
        // given
        val users = prepareUsers()
        prepareMessages(users)

        // when
        val receivedMessagesResponse =
            RestAssured.given()
                .contentType(JSON)
                .`when`()
                .header(USER_ID, randomUUID().toString())
                .get("$MESSAGES_ENDPOINT$RECEIVED_FROM/${users[0].id}")
                .then()
                .log().ifValidationFails()
                .statusCode(CONFLICT.value())
                .extract()
                .asString()

        // then
        assertThat(receivedMessagesResponse).isEqualTo(RECEIVER_USER_NOT_FOUND_ERROR)
    }

    @Test
    fun `returns 409(CONFLICT) when a user trys to get all messages that the user received from a specific non-existing user`() {
        // given
        val users = prepareUsers()
        prepareMessages(users)

        // when
        val receivedMessagesResponse =
            RestAssured.given()
                .contentType(JSON)
                .`when`()
                .header(USER_ID, users[0].id)
                .get("$MESSAGES_ENDPOINT$RECEIVED_FROM/${randomUUID()}")
                .then()
                .log().ifValidationFails()
                .statusCode(CONFLICT.value())
                .extract()
                .asString()

        // then
        assertThat(receivedMessagesResponse).isEqualTo(SENDER_USER_NOT_FOUND_ERROR)
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
