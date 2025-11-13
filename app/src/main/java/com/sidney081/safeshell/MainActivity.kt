package com.sidney081.SafeShell

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val welcomeText = findViewById<TextView>(R.id.welcomeText)
        val logoutBtn = findViewById<Button>(R.id.logoutButton)
        val emergencyBtn = findViewById<Button>(R.id.emergencyButton)

        // Get current user and display email
        val currentUser = auth.currentUser
        if (currentUser != null) {
            welcomeText.text = "Welcome, ${currentUser.email}!"
        } else {
            welcomeText.text = "Welcome to SafeShell!"
        }

        // Logout Button
        logoutBtn.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Emergency Mode Button
        emergencyBtn.setOnClickListener {
            Toast.makeText(this, "🆘 Activating Emergency Mode", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, EmergencyLauncherActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}