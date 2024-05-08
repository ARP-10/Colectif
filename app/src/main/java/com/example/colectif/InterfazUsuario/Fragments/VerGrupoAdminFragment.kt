package com.example.colectif.InterfazUsuario.Fragments

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.colectif.Adapter.AdapterUsuarioAdmin
import com.example.colectif.R
import com.example.colectif.databinding.FragmentVerGrupoAdminBinding
import com.example.colectif.models.UsuarioGrupo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class VerGrupoAdminFragment : Fragment() {
    private lateinit var binding: FragmentVerGrupoAdminBinding
    private var idGrupo:String? = null
    private lateinit var adaptadorUsuariosAdmin : AdapterUsuarioAdmin
    private lateinit var listaUsuarios: ArrayList<UsuarioGrupo>
    private lateinit var database: FirebaseDatabase
    private lateinit var textPassword: TextView
    private lateinit var txtShowPassword: TextView

    // Recoger id grupo
    override fun onAttach(context: Context) {
        super.onAttach(context)
        idGrupo = arguments?.getString("idGrupo", "")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVerGrupoAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        database =
            FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        listaUsuarios = ArrayList()
        // Configuración del RecyclerView
        adaptadorUsuariosAdmin = context?.let { AdapterUsuarioAdmin(it, listaUsuarios) }!!
        binding.recyclerVerUsuariosAdmin.adapter = adaptadorUsuariosAdmin
        binding.recyclerVerUsuariosAdmin.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        recogerUsuarios()
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

                // Inicializar elementos para mostrar/ocultar contraseña
                textPassword = view.findViewById(R.id.txt_password)
                txtShowPassword = view.findViewById(R.id.txt_show_password)

                // Estado inicial de la visibilidad de la contraseña
                var passwordVisible = false

                // Llamar a la función para mostrar/ocultar la contraseña al inicio
                Log.v("cambio", contrasenia.toString())
                togglePasswordVisibility(passwordVisible, contrasenia)

                // Establecer el onClickListener para mostrar/ocultar la contraseña
                txtShowPassword.setOnClickListener {
                    passwordVisible = !passwordVisible
                    Log.v("cambio", passwordVisible.toString())
                    togglePasswordVisibility(passwordVisible, contrasenia)
                }

                // Obtener el nombre del admin de la bbdd de "users"
                ref2.child(administradorId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val nombreAdmin = snapshot.child("name").value.toString()

                        // Actualizar la interfaz de usuario con los datos recuperados
                        binding.txtAdministrador.text = nombreAdmin
                        //binding.txtApp.text = app
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

                        // Comprobar si los datos llegan correctamente
                        Log.v("administrador", nombreAdmin)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })




            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(ContentValues.TAG, "Error al leer los datos", databaseError.toException())
            }
        })
    }

    fun recogerUsuarios() {
        val ref = database.getReference("groups")
        val ref2 = database.getReference("users")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (i in 2 until 8) {
                        if (snapshot.child(idGrupo!!).child("users").child(i.toString()).child("id").value.toString() != "null") {
                            var idUsuario = snapshot.child(idGrupo!!).child("users").child(i.toString()).child("id").value.toString()
                            var pagado = snapshot.child(idGrupo!!).child("users").child(i.toString()).child("pagado").getValue(Boolean::class.java) ?: false
                            Log.v("verUsuario", idGrupo!!.toString())
                            ref2.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(usuaruiosSnapshot: DataSnapshot) {
                                    if (usuaruiosSnapshot.exists()) {
                                        var nombreUsuario = usuaruiosSnapshot.child(idUsuario).child("userName").value.toString()
                                        adaptadorUsuariosAdmin.addUsuarioAdmin(UsuarioGrupo(idUsuario, nombreUsuario, idGrupo!!, pagado))
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Manejar errores de base de datos
                                }
                            })
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }

    // Función para mostrar u ocultar la contraseña
    private fun togglePasswordVisibility(showPassword: Boolean, password: String) {
        Log.v(TAG, "Mostrar contraseña: $showPassword")
        if (showPassword) {
            // Mostrar la contraseña
            textPassword.text = password
            txtShowPassword.text = "Ocultar"
            Log.v("cambio", "Mostrando contraseña")
        } else {
            // Ocultar la contraseña
            textPassword.text = "********"
            txtShowPassword.text = "Mostrar"
            Log.v("cambio", "Ocultando contraseña")
        }
    }

}