package com.example.colectif.InterfazUsuario.Fragments

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.colectif.R
import com.example.colectif.databinding.FragmentVerGrupoBinding
import com.example.colectif.databinding.FragmentVerInfoGrupoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class VerInfoGrupoFragment : Fragment() {

    private lateinit var binding: FragmentVerInfoGrupoBinding
    private lateinit var auth: FirebaseAuth
    private var idGrupo:String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        idGrupo = arguments?.getString("idGrupo", "")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVerInfoGrupoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            val databaseReference: DatabaseReference = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/").getReference("groups")
            val databaseReference2: DatabaseReference = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users")

            databaseReference.addValueEventListener(object : ValueEventListener {

                override fun onDataChange(childSnapshot: DataSnapshot) {
                    Log.d("VerInfoGrupoFragment", "DataSnapshot: $childSnapshot")
                    // Obtener los valores de cada hijo
                    val administradorId = childSnapshot.child(idGrupo!!).child("administrador").value.toString()
                    val app = childSnapshot.child(idGrupo!!).child("app").value.toString()
                    val nombre = childSnapshot.child(idGrupo!!).child("nombre").value.toString()
                    val plan = childSnapshot.child(idGrupo!!).child("plan").value.toString()
                    val precio = childSnapshot.child(idGrupo!!).child("precio").value.toString()

                    // Obtener el nombre del admin de la bbdd de "users"
                    databaseReference2.child(administradorId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val nombreAdmin = snapshot.child("name").value.toString()

                            // Actualizar la interfaz de usuario con los datos recuperados
                            binding.txtAdministrador.text = nombreAdmin
                            binding.txtApp.text = app

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

                override fun onCancelled(error: DatabaseError) {
                    Log.e("VerInfoGrupoFragment", "Error al leer datos del grupo: ${error.message}")
                }
            })
    }

    override fun onDetach() {
        super.onDetach()
    }


}