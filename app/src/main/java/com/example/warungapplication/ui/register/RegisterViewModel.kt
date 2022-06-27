package com.example.warungapplication.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.warungapplication.R
import com.example.warungapplication.data.model.User
import com.example.warungapplication.data.repository.WarungRepository
import com.example.warungapplication.utils.Event
import com.example.warungapplication.utils.Regex.toSHA256Hash
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject

class RegisterViewModel @Inject constructor(
    private val repository: WarungRepository
): ViewModel() {

    private val _snackBarText = MutableLiveData<Event<Int>>()
    val snackBarText: LiveData<Event<Int>> = _snackBarText

    private val _isNavigateTo = MutableLiveData<Boolean>()
    val isNavigateTo : LiveData<Boolean> = _isNavigateTo


    fun saveLoginClick(_email : String,
                       _password : String,
                       _confirmPassword : String  ) {

        when {
            _email.isEmpty() -> {
                _snackBarText.value = Event(R.string.enter_your_email)
                _isNavigateTo.value = false
                return
            }

            _password.isEmpty() -> {
                _snackBarText.value = Event(R.string.enter_your_password)
                _isNavigateTo.value = false
                return
            }
            _confirmPassword.isEmpty() -> {
                _snackBarText.value = Event(R.string.enter_your_confirm_password)
                _isNavigateTo.value = false
                return
            }
            _confirmPassword != _password -> {
                _snackBarText.value = Event(R.string.password_doesnt_match)
                _isNavigateTo.value = false
                return
            }
            else -> {
                val user = User(email = _email, password = _password.toSHA256Hash())
                repository.addUser(user)
                _snackBarText.value = Event(R.string.register_succes)
                _isNavigateTo.value = true
            }
        }
    }
}