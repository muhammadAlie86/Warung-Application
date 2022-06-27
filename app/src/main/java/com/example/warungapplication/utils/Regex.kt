package com.example.warungapplication.utils

import java.security.MessageDigest

object Regex {

    fun String.toSHA256Hash() : String {
        val bytes = this.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}