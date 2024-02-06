package com.visable.messaging.controller.dto.request

import com.visable.messaging.consts.ID
import com.visable.messaging.consts.UNIQUE_NICKNAME_1
import com.visable.messaging.fixtures.dummyUser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserRequestDTOTest {
    @Test
    fun toUser() {
        // given
        val userRequestDTO = UserRequestDTO(nickName = UNIQUE_NICKNAME_1)

        // when
        val user = userRequestDTO.toUser()

        // then
        val expectedUser = dummyUser(nickName = UNIQUE_NICKNAME_1)
        assertThat(user).usingRecursiveComparison().ignoringFields(ID).isEqualTo(expectedUser)
    }
}
