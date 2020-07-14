package com.service

import com.mongodb.MongoWriteException
import java.time.LocalDateTime

val N = 5

class User(val login: String, var password: String, var count_present: Int = 0) {

    fun addUser(db: MongoDataService) {
        try {
            db.addUser(this)
        }
        catch (e: MongoWriteException) { }
    }

    fun getFriends(db: MongoDataService) = db.stringToSet(db.getFriendsString(this.login).joinToString(", "))

    fun getSentRequests(db: MongoDataService) = db.getSentRequests(this.login)

    fun getIncomingRequests(db: MongoDataService) = db.getIncomingRequests(this.login)

    fun sendFriendRequest(user_login: String, db: MongoDataService) {
        if (getFriends(db).contains(user_login) || db.checkRequest(this.login, user_login) || db.checkRequest(user_login, this.login)) return
        Request(this.login, user_login).addRequest(db)
        db.sendFriendRequest(this.login, user_login)
    }

    fun acceptFriendRequest(request: Request, db: MongoDataService) {
        if (!db.checkRequest(request.sender_login, request.recipient_login)) return
        request.accept(db)
    }

    fun rejectFriendRequest(request: Request, db: MongoDataService) {
        if (!db.checkRequest(request.sender_login, request.recipient_login)) return
        request.reject(db)
    }

    fun deleteFriend(user_login: String, db: MongoDataService) {
        if (!getFriends(db).contains(user_login)) return
        db.deleteFriend(this.login, user_login)
    }

    fun getSentAcceptedPresents(db: MongoDataService) = db.getSentPresents(this.login, true)

    fun getSentUnacceptedPresents(db: MongoDataService) = db.getSentPresents(this.login, false)

    fun getReceivedAcceptedPresents(db: MongoDataService) = db.getIncomingPresents(this.login, true)

    fun getReceivedUnacceptedPresents(db: MongoDataService) = db.getIncomingPresents(this.login, false)

    fun checkUnacceptedPresent(user_login: String, db: MongoDataService): Boolean {
        getSentUnacceptedPresents(db).forEach {
            if (it.recipient_login == user_login && it.status == false)
                return true
        }
        return false
    }

    fun sendPresent(user_login: String, db: MongoDataService) {
        if (!getFriends(db).contains(user_login) || this.count_present > N || checkUnacceptedPresent(user_login, db)) return

        Present("${this.login}_${LocalDateTime.now()}_$count_present", this.login, user_login).addPresent(db)
        this.count_present++
    }

    fun acceptPrecent(present: Present, db: MongoDataService) {
        if (!db.checkPresent(present)) return
        present.accept(db)
    }
}