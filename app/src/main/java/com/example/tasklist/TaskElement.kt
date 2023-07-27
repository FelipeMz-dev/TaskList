package com.example.tasklist

data class TaskElement(
    var taskText: String,
    var expiryDate: String,
    var done: Boolean = false) {

}