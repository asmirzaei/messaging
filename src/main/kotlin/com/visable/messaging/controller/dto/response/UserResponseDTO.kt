package com.visable.messaging.controller.dto.response

import com.visable.messaging.domain.User

data class UserResponseDTO(
    val id: String,
    val nickName: String,
)

fun User.toUserResponseDTO() = UserResponseDTO(id = id, nickName = nickName)
