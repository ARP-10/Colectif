package com.example.colectif

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.colectif.databinding.ActivityMainBinding
import com.example.colectif.databinding.ActivityRegistroBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private lateinit var nombre: String
    private lateinit var primerApellido: String
    private lateinit var segundoApellido: String
    private lateinit var correo: String
    private lateinit var usuario: String
    private lateinit var contraseña: String
    private lateinit var auth: FirebaseAuth
    private lateinit var dataBase: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Conexión a Firebase
        auth = FirebaseAuth.getInstance()
        dataBase = FirebaseDatabase.getInstance().reference

        binding.buttonRegistrarGuardarUsuario.setOnClickListener {
            nombre = binding.editTextRegistrarNombre.text.toString()
            primerApellido = binding.editTextRegistrarPrimerApellido.text.toString()
            segundoApellido = binding.editTextRegistrarSegundoApellido.text.toString()
            correo = binding.editTextRegistrarCorreo.text.toString()
            usuario = binding.editTextRegistrarUsuario.text.toString()
            contraseña = binding.editTextRegistrarContraseA.text.toString()

            //Comprobaciones de que este obien puesto los campos
            if(!nombre.isEmpty()&&!primerApellido.isEmpty()&&!segundoApellido.isEmpty()&&!correo.isEmpty()&&!usuario.isEmpty()
                &&!contraseña.isEmpty()){
                if(contraseña.length >= 6){
                    if(contraseña.equals(binding.editTextRegistrarConfirmarContraseA.text.toString())){
                        registrarUsuario()
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
    private fun registrarUsuario(){

        auth.createUserWithEmailAndPassword(correo, contraseña)

            .addOnCompleteListener{
                //Aún en proceso porque falla el añadir información adicional
                if(it.isSuccessful){
                    var array = arrayOf(nombre,primerApellido,segundoApellido,correo,
                    usuario,contraseña)
                    var id = auth.currentUser?.providerId
                    if (id != null) {
                        dataBase.child("Users").child(id).setValue(array)
                            .addOnCompleteListener{
                                Snackbar.make(binding.root, "Se registró coreectamente", Snackbar.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Snackbar.make(binding.root, "No se pudo registrar este usuario", Snackbar.LENGTH_SHORT).show()
                }
            }
    }

}


