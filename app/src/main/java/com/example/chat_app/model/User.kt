package com.example.chat_app.model

public class User {
    var uid: String? = null
    var username: String? = null

    constructor(){}

    constructor(uid: String?, username: String?) {
        this.uid = uid
        this.username = username
    }

}