package com.visable.messaging.controller.dto.request

import com.visable.messaging.domain.User
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.UUID.randomUUID

data class UserRequestDTO(
    @field:NotBlank(message = "The nickName shouldn't be blank.")
    @field:Size(max = 50, message = "The nickName shouldn't be more than 50 characters.")
    val nickName: String,
)

fun UserRequestDTO.toUser() = User(id = randomUUID().toString(), nickName)
