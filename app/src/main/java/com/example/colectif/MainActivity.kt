package com.example.colectif

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.colectif.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar el OnClickListener para el enlace de registro
        binding.registerLink.setOnClickListener {
            openRegistrationScreen()
        }
    }

    // Método para abrir la pantalla de registro
    private fun openRegistrationScreen() {
        val intent = Intent(this, RegistroActivity::class.java)
        startActivity(intent)
    }
}