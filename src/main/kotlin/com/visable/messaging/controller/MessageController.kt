package com.visable.messaging.controller

import com.visable.messaging.controller.MessageController.Companion.MESSAGE_ENDPOINT
import com.visable.messaging.controller.dto.request.MessageRequestDTO
import com.visable.messaging.controller.dto.response.MessageResponseDTO
import com.visable.messaging.controller.dto.response.MessageResponseListDTO
import com.visable.messaging.service.MessageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(MESSAGE_ENDPOINT)
@Validated
class MessageController(
    private val messageService: MessageService,
) {
    @Operation(description = "Sends a new message.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "User is created."),
            ApiResponse(responseCode = "400", description = "Invalid request property", content = [Content()]),
            ApiResponse(responseCode = "409", description = "User nickName is already exists in the DB.", content = [Content()]),
        ],
    )
    @PostMapping(produces = [APPLICATION_JSON_VALUE])
    @ResponseStatus(code = CREATED)
    fun createUser(
        @RequestHeader(name = USER_ID) userId: String,
        @RequestBody @Valid
        messageRequestDTO: MessageRequestDTO,
    ): MessageResponseDTO {
        return messageService.saveMessage(userId, messageRequestDTO)
    }

    @Operation(description = "Returns all the messages that the user has received.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User is created."),
            ApiResponse(responseCode = "400", description = "Invalid request property", content = [Content()]),
        ],
    )
    @GetMapping
    @ResponseStatus(code = OK)
    fun getReceivedMessages(@RequestHeader(name = USER_ID) userId: String): MessageResponseListDTO {
        return messageService.getReceivedMessages(userId)
    }

    @Operation(description = "Returns all the messages that the user sent.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User is created."),
            ApiResponse(responseCode = "400", description = "Invalid request property", content = [Content()]),
        ],
    )
    @GetMapping(SENT)
    @ResponseStatus(code = OK)
    fun getSentMessages(@RequestHeader(name = USER_ID) userId: String): MessageResponseListDTO {
        return messageService.getSentMessages(userId)
    }

    @Operation(description = "Returns all the messages that the user has received from a specific user.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User is created."),
            ApiResponse(responseCode = "400", description = "Invalid request property", content = [Content()]),
        ],
    )
    @GetMapping("$RECEIVED_FROM/{senderUserId}")
    @ResponseStatus(code = OK)
    fun getReceivedMessagesFrom(@RequestHeader(name = USER_ID) userId: String, @PathVariable senderUserId: String): MessageResponseListDTO {
        return messageService.getReceivedMessagesFrom(receiverUserId = userId, senderUserId = senderUserId)
    }

    companion object {
        const val MESSAGE_ENDPOINT = "/api/v1/messages"
        const val USER_ID = "UserId"
        const val SENT = "/sent"
        const val RECEIVED_FROM = "/receivedFrom"
    }
}
