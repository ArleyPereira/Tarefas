package br.com.apsmobile.tarefasonline.model

import com.google.firebase.database.FirebaseDatabase

data class Task(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var completed: Boolean = false
) {

    init {
        val firebaseRef = FirebaseDatabase.getInstance().reference
        val taskRef = firebaseRef.child("tasks")
        this.id = taskRef.push().key.toString()
    }

}