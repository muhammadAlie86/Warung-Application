package com.example.warungapplication.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.warungapplication.R
import com.example.warungapplication.data.model.Warung
import com.example.warungapplication.data.repository.WarungRepository
import com.example.warungapplication.utils.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditViewModel @Inject constructor(private val repository: WarungRepository): ViewModel() {

    private val _snackBarText = MutableLiveData<Event<Int>>()
    val snackBarText: LiveData<Event<Int>> = _snackBarText

    private val _isNavigateTo = MutableLiveData<Boolean>()
    val isNavigateTo: LiveData<Boolean> = _isNavigateTo

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    fun editWarung(_name: String, _address: String, location: String, _imgWarung: String) {
        when {
            _imgWarung.isEmpty() -> {
                _snackBarText.value = Event(R.string.pilih_gambar)
                _isNavigateTo.value = false
                return
            }
            _name.isEmpty() -> {
                _snackBarText.value = Event(R.string.enter_your_name)
                _isNavigateTo.value = false
                return
            }
            location.isEmpty() -> {
                _snackBarText.value = Event(R.string.cari_koordinat)
                _isNavigateTo.value = false
                return
            }
            _address.isEmpty() -> {
                _snackBarText.value = Event(R.string.enter_your_address)
                _isNavigateTo.value = false
                return
            }
            else -> {
                uiScope.launch {
                    val warung = Warung(name = _name, address = _address, imgWarung = _imgWarung, location = location)
                    repository.editWarung(warung)
                    _snackBarText.value = Event(R.string.edit_successfully)
                    _isNavigateTo.value = true
                }
            }
        }
    }
}