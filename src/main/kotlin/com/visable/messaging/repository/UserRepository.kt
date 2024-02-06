package com.visable.messaging.repository

import com.visable.messaging.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, String> {
    fun findUserByNickName(nickName: String): User?
}
