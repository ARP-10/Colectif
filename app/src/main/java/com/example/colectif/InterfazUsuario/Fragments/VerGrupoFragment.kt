package com.example.colectif.InterfazUsuario.Fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.colectif.Adapter.AdapterUsuarios
import com.example.colectif.R
import com.example.colectif.databinding.FragmentCrearGrupoBinding
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
                    val precio = childSnapshot.child(idGrupo!!).child("precio").value.toString()

                    // Obtener el nombre del admin de la bbdd de "users"
                ref2.child(administradorId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val nombreAdmin = snapshot.child("name").value.toString()

                            // Actualizar la interfaz de usuario con los datos recuperados
                            binding.txtAdministrador.text = nombreAdmin
                            binding.txtApp.text = app
                            binding.txtPassword.text = contrasenia
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
                            binding.txtPrecio.text = precio

                            // Comprobar si los datos llegan correctamente
                            Log.v("administrador", nombreAdmin)
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

    }

    fun recogerUsuarios() {
        val ref = database.getReference("groups")
        val ref2 = database.getReference("users")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (i in 1 until snapshot.child(idGrupo!!)
                        .child("numUsuarios").value.toString().toInt() + 1) {
                        var idUsuario = snapshot.child(idGrupo!!).child("users").child(i.toString()).value.toString()
                        Log.v("verUsuario", idGrupo!!.toString())
                        ref2.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(usuaruiosSnapshot: DataSnapshot) {
                                if (usuaruiosSnapshot.exists()) {
                                    var nombreUsuario = usuaruiosSnapshot.child(idUsuario).child("userName").value.toString()
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
                TODO("Not yet implemented")
            }
        })


    }

}