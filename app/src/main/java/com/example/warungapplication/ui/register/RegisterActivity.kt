package com.example.warungapplication.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import androidx.activity.viewModels
import com.example.warungapplication.ui.login.LoginActivity
import com.example.warungapplication.MyApplication
import com.example.warungapplication.ViewModelFactory
import com.example.warungapplication.databinding.ActivityRegisterBinding
import com.example.warungapplication.utils.Event
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class RegisterActivity : AppCompatActivity() {

    @Inject
    lateinit var factory : ViewModelFactory


    private val viewModel : RegisterViewModel by viewModels {
        factory
    }
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            addUser()
        }
        binding.tvSignIn.setOnClickListener {
            moveToLogin()
        }
        emailFocusListener()
    }
    private fun emailFocusListener()
    {
        binding.edEmail.setOnFocusChangeListener { _, focused ->
            if(!focused)
            {
                binding.textInputLayout3.helperText = validEmail()
            }
        }
    }

    private fun validEmail(): String?
    {
        val emailText = binding.edEmail.text.toString()
        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches())
        {
            return "Invalid Email Address"
        }
        return null
    }
    private fun moveToLogin(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun addUser(){
        val email = binding.edEmail.text.toString().lowercase().trim()
        val password = binding.edPassword.text.toString().lowercase().trim()
        val confirmPassword = binding.edConfirmPassword.text.toString().lowercase().trim()

        viewModel.isNavigateTo.observe(this) { status ->
            if (status == true) {
                moveToLogin()
            }
        }
        viewModel.saveLoginClick(email ,password, confirmPassword)
        viewModel.snackBarText.observe(this) {
            showSnackBar(it)
        }


    }
    private fun showSnackBar(eventMessage: Event<Int>) {
        val message = eventMessage.getContentIfNotHandled() ?: return
        Snackbar.make(
            binding.constraint,
            getString(message),
            Snackbar.LENGTH_SHORT
        ).show()
    }

}