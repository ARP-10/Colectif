package com.example.colectif.InterfazUsuario.Fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.colectif.Adapter.AdapterUsuarios
import com.example.colectif.R
import com.example.colectif.databinding.FragmentCrearGrupoBinding
import com.example.colectif.databinding.FragmentVerGrupoAdminBinding
import com.example.colectif.databinding.FragmentVerGrupoBinding
import com.example.colectif.models.Grupo
import com.example.colectif.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore


class VerGrupoFragment : Fragment() {

    private lateinit var binding: FragmentVerGrupoBinding
    private lateinit var auth: FirebaseAuth
    private var idGrupo:String? = null
    private lateinit var adaptadorUsuarios : AdapterUsuarios
    private lateinit var listaUsuarios: ArrayList<String>
    private lateinit var database: FirebaseDatabase
    private lateinit var textPassword: TextView
    private lateinit var txtShowPassword: TextView


    override fun onAttach(context: Context) {
        super.onAttach(context)
        idGrupo = arguments?.getString("idGrupo", "")
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVerGrupoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        listaUsuarios = ArrayList()
        // Configuración del RecyclerView
        adaptadorUsuarios = context?.let { AdapterUsuarios(it, listaUsuarios) }!!
        binding.recyclerVerUsuarios.adapter = adaptadorUsuarios
        binding.recyclerVerUsuarios.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        recogerUsuarios()

        // Ubicar la BBDD

        var ref = database.getReference("groups")
        var ref2 = database.getReference("users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(childSnapshot: DataSnapshot) {

                // Obtener los valores de cada hijo
                val administradorId = childSnapshot.child(idGrupo!!).child("administrador").value.toString()
                val app = childSnapshot.child(idGrupo!!).child("app").value.toString()
                val contrasenia = childSnapshot.child(idGrupo!!).child("contrasenia").value.toString()
                val email = childSnapshot.child(idGrupo!!).child("email").value.toString()
                val nombre = childSnapshot.child(idGrupo!!).child("nombre").value.toString()
                val plan = childSnapshot.child(idGrupo!!).child("plan").value.toString()

                val precioString = childSnapshot.child(idGrupo!!).child("precio").value.toString()
                val precio = precioString.substringBefore(" ")
                    .replace(",", ".")
                    .toDoubleOrNull() ?: 0.0

                var numUsuariosActual = childSnapshot.child(idGrupo!!).child("numUsuarios").value.toString().toDouble()

                Log.v(TAG, "Precio: $precio")
                Log.v(TAG, "NumUsuariosActual: $numUsuariosActual")

                // Inicializar elementos para mostrar/ocultar contraseña
                textPassword = view.findViewById(R.id.txt_password)
                txtShowPassword = view.findViewById(R.id.txt_show_password)

                // Estado inicial de la visibilidad de la contraseña
                var passwordVisible = false

                // Llamar a la función para mostrar/ocultar la contraseña al inicio
                Log.v("cambio2", contrasenia.toString())
                togglePasswordVisibility(passwordVisible, contrasenia)

                // Establecer el onClickListener para mostrar/ocultar la contraseña
                txtShowPassword.setOnClickListener {
                    passwordVisible = !passwordVisible
                    Log.v("cambio2", passwordVisible.toString())
                    togglePasswordVisibility(passwordVisible, contrasenia)
                }

                // Obtener el nombre del admin de la bbdd de "users"
                ref2.child(administradorId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val nombreAdmin = snapshot.child("name").value.toString()

                            // Actualizar la interfaz de usuario con los datos recuperados
                            binding.txtAdministrador.text = nombreAdmin
                            //binding.txtPassword.text = contrasenia
                            binding.txtCorreo.text = email

                            val drawableApp = when (app) {
                                "Netflix" -> R.drawable.netflix
                                "Spotify" -> R.drawable.spotify
                                "Amazon Prime" -> R.drawable.amazon
                                "Disney +" -> R.drawable.disney
                                else -> R.drawable.error
                            }
                            binding.imgGrupo.setImageResource(drawableApp)
                            binding.txtNombregrupo.text = nombre
                            binding.txtPlan.text = plan
                            numUsuariosActual = precio / numUsuariosActual
                            // Redondear el resultado de la división a dos decimales
                            val gastosRedondeados = String.format("%.2f", numUsuariosActual)

                            binding.txtPrecio.text = "$precio €"
                            binding.txtGastos.text = "$gastosRedondeados €"


                            Log.v(TAG, "txtPrecio: ${binding.txtPrecio.text}")
                            Log.v(TAG, "txtGastos: ${binding.txtGastos.text}")
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Error al leer los datos", databaseError.toException())
            }
        })

        // Botón salir grupo
        binding.btnSalirGrupo.setOnClickListener {
            context?.let { it1 -> mostrarMensaje(it1, "Abandonar grupo", "¿Deseas salir del grupo?") }
        }



    }

    fun recogerUsuarios() {
        val ref = database.getReference("groups")
        val ref2 = database.getReference("users")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Recoger información del administrador
                    val adminId = snapshot.child(idGrupo!!).child("administrador").value.toString()

                    ref2.child(adminId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(adminSnapshot: DataSnapshot) {
                            if (adminSnapshot.exists()) {
                                val nombreAdmin = adminSnapshot.child("userName").value.toString()
                                adaptadorUsuarios.addUsuario(nombreAdmin)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Manejar errores de base de datos
                        }
                    })

                    // Recoger información de los usuarios
                    for (i in 2..snapshot.child(idGrupo!!).child("numUsuarios").value.toString().toInt()) {
                        val usuarioId = snapshot.child(idGrupo!!).child("users").child(i.toString()).child("id").value.toString()

                        ref2.child(usuarioId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(usuarioSnapshot: DataSnapshot) {
                                if (usuarioSnapshot.exists()) {
                                    val nombreUsuario = usuarioSnapshot.child("userName").value.toString()
                                    adaptadorUsuarios.addUsuario(nombreUsuario)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Manejar errores de base de datos
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores de base de datos
            }
        })
    }

    // Cuadro que muestra mensaje de aviso
    fun mostrarMensaje(contexto: Context, titulo: String, mensaje: String){
        val builder = AlertDialog.Builder(contexto)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)


        // Botón aceptar
        builder.setPositiveButton("Sí, estoy de acuerdo") { dialog, _ ->
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            Log.d(TAG, "Usuario actual: $userId")

            if (userId != null) {
                val database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
                val referenciaUsuario = database.getReference("users").child(userId)
                val ref3 = database.getReference("groups").child(idGrupo!!)
                val ref4 = database.getReference("groups").child(idGrupo!!).child("users")

                referenciaUsuario.child("groups").addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (grupoSnapshot in snapshot.children) {
                            val grupoId = grupoSnapshot.value.toString()
                            if (grupoId == idGrupo) {
                                Log.v("salir", grupoId)
                                Log.v("salir", "$idGrupo")
                                grupoSnapshot.ref.removeValue()
                                break
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "Error al leer los datos", error.toException())
                    }
                })

                ref3.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var numUsuariosActual = snapshot.child("numUsuarios").getValue(Int::class.java) ?: 0
                        numUsuariosActual--
                        ref3.child("numUsuarios").setValue(numUsuariosActual)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

                ref4.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (userSnapchot in snapshot.children) {
                            val usuarioId = userSnapchot.value.toString()
                            if (usuarioId == userId) {

                                userSnapchot.ref.removeValue()
                                break
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }

            // Volver a la pantalla de inicio
            //findNavController().navigate(R.id.action_verGrupoFragment_to_inicioFragment)
            // Ir a pantalla buscar grupos
            //findNavController().navigate(R.id.action_verGrupoFragment_to_listaGruposFragment)

            dialog.dismiss()
        }


        // Botón Rechazar
        builder.setNegativeButton("No, quiero seguir en el grupo") {dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    // Función para mostrar u ocultar la contraseña
    private fun togglePasswordVisibility(showPassword: Boolean, password: String) {
        Log.v(TAG, "Mostrar contraseña: $showPassword")
        if (showPassword) {
            // Mostrar la contraseña
            textPassword.text = password
            txtShowPassword.text = "Ocultar"
            Log.v("cambio2", "Mostrando contraseña")
        } else {
            // Ocultar la contraseña
            textPassword.text = "********"
            txtShowPassword.text = "Mostrar"
            Log.v("cambio2", "Ocultando contraseña")
        }
    }





}