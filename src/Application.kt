package com.service

import com.mongodb.MongoClient
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.request.receive

fun main(args: Array<String>) {
    val db = MongoDataService(MongoClient(), "friends")
//    io.ktor.server.netty.EngineMain.main(args)

    val Rachel = User("Rachel", "cvbkl")
    val Monica = User("Monica", "asdbnm")
    val Ross = User("Ross", "wertyu")
    val Chandler = User("Chandler", "dfghj")
    val Joey = User("Joey", "dfghj")

    Rachel.addUser(db)
    Monica.addUser(db)
    Ross.addUser(db)
    Chandler.addUser(db)
    Joey.addUser(db)

    Ross.sendFriendRequest(Rachel.login, db)
    Ross.sendFriendRequest(Monica.login, db)
    Rachel.acceptFriendRequest(Rachel.getIncomingRequests(db).elementAt(0), db)
    Monica.acceptFriendRequest(Rachel.getIncomingRequests(db).elementAt(0), db)

    Ross.sendPresent(Rachel.login, db)
    Monica.sendPresent(Rachel.login, db)
    Joey.sendPresent(Rachel.login, db)
    Ross.sendPresent(Rachel.login, db)
    Ross.sendPresent(Rachel.login, db)
    Ross.sendPresent(Rachel.login, db)

    Rachel.acceptPrecent(Rachel.getReceivedUnacceptedPresents(db).elementAt(0), db)
    Rachel.acceptPrecent(Rachel.getReceivedUnacceptedPresents(db).elementAt(0), db)
}

fun Application.module(testing: Boolean = false) {
    val db = MongoDataService(MongoClient(), "friends")
    routing {
        get("/") {
            call.respondText("")
        }
    }
}