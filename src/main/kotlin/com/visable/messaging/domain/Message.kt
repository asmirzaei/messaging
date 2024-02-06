package com.visable.messaging.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.Instant

@Entity
data class Message(
    @Id
    val id: String,
    val message: String,
    @ManyToOne
    @JoinColumn(name = "sender_user_id")
    val senderUser: User,
    @ManyToOne
    @JoinColumn(name = "receiver_user_id")
    val receiverUser: User,
    val receivedDateTime: Instant,
)
