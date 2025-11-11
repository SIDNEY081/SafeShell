package com.sidney081.safeshell

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this)
        tv.text = "Welcome to SafeShell!"
        tv.textSize = 24f
        setContentView(tv)
    }
}
