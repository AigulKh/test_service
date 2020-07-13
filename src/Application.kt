package com.service

import com.mongodb.MongoClient
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.request.receive

//import org.graalvm.compiler.api.replacements.Snippet

fun main(args: Array<String>) {
    val db = MongoDataService(MongoClient(), "friends")
//    io.ktor.server.netty.EngineMain.main(args)

    val lena = User("Lena", "cvbkl")
    val andrey = User("Andrey", "asdbnm")
    val liza = User("Liza", "wertyu")
    val alina = User("Alina", "dfghj")
    val alex = User("Alex", "dfghj")

    lena.addUser(db)
    andrey.addUser(db)
    liza.addUser(db)
    alina.addUser(db)
    alex.addUser(db)

//    println(liza.getSentAcceptedPresents(db))
//    liza.getSentUnacceptedPresents(db).forEach { println("${it.id}, ") }
//    println(lena.getReceivedAcceptedPresents(db))
//    println("____________")
    liza.sendFriendRequest(lena.login, db)
    liza.sendFriendRequest(andrey.login, db)
    lena.acceptFriendRequest(lena.getIncomingRequests(db).elementAt(0), db)
    andrey.acceptFriendRequest(lena.getIncomingRequests(db).elementAt(0), db)

    liza.sendPresent(lena.login, db)
    liza.sendPresent(lena.login, db)
    liza.sendPresent(lena.login, db)
    liza.sendPresent(lena.login, db)
    liza.sendPresent(lena.login, db)
    liza.sendPresent(lena.login, db)

    lena.getReceivedUnacceptedPresents(db).forEach { println("${it.id}, ") }
    lena.acceptPrecent(lena.getReceivedUnacceptedPresents(db).elementAt(0), db)
    lena.acceptPrecent(lena.getReceivedUnacceptedPresents(db).elementAt(0), db)
    lena.getReceivedAcceptedPresents(db).forEach { println("${it.id}, ") }

//    lena.sendFriendRequest(liza.login)
//    lena.sendFriendRequest(andrey.login)
//    liza.sendFriendRequest(andrey.login)

//    liza.deleteFriend(liza.getFriends().elementAt(0))

//    andrey.getIncomingRequests().forEach { print("${it.sender_login}, ") }
//    andrey.getIncomingRequests().forEach { print("${it.sender_login}, ") }
//    liza.getSentRequests().forEach { print("${it.recipient_login}, ") }
//    liza.getFriends().forEach { print("${it}, ") }

//    andrey.acceptFriendRequest(andrey.getIncomingRequests().elementAt(1))
//    liza.acceptFriendRequest(liza.getIncomingRequests().elementAt(1))
//    println(andrey.getFriends())
//    andrey.acceptFriendRequest(andrey.getIncomingRequests().elementAt(0))
//    lena.acceptFriendRequest(
//            liza.getIncomingRequests_()
//    )
//
//    lena.getFriends().forEach { println(it) }
//    println(lena.getSentRequests())
//    println(lena.getIncomingRequests())

//    println(alina.getRequests("sender").joinToString())
//    println(alina.getRequests("recipient").joinToString())

//    liza.friends_list.forEach { print("${it.login}, ") }
//    println()
//    liza.getIncomingRequests().forEach { print("$it, ") }
//    lena.getSentRequests().forEach { print("$it, ") }
//
//    liza.acceptFriendRequest(liza.incoming_requests.elementAt(0))
//    liza.acceptFriendRequest(liza.incoming_requests.elementAt(0))
//    liza.rejectFriendRequest(liza.incoming_requests.elementAt(0))
//
//    liza.friends_list.forEach { print("${it.login}, ") }
//    println()
//    liza.incoming_requests.forEach { print("${it.sender.login}, ") }
//    println()
//    liza.sent_requests.forEach { print("${it.recipient.login}, ") }
//    println()
//
//    liza.deleteFriend(liza.friends_list.elementAt(1))
//    liza.friends_list.forEach { print("${it.login}, ") }
//
//    liza.sendPresent(lena)
//    liza.sendPresent(andrey)
//    liza.sendPresent(lena)
//    lena.acceptPresent(lena.new_presents.elementAt(0))
//    liza.sendPresent(lena)
//    lena.acceptPresent(lena.new_presents.elementAt(0))
//
//    lena.presents_list.forEach { print("${it.sender.login} -> ${it.recipient.login}, ") }
//    println(alina.sent_requests.elementAt(0).sender.name)
//    println(alina.sent_requests.elementAt(0).recipient.name)
}

fun Application.module(testing: Boolean = false) {
    val db = MongoDataService(MongoClient(), "friends")
    routing {
        get("/") {
            call.respondText("/api/login")
        }
        post("/api/login") {
//            val login: String? = call.request.queryParameters["login"]
//            val password: String? = call.request.queryParameters["password"]
            val post = call.receive<String>()
            val login = post.get(0)
            val password = post.get(1)
//            val snippet: PostSnippet.Text
//            val post = call.receive<PostSnippet>()
//            snippets += Snippet(post.snippet.text)
            call.respond(mapOf("OK" to true))
            if (login != null && password != null) {
                User(login.toString(), password.toString())
//                call.respondText("User added")
            }
//            else call.respondText("Uncorrect date")
        }
//        post("/login-register") {
//            val post = call.receive<LoginRegister>()
//            val user = getOrPut(post.user) { User(post.user, post.password) }
//            if (user.password != post.password) throw InvalidCredentialsException("Invalid credentials")
//            call.respond(mapOf("token" to simpleJwt.sign(user.name)))
//        }
    }
}