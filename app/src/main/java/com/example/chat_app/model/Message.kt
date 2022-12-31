package com.example.chat_app.model

class Message {
    var message: String? = null
    var senderId: String? = null

    constructor(){}

    public constructor(message: String?, senderId: String?) {
        this.message = message
        this.senderId = senderId
    }

}