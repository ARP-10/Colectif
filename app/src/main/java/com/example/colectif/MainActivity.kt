package com.example.colectif

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.colectif.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var correo: String
    private lateinit var contraseña: String
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            loginUsuario()
        }
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

    // Función para comprobar los campos de inicio de sesión
    private fun loginUsuario(){
        correo = binding.editUsuario.text.toString()
        contraseña = binding.editPassword.text.toString()

        auth.signInWithEmailAndPassword(correo, contraseña).
                addOnCompleteListener {
                    if(it.isSuccessful) {
                        startActivity(Intent(this, PruebaActivity::class.java))
                    }
                    else{
                        Snackbar.make(binding.root, "No se ha podido iniciar sesión", Snackbar.LENGTH_SHORT).show()
                    }
                }
    }
}