package com.visable.messaging.service

import com.visable.messaging.controller.dto.request.UserRequestDTO
import com.visable.messaging.controller.dto.request.toUser
import com.visable.messaging.controller.dto.response.UserResponseDTO
import com.visable.messaging.controller.dto.response.toUserResponseDTO
import com.visable.messaging.domain.User
import com.visable.messaging.exception.UserNicknameUniquenessException
import com.visable.messaging.repository.UserRepository
import mu.KotlinLogging.logger
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {
    fun createUser(userRequestDTO: UserRequestDTO): UserResponseDTO {
        validateUserRequestDTO(userRequestDTO)
        val savedUser = userRepository.save(userRequestDTO.toUser())
        logger.info { "A new user is created = $savedUser" }
        return savedUser.toUserResponseDTO()
    }

    fun isUserIdValid(userId: String): Boolean {
        val user = userRepository.findById(userId)
        return user.isPresent
    }

    fun findUser(userId: String): User {
        val user = userRepository.findById(userId)
        return user.orElseThrow()
    }

    private fun validateUserRequestDTO(userRequestDTO: UserRequestDTO) {
        val existingUser = userRepository.findUserByNickName(userRequestDTO.nickName)
        existingUser?.let {
            logger.info { "Invalid UserRequestDTO = $userRequestDTO with non-unique nickName received!" }
            throw UserNicknameUniquenessException()
        }
    }

    companion object {
        private val logger = logger {}
    }
}
