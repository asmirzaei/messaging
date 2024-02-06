package com.visable.messaging.exception

class UserNicknameUniquenessException(
    override val message: String = "The nickName is already exist. Please enter another one.",
) : Exception(message)
