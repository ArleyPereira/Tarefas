package br.com.apsmobile.tarefasonline.model

import com.google.firebase.database.Exclude

class User(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    @get:Exclude
    var password: String = ""
)