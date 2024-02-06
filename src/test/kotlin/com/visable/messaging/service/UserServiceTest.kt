package com.visable.messaging.service

import com.visable.messaging.consts.UNIQUE_NICKNAME_1
import com.visable.messaging.controller.dto.request.UserRequestDTO
import com.visable.messaging.controller.dto.response.UserResponseDTO
import com.visable.messaging.exception.UserNicknameUniquenessException
import com.visable.messaging.fixtures.dummyUser
import com.visable.messaging.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional
import java.util.UUID.randomUUID

class UserServiceTest {
    private val userRepository: UserRepository = mockk(relaxed = true)
    private val userService = UserService(userRepository = userRepository)

    @Test
    fun `saves new user in DB if the nickName is not exist in the DB`() {
        // given
        every { userRepository.findUserByNickName(UNIQUE_NICKNAME_1) } returns null
        val userRequestDTO = UserRequestDTO(nickName = UNIQUE_NICKNAME_1)
        val dummyUser = dummyUser(nickName = UNIQUE_NICKNAME_1)
        every { userRepository.save(any()) } returns dummyUser

        // when
        val result = userService.createUser(userRequestDTO)

        // then
        verify(exactly = 1) { userRepository.findUserByNickName(UNIQUE_NICKNAME_1) }
        verify(exactly = 1) { userRepository.save(any()) }
        val expectedResult = UserResponseDTO(id = dummyUser.id, nickName = dummyUser.nickName)
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `throws UserNicknameUniquenessException when saves new user in DB that the nickName is exist in the DB`() {
        // given
        every { userRepository.findUserByNickName(UNIQUE_NICKNAME_1) } returns dummyUser(nickName = UNIQUE_NICKNAME_1)
        val userRequestDTO = UserRequestDTO(nickName = UNIQUE_NICKNAME_1)

        // when  then
        assertThrows<UserNicknameUniquenessException> { userService.createUser(userRequestDTO) }
    }

    @Test
    fun `returns true if the given userId is exist in the DB`() {
        // given
        val userId = randomUUID().toString()
        every { userRepository.findById(userId) } returns Optional.of(dummyUser())

        // when
        val result = userService.isUserIdValid(userId)

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `returns false if the given userId is not exist in the DB`() {
        // given
        val userId = randomUUID().toString()
        every { userRepository.findById(userId) } returns Optional.ofNullable(null)

        // when
        val result = userService.isUserIdValid(userId)

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `returns the user if the given userId is exist in the DB`() {
        // given
        val user = dummyUser()
        every { userRepository.findById(user.id) } returns Optional.of(user)

        // when
        val result = userService.findUser(user.id)

        // then
        assertThat(result).isEqualTo(user)
    }

    @Test
    fun `throws exception if the given userId is not exist in the DB`() {
        // given
        val userId = randomUUID().toString()
        every { userRepository.findById(userId) } returns Optional.ofNullable(null)

        // when then
        assertThrows<NoSuchElementException> { userService.findUser(userId) }
    }
}
