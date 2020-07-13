package com.service

class Request(val sender_login: String, val recipient_login: String) {

    fun addRequest(db: MongoDataService) {
        db.addRequest(this)
    }

    fun accept(db: MongoDataService) {
        if (db.checkRequest(this.sender_login, this.recipient_login))
            db.acceptRequest(this)
    }

    fun reject(db: MongoDataService) {
        if (db.checkRequest(this.sender_login, this.recipient_login))
            db.rejectRequest(this)
    }
}