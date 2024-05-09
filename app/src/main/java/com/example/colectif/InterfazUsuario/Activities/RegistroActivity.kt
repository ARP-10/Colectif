package com.example.colectif.InterfazUsuario.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.colectif.databinding.ActivityRegistroBinding
import com.example.colectif.models.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private lateinit var user: User
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Conexión a Firebase
        auth = FirebaseAuth.getInstance()
        var database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")

        binding.buttonRegistrarGuardarUsuario.setOnClickListener {
            // Recogemos los datos
            val name = binding.editTextRegistrarNombre.text.toString()
            val firstSurName = binding.editTextRegistrarPrimerApellido.text.toString()
            val secondSurName = binding.editTextRegistrarSegundoApellido.text.toString()
            val mail = binding.editTextRegistrarCorreo.text.toString()
            val userName = binding.editTextRegistrarUsuario.text.toString()
            val password = binding.editTextRegistrarContraseA.text.toString()

            //Comprobaciones de que este bien puesto los campos
            if(!name.isEmpty() &&!firstSurName.isEmpty()&&!secondSurName.isEmpty()&&!mail.isEmpty()&&!userName.isEmpty()
                &&!password.isEmpty()){
                if(password.length >= 6){
                    if(password.equals(binding.editTextRegistrarConfirmarContraseA.text.toString())){
                        user = User(name, firstSurName, secondSurName, mail, userName, "",ArrayList(), 0, 0 )
                        registrarUsuario(user, database)
                        // TODO: quitar este snackbar??
                        //Snackbar.make(binding.root, "Usuario registrado", Snackbar.LENGTH_SHORT).show()
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
    // TODO: Aún en proceso porque falla el añadir información adicional
    private fun registrarUsuario(user : User, database : FirebaseDatabase){

        auth.createUserWithEmailAndPassword(user.mail, binding.editTextRegistrarContraseA.text.toString()).addOnCompleteListener{

            if(it.isSuccessful){

                val id = auth.currentUser!!.uid
                database.getReference("users").child(id).setValue(user).addOnSuccessListener {
                    // Crear un nodo vacío para numGrupos y numSolicitudes
                    val userData = HashMap<String, Any>()
                    userData["name"] = user.name
                    userData["firstSurName"] = user.firstSurName
                    userData["secondSurName"] = user.secondSurName
                    userData["mail"] = user.mail
                    userData["userName"] = user.userName
                    userData["imagen"] = user.imagen
                    userData["groups"] = user.groups
                    userData["numGrupos"] = user.numGrupos // Inicializado en 0
                    userData["numSolicitudes"] = user.numSolicitudes // Inicializado en 0

                    Snackbar.make(binding.root, "El usuario se registró correctamente", Snackbar.LENGTH_SHORT).show()

                    // Vaciar los campos del EditText
                    binding.editTextRegistrarNombre.text.clear()
                    binding.editTextRegistrarPrimerApellido.text.clear()
                    binding.editTextRegistrarSegundoApellido.text.clear()
                    binding.editTextRegistrarCorreo.text.clear()
                    binding.editTextRegistrarUsuario.text.clear()
                    binding.editTextRegistrarContraseA.text.clear()
                    binding.editTextRegistrarConfirmarContraseA.text.clear()


                    // TODO: Hacer que te lleve a la pantalla login
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            } else {
                Log.e("RegistroUsuario", "Error al registrar el usuario: ${it.exception?.message}")
                Snackbar.make(binding.root, "No se pudo registrar este usuario", Snackbar.LENGTH_SHORT).show()
            }
        }

    }
}


