package com.example.colectif.InterfazUsuario.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.colectif.Adapter.AdapterUsuarioAdmin
import com.example.colectif.databinding.FragmentVerGrupoAdminBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class VerGrupoAdminFragment : Fragment() {
    private lateinit var binding: FragmentVerGrupoAdminBinding
    private var idGrupo:String? = null
    private lateinit var adaptadorUsuariosAdmin : AdapterUsuarioAdmin
    private lateinit var listaUsuarios: ArrayList<String>
    private lateinit var database: FirebaseDatabase

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
                                    adaptadorUsuariosAdmin.addUsuarioAdmin(nombreUsuario)


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


    }}

