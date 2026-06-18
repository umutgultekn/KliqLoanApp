package com.kliq.loanapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kliq.loanapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener { handleSignIn() }
    }

    private fun handleSignIn() {
        val email = binding.emailField.text.toString()
        val password = binding.passwordField.text.toString()

        if (email == "" || password == "") {
            binding.errorLabel.text = "Please fill all fields"
            return
        }

        if (!email.contains("@")) {
            binding.errorLabel.text = "Invalid email"
            return
        }

        if (password.length < 6) {
            binding.errorLabel.text = "Password too short"
            return
        }

        startActivity(Intent(this, HomeActivity::class.java))
    }
}
