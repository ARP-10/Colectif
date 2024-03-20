package com.example.colectif.InterfazUsuario.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.colectif.R
import com.example.colectif.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private var sharedPref: String = "com.example.colectif"
    private lateinit var binding: ActivityLoginBinding
    private lateinit var correo: String
    private lateinit var contraseña: String
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedP: SharedPreferences
    private var estado: Boolean = false





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedP = getSharedPreferences(sharedPref, Context.MODE_PRIVATE)
        estado = sharedP.getBoolean("estado", false)
        auth = FirebaseAuth.getInstance()



        if(estado){
            val intent = Intent(applicationContext, InicioActivity::class.java)
            intent.putExtra("id",auth.currentUser?.uid)
            startActivity(intent)
            finish()
        }
        binding.btnLogin.setOnClickListener {
            correo = binding.editUsuario.text.toString()
            contraseña = binding.editPassword.text.toString()
            if(correo != ""||contraseña != ""){
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
                        auth.currentUser?.let { it1 -> saveData(it1.uid) }
                        val intent = Intent(applicationContext, InicioActivity::class.java)
                        intent.putExtra("id",auth.currentUser?.uid)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Snackbar.make(binding.root, "No se ha podido iniciar sesión", Snackbar.LENGTH_SHORT).show()
                    }
                }
    }


    //TODO funcion para iniciar con google
    /*private fun inicioGoogle(){
        val googleConfig =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.SERVER_CLIENT_ID))
                .requestEmail()
                .build()
        val googleClient = GoogleSignIn.getClient(this,googleConfig)
        startActivityForResult(googleClient.signInIntent, )
    }*/

    private fun saveData(id:String){
        val sharedPreferences = getSharedPreferences(sharedPref,Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("estado",true)
        editor.putString("id",id)
        editor.apply()
    }


}