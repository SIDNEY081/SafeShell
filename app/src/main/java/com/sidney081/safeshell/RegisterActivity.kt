package com.sidney081.SafeShell

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val emailInput = findViewById<EditText>(R.id.emailEditText)
        val passwordInput = findViewById<EditText>(R.id.passwordEditText)
        val confirmPasswordInput = findViewById<EditText>(R.id.confirmPasswordEditText)
        val nameInput = findViewById<EditText>(R.id.nameEditText)
        val registerBtn = findViewById<Button>(R.id.registerButton)
        val webRegisterBtn = findViewById<Button>(R.id.webRegisterButton)
        val loginNav = findViewById<TextView>(R.id.loginNavText)

        // Native registration
        registerBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()
            val name = nameInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(email, password, name)
            }
        }

        // Web registration
        webRegisterBtn.setOnClickListener {
            redirectToWebRegistration()
        }

        loginNav.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser(email: String, password: String, name: String) {
        // Show loading
        val registerBtn = findViewById<Button>(R.id.registerButton)
        registerBtn.text = "Creating Account..."
        registerBtn.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Update user profile with name
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                        // Reset button
                        registerBtn.text = "Create Account"
                        registerBtn.isEnabled = true

                        if (profileTask.isSuccessful) {
                            Toast.makeText(this, "Registration Successful! 🎉", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Registration successful but failed to set name", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                    }
                } else {
                    // Reset button on failure
                    registerBtn.text = "Create Account"
                    registerBtn.isEnabled = true

                    val errorMessage = when {
                        task.exception?.message?.contains("email address is already") == true ->
                            "Email already registered. Try logging in instead."
                        task.exception?.message?.contains("badly formatted") == true ->
                            "Please enter a valid email address."
                        else -> "Registration failed: ${task.exception?.message}"
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun redirectToWebRegistration() {
        val webRegistrationUrl = " https://safeshell-f3b42.web.app"

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webRegistrationUrl))
            startActivity(intent)
            Toast.makeText(this, "Opening web registration...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot open web browser. Please install a browser app.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}