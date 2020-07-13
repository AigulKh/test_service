package com.service

import com.mongodb.MongoClient
import org.bson.Document

val ID = "_id"
val PASSWORD = "password"
val FRIENDS = "friends"
val PRESENTS = "presents"
val SENDER = "sender"
val SENT_REQUESTS = "sent_requests"
val RECIPIENT = "recipient"
val INCOMING_REQUESTS = "incoming_requests"
val DATE_CREATED = "dateCreated"
val STATUS = "status"

class MongoDataService(mongoClient: MongoClient, database: String) {
    val database_ = mongoClient.getDatabase(database)
    val usersCollection = database_.getCollection("users")
    val presentsCollection = database_.getCollection("presents")
    val requestsCollection = database_.getCollection("requests")

    fun addUser(user: User) {
        usersCollection.insertOne(Document(ID, user.login).append(PASSWORD, user.password))
    }

    fun addPresent(present: Present) {
        presentsCollection.insertOne(Document(ID, present.id).append(SENDER, present.sender_login).append(RECIPIENT, present.recipient_login).append(DATE_CREATED, present.date_created).append(STATUS, present.status))
    }

    fun addRequest(request: Request) {
        requestsCollection.insertOne(Document(SENDER, request.sender_login).append(RECIPIENT, request.recipient_login))
    }

    fun checkRequest(sender_login: String, recipient_login: String): Boolean {
        requestsCollection.find(Document(SENDER, sender_login).append(RECIPIENT, recipient_login)).first() ?: return false
        return true
    }

    fun getFriendsString(user_login: String) = setOf(usersCollection.find(Document(ID, user_login)).first().toMutableMap().get(FRIENDS).toString())

    fun getSentRequestsString(user_login: String) = setOf(usersCollection.find(Document(ID, user_login)).first().toMutableMap().get(SENT_REQUESTS).toString())

    fun getSentRequests(user_login: String): Set<Request> {
        val set = mutableSetOf<Request>()
        val sender_sent_requests = stringToSet(getSentRequestsString(user_login).joinToString(", "))
        sender_sent_requests.forEach {
            set.add(Request(it, user_login))
        }
        return set
    }

    fun getIncomingRequestsString(user_login: String) = setOf(usersCollection.find(Document(ID, user_login)).first().toMutableMap().get(INCOMING_REQUESTS).toString())

    fun getIncomingRequests(user_login: String): Set<Request> {
        val set = mutableSetOf<Request>()
        val recipient_incoming_requests = stringToSet(getIncomingRequestsString(user_login).joinToString(", "))
        recipient_incoming_requests.forEach {
            set.add(Request(it, user_login))
        }
        return set
    }

    fun stringToSet(string: String): MutableSet<String> {
        val set = mutableSetOf<String>()
        var name = ""
        for (i in 0..string.length - 1) {
            when {
                string[i].isLetter() -> name += string[i]
                string[i] == ',' || string[i] == ']' -> {
                    set.add(name)
                    name = ""
                }
            }
        }
        return set
    }

    fun sendFriendRequest(sender_login: String, recipient_login: String) {
        val senders_sent_requests = stringToSet(getSentRequestsString(sender_login).joinToString(", "))
        senders_sent_requests.add(recipient_login)
        usersCollection.updateOne((Document(ID, sender_login)), Document("\$set", Document(SENT_REQUESTS, senders_sent_requests)))

        val recipients_incoming_requests = stringToSet(getIncomingRequestsString(recipient_login).joinToString(", "))
        recipients_incoming_requests.add(sender_login)
        usersCollection.updateOne((Document(ID, recipient_login)), Document("\$set", Document(INCOMING_REQUESTS, recipients_incoming_requests)))
    }

    fun acceptRequest(request: Request) {
        val sender_friends = stringToSet(getFriendsString(request.sender_login).joinToString(", "))
        sender_friends.add(request.recipient_login)
        usersCollection.updateOne((Document(ID, request.sender_login)), Document("\$set", Document(FRIENDS, sender_friends)))

        val recipient_friends = stringToSet(getFriendsString(request.recipient_login).joinToString(", "))
        recipient_friends.add(request.sender_login)
        usersCollection.updateOne((Document(ID, request.recipient_login)), Document("\$set", Document(FRIENDS, recipient_friends)))

        rejectRequest(request)
    }

    fun rejectRequest(request: Request) {
        val senders_sent_requests = stringToSet(getSentRequestsString(request.sender_login).joinToString(", "))
        senders_sent_requests.remove(request.recipient_login)
        usersCollection.updateOne((Document(ID, request.sender_login)), Document("\$set", Document(SENT_REQUESTS, senders_sent_requests)))

        val recipients_incoming_requests = stringToSet(getIncomingRequestsString(request.recipient_login).joinToString(", "))
        recipients_incoming_requests.remove(request.sender_login)
        usersCollection.updateOne((Document(ID, request.recipient_login)), Document("\$set", Document(INCOMING_REQUESTS, recipients_incoming_requests)))

        requestsCollection.deleteOne(Document(SENDER, request.sender_login).append(RECIPIENT, request.recipient_login))
    }

    fun deleteFriend(login: String, login_: String) {
        val first_user_friends = stringToSet(getFriendsString(login).joinToString(", "))
        first_user_friends.remove(login_)
        usersCollection.updateOne((Document(ID, login)), Document("\$set", Document(FRIENDS, first_user_friends)))

        val second_user_friends = stringToSet(getFriendsString(login_).joinToString(", "))
        second_user_friends.remove(login)
        usersCollection.updateOne((Document(ID, login_)), Document("\$set", Document(FRIENDS, second_user_friends)))
    }

    fun documentToPresent(document: Document): Present {
        val id = document.toMutableMap().get(ID).toString()
        val sender_login = document.toMutableMap().get(SENDER).toString()
        val recipient_login = document.toMutableMap().get(RECIPIENT).toString()
        val date_created = document.toMutableMap().get(DATE_CREATED).toString()
        var status = document.toMutableMap().get(STATUS).toString().toBoolean()
        return Present(id, sender_login, recipient_login, date_created, status)
    }

    fun getPresentsString(user_login: String) = setOf(usersCollection.find(Document(ID, user_login)).first().toMutableMap().get(PRESENTS).toString())

    fun getSentPresents(user_login: String, status: Boolean): Set<Present> {
        val set = mutableSetOf<Present>()
        presentsCollection.find(Document(SENDER, user_login).append(STATUS, status)).forEach{
            set.add(documentToPresent(it))
        }
        return set
    }

    fun getIncomingPresents(user_login: String, status: Boolean): Set<Present> {
        val set = mutableSetOf<Present>()
        presentsCollection.find(Document(RECIPIENT, user_login).append(STATUS, status)).forEach{
            set.add(documentToPresent(it))
        }
        return set
    }

    fun checkPresent(present: Present): Boolean {
        presentsCollection.find(Document(SENDER, present.sender_login).append(RECIPIENT, present.recipient_login).append(STATUS, false)).first() ?: return false
        return true
    }

    fun acceptPresent(present: Present) {
        val recipient_presents = stringToSet(getPresentsString(present.recipient_login).joinToString(", "))
        recipient_presents.add(present.id)
        usersCollection.updateOne((Document(ID, present.recipient_login)), Document("\$set", Document(PRESENTS, recipient_presents)))

        presentsCollection.updateOne((Document(ID, present.id)), Document("\$set", Document(STATUS, true)))
    }
}