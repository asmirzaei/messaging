package com.visable.messaging.controller

import com.visable.messaging.IntegrationTestParent
import com.visable.messaging.consts.ID
import com.visable.messaging.consts.NICKNAME_EXISTS_ERROR
import com.visable.messaging.consts.UNIQUE_NICKNAME_1
import com.visable.messaging.consts.USERS_ENDPOINT
import com.visable.messaging.controller.dto.response.UserResponseDTO
import com.visable.messaging.fixtures.dummyUser
import com.visable.messaging.fixtures.dummyUserRequestDTO
import com.visable.messaging.fixtures.dummyUserResponseDTO
import com.visable.messaging.repository.UserRepository
import io.restassured.RestAssured
import io.restassured.http.ContentType.JSON
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.CREATED

class UserControllerIT(
    @Autowired private val userRepository: UserRepository,
) : IntegrationTestParent() {
    @Test
    fun `creates a new user`() {
        // given
        val userRequestDTO = dummyUserRequestDTO()

        // when
        val createdUser =
            RestAssured.given()
                .contentType(JSON)
                .`when`()
                .body(objectMapper.writeValueAsString(userRequestDTO))
                .post(USERS_ENDPOINT)
                .then()
                .log().ifValidationFails()
                .statusCode(CREATED.value())
                .extract()
                .body()
                .`as`(UserResponseDTO::class.java)

        // then
        val expectedUser = dummyUserResponseDTO()
        assertThat(createdUser).usingRecursiveComparison().ignoringFields(ID).isEqualTo(expectedUser)
    }

    @Test
    fun `returns 409(CONFLICT) in case of trying to create a new user with an existing nickName`() {
        // given
        val user1 = dummyUser(nickName = UNIQUE_NICKNAME_1)
        userRepository.save(user1)
        // given
        val userRequestDTO = dummyUserRequestDTO()

        // when
        val createdUser =
            RestAssured.given()
                .contentType(JSON)
                .`when`()
                .body(objectMapper.writeValueAsString(userRequestDTO))
                .post(USERS_ENDPOINT)
                .then()
                .log().ifValidationFails()
                .statusCode(CONFLICT.value())
                .extract()
                .body()
                .asString()

        // then
        assertThat(createdUser).isEqualTo(NICKNAME_EXISTS_ERROR)
    }

    @Test
    fun `returns 40o(BAD_REQUEST) in case of trying to create a new user with invalid userRequestDTO`() {
        // given
        val user1 = dummyUser(nickName = UNIQUE_NICKNAME_1)
        userRepository.save(user1)
        // given
        val requestString = "{\"invalidParamName\":\"unique nickName 1\"}"

        // when then
        RestAssured.given()
            .contentType(JSON)
            .`when`()
            .body(requestString)
            .post(USERS_ENDPOINT)
            .then()
            .log().ifValidationFails()
            .statusCode(BAD_REQUEST.value())
    }
}
