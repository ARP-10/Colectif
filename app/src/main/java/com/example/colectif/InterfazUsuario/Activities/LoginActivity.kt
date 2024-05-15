package com.example.colectif.InterfazUsuario.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.colectif.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

/**
 * Actividad de inicio de sesión que permite a los usuarios iniciar sesión en la aplicación.
 * Si el usuario ya ha iniciado sesión anteriormente, se redirige automáticamente a la pantalla de inicio.
 * Permite al usuario ingresar su correo electrónico y contraseña para autenticarse.
 * También proporciona un enlace para dirigir al usuario a la pantalla de registro en caso de que no tenga una cuenta.
 */
class LoginActivity : AppCompatActivity() {
    private var sharedPref: String = "com.example.colectif"
    private lateinit var binding: ActivityLoginBinding
    private lateinit var correo: String
    private lateinit var contraseña: String
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedP: SharedPreferences // Para acceder a la memoria interna del móvil
    private var estado: Boolean = false // Variable que determina si ya estaba logueado o no





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedP = getSharedPreferences(sharedPref, Context.MODE_PRIVATE)
        estado = sharedP.getBoolean("estado", false)
        auth = FirebaseAuth.getInstance()


        // Comprueba si el usuario ya esta logueado a través de la memoria del móvil
        if(estado){
            Log.v("LoginActivity", "Usuario ya autenticado, redirigiendo a la pantalla de inicio")
            val intent = Intent(applicationContext, InicioActivity::class.java)
            intent.putExtra("id",auth.currentUser?.uid)
            startActivity(intent)
            finish()
        }

        // Al pulsar comprueba los campos y cambia de pantalla si todo está correcto
        binding.btnLogin.setOnClickListener {
            correo = binding.editUsuario.text.toString()
            contraseña = binding.editPassword.text.toString()
            if(correo != ""||contraseña != ""){
                Log.v("LoginActivity", "Iniciando sesión con correo: $correo")
                loginUsuario(correo, contraseña)
            }else{
                Snackbar.make(binding.root,"Rellene todos los campos", Snackbar.LENGTH_SHORT).show()
            }

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
    private fun loginUsuario(mail:String, pass:String){

        auth.signInWithEmailAndPassword(mail, pass).
                addOnCompleteListener {
                    if(it.isSuccessful) {
                        Log.v("LoginActivity", "Inicio de sesión exitoso")
                        auth.currentUser?.let { it1 -> saveData(it1.uid) }
                        val intent = Intent(applicationContext, InicioActivity::class.java)
                        intent.putExtra("id",auth.currentUser?.uid)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Log.v("LoginActivity", "Error al iniciar sesión: ${it.exception?.message}")
                        Snackbar.make(binding.root, "No se ha podido iniciar sesión", Snackbar.LENGTH_SHORT).show()
                    }
                }
    }



    // Guarda dentro de la memoria del móvil que el usuario ya está logueado
    private fun saveData(id:String){
        val sharedPreferences = getSharedPreferences(sharedPref,Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("primeraVez",true)
        editor.putBoolean("estado",true)
        editor.putString("id",id)
        editor.apply()
    }
}