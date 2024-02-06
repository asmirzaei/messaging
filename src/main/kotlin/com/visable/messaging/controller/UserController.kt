package com.visable.messaging.controller

import com.visable.messaging.controller.UserController.Companion.USER_ENDPOINT
import com.visable.messaging.controller.dto.request.UserRequestDTO
import com.visable.messaging.controller.dto.response.UserResponseDTO
import com.visable.messaging.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(USER_ENDPOINT)
@Validated
class UserController(
    private val userService: UserService,
) {
    @Operation(description = "Creates a new user.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "User is created."),
            ApiResponse(responseCode = "400", description = "Invalid request property", content = [Content()]),
            ApiResponse(responseCode = "409", description = "User nickName is already exists in the DB.", content = [Content()]),
        ],
    )
    @PostMapping(produces = [APPLICATION_JSON_VALUE])
    fun createUser(
        @RequestBody @Valid
        userRequestDTO: UserRequestDTO,
    ): ResponseEntity<UserResponseDTO> {
        val createdUser = userService.createUser(userRequestDTO)
        return ResponseEntity(createdUser, CREATED)
    }

    companion object {
        const val USER_ENDPOINT = "/api/v1/users"
    }
}
