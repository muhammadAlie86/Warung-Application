package com.example.warungapplication.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.warungapplication.R
import com.example.warungapplication.data.repository.WarungRepository
import com.example.warungapplication.utils.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val repository: WarungRepository): ViewModel() {

    private val _snackBarText = MutableLiveData<Event<Int>>()
    val snackBarText: LiveData<Event<Int>> = _snackBarText

    private val _isNavigateTo = MutableLiveData<Boolean>()
    val isNavigateTo : LiveData<Boolean> = _isNavigateTo

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    fun isValid(_username : String , _password : String) {
        when {
            _username.isEmpty() -> {
                _snackBarText.value = Event(R.string.enter_your_username)
                _isNavigateTo.value = false
                return
            }
            _password.isEmpty() -> {
                _snackBarText.value = Event(R.string.enter_your_password)
                _isNavigateTo.value = false
                return
            }
            else -> {
                uiScope.launch {
                    repository.login(_username,_password)
                    _snackBarText.value = Event(R.string.login_successfully)
                    _isNavigateTo.value = true
                }
            }
        }
    }
}