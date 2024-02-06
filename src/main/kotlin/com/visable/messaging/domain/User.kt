package com.visable.messaging.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity(name = "`user`")
data class User(
    @Id
    val id: String,
    val nickName: String,
)
