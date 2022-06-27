package com.example.warungapplication.data.repository

import androidx.lifecycle.MutableLiveData
import com.example.warungapplication.data.model.User
import com.example.warungapplication.data.model.Warung

interface IWarungRepository {

    fun addUser(user: User)
    fun login(email : String, password : String)
    fun signOut()
    fun addWarung(warung: Warung)
    fun getAllWarung() : MutableLiveData<List<Warung>>
    fun editWarung(warung: Warung)
    fun deleteWarung(warung: Warung)
}