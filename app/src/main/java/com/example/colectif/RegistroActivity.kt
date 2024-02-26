package com.example.colectif

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.colectif.databinding.ActivityMainBinding
import com.example.colectif.databinding.ActivityRegistroBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private lateinit var user: User
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Conexión a Firebase
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference




        binding.buttonRegistrarGuardarUsuario.setOnClickListener {
                val name = binding.editTextRegistrarNombre.text.toString()
                val firstSurName = binding.editTextRegistrarPrimerApellido.text.toString()
                val secondSurName = binding.editTextRegistrarSegundoApellido.text.toString()
                val mail = binding.editTextRegistrarCorreo.text.toString()
                val userName = binding.editTextRegistrarUsuario.text.toString()
                val password = binding.editTextRegistrarContraseA.text.toString()

            //Comprobaciones de que este obien puesto los campos
            if(!name.isEmpty() &&!firstSurName.isEmpty()&&!secondSurName.isEmpty()&&!mail.isEmpty()&&!userName.isEmpty()
                &&!password.isEmpty()){
                if(password.length >= 6){
                    if(password.equals(binding.editTextRegistrarConfirmarContraseA.text.toString())){
                        val user = User(name, firstSurName, secondSurName, mail, userName, password)
                        registrarUsuario(user)
                        Snackbar.make(binding.root, "Usuario registrado", Snackbar.LENGTH_SHORT).show()
                    }
                    else{
                        Snackbar.make(binding.root, "Las contraseña no coinciden", Snackbar.LENGTH_SHORT).show()
                    }
                }
                else{
                    Snackbar.make(binding.root, "La contraseña tiene que ser igual o mayor de 6 caracteres", Snackbar.LENGTH_SHORT).show()
                }
            }
            else{
                Snackbar.make(binding.root, "Debe de completar todos los campos", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    //Función para añadir los datos de los campos a Firebase
    private fun registrarUsuario(user : User){

        auth.createUserWithEmailAndPassword(user.mail, user.password)
            .addOnCompleteListener{
                //Aún en proceso porque falla el añadir información adicional
                if(it.isSuccessful){
                    database.child("users").child(user.name).setValue(user)
                        .addOnSuccessListener {
                            Snackbar.make(binding.root,"El usuario se registró correctamente", Snackbar.LENGTH_SHORT).show()
                        }

                } else {
                    Snackbar.make(binding.root, "No se pudo registrar este usuario", Snackbar.LENGTH_SHORT).show()
                }
            }
    }

}


