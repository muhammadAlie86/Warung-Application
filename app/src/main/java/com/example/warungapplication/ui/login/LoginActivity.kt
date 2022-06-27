package com.example.warungapplication.ui.login

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.example.warungapplication.ui.main.MainActivity
import com.example.warungapplication.MyApplication
import com.example.warungapplication.R
import com.example.warungapplication.ViewModelFactory
import com.example.warungapplication.databinding.ActivityLoginBinding
import com.example.warungapplication.ui.register.RegisterActivity
import com.example.warungapplication.utils.Event
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var factory : ViewModelFactory

    private val viewModel : LoginViewModel by viewModels {
        factory
    }
    private val activityScope = CoroutineScope(Dispatchers.Main)


    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        emailFocusListener()
        binding.btnLogin.setOnClickListener {
            validation()

        }
        binding.signUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun emailFocusListener()
    {
        binding.edEmail.setOnFocusChangeListener { _, focused ->
            if(!focused)
            {
                binding.textInputLayout.helperText = validEmail()
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

    private fun validation(){
        val username = binding.edEmail.text.toString()
        val password = binding.edPassword.text.toString()

        viewModel.isNavigateTo.observe(this) { status ->
            if (status == true) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        viewModel.isValid(username, password)
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
    private fun customDialog(){
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this).inflate(R.layout.custom_dialog,null)
        builder.setView(inflater)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
        activityScope.launch {
            delay(5000L)
            dialog.dismiss()
            validation()

        }
    }
}