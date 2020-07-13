package com.service

import java.time.LocalDateTime

class Present(val id: String, val sender_login: String, val recipient_login: String, val date_created: String = LocalDateTime.now().toString(), val status: Boolean = false) {

    fun addPresent(db: MongoDataService) = db.addPresent(this)

    fun accept(db: MongoDataService) {
        db.acceptPresent(this)
    }
}