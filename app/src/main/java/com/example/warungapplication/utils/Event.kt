package com.example.warungapplication.utils

open class Event<out T>(private val content: T) {

    private var eventHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (eventHandled) {
            null
        } else {
            eventHandled = true
            content
        }
    }

}