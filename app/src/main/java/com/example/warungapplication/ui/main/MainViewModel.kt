package com.example.warungapplication.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.warungapplication.data.model.Warung
import com.example.warungapplication.data.repository.WarungRepository
import javax.inject.Inject

class MainViewModel @Inject constructor(private val repository: WarungRepository): ViewModel() {


    fun getAllWarung() : LiveData<List<Warung>>{
        return repository.getAllWarung()
    }
    fun signOut(){
        repository.signOut()
    }
    fun deleteWarung(warung: Warung){
        repository.deleteWarung(warung)
    }
}