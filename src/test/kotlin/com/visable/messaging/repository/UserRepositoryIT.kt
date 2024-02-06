package com.visable.messaging.repository

import com.visable.messaging.IntegrationTestParent
import com.visable.messaging.consts.UNIQUE_NICKNAME_1
import com.visable.messaging.consts.UNIQUE_NICKNAME_2
import com.visable.messaging.fixtures.dummyUser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException

class UserRepositoryIT(
    @Autowired private val userRepository: UserRepository,
) : IntegrationTestParent() {
    @Test
    fun `returns exception in case of trying to save a new user with an existing nickName`() {
        // given
        val user1 = dummyUser(nickName = UNIQUE_NICKNAME_1)
        userRepository.save(user1)
        val user2 = dummyUser(nickName = UNIQUE_NICKNAME_1)

        // when then
        assertThrows<DataIntegrityViolationException> { userRepository.save(user2) }
    }

    @Test
    fun `finds a user by its nickName`() {
        // given
        val user1 = dummyUser(nickName = UNIQUE_NICKNAME_1)
        val user2 = dummyUser(nickName = UNIQUE_NICKNAME_2)
        userRepository.saveAll(listOf(user1, user2))

        // when
        val foundUser = userRepository.findUserByNickName(UNIQUE_NICKNAME_2)

        // then
        assertThat(foundUser).isEqualTo(user2)
    }
}
