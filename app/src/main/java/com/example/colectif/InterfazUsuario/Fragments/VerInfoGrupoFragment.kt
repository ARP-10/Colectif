package com.example.colectif.InterfazUsuario.Fragments

import android.content.ContentValues
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVerInfoGrupoBinding.inflate(inflater, container, false)
        return binding.root
    }

    /*
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        idGrupo?.let { id ->
            val databaseReference: DatabaseReference = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/").getReference("groups")
            val databaseReference2: DatabaseReference = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users")
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(childSnapshot: DataSnapshot) {
                    childSnapshot.child(id).let { grupoSnapshot ->
                        if (grupoSnapshot.exists()) {
                            val administradorId = grupoSnapshot.child("administrador").value.toString()
                            databaseReference2.child(administradorId).addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val nombreAdmin = snapshot.child("name").value.toString()
                                    binding.txtAdministrador.text = nombreAdmin
                                    binding.txtApp.text = grupoSnapshot.child("app").value.toString()
                                    binding.txtPassword.text = grupoSnapshot.child("contrasenia").value.toString()
                                    binding.txtCorreo.text = grupoSnapshot.child("email").value.toString()

                                    val drawableApp = when (grupoSnapshot.child("app").value.toString()) {
                                        "Netflix" -> R.drawable.netflix
                                        "Spotify" -> R.drawable.spotify
                                        "Amazon Prime" -> R.drawable.amazon
                                        "Disney +" -> R.drawable.disney
                                        else -> R.drawable.error
                                    }
                                    binding.imgGrupo.setImageResource(drawableApp)
                                    binding.txtNombregrupo.text = grupoSnapshot.child("nombre").value.toString()
                                    binding.txtPlan.text = grupoSnapshot.child("plan").value.toString()
                                    binding.txtPrecio.text = grupoSnapshot.child("precio").value.toString()


                                }

                                override fun onCancelled(error: DatabaseError) {
                                }
                            })
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        } ?: run {
        }
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        idGrupo?.let { id ->
            val databaseReference: DatabaseReference = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/").getReference("groups")
            val databaseReference2: DatabaseReference = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users")

            databaseReference.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.d("VerInfoGrupoFragment", "DataSnapshot: $dataSnapshot")
                    val grupoSnapshot = dataSnapshot

                    if (grupoSnapshot.exists()) {
                        Log.d("VerInfoGrupoFragment", "GrupoSnapshot exists")
                        val administradorId = grupoSnapshot.child("administrador").value.toString()
                        Log.d("VerInfoGrupoFragment", "AdministradorId: $administradorId")

                        databaseReference2.child(administradorId).addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val nombreAdmin = snapshot.child("name").value.toString()

                                binding.txtAdministrador.text = nombreAdmin
                                binding.txtApp.text = grupoSnapshot.child("app").value.toString()
                                binding.txtPassword.text = grupoSnapshot.child("contrasenia").value.toString()
                                binding.txtCorreo.text = grupoSnapshot.child("email").value.toString()

                                val drawableApp = when (grupoSnapshot.child("app").value.toString()) {
                                    "Netflix" -> R.drawable.netflix
                                    "Spotify" -> R.drawable.spotify
                                    "Amazon Prime" -> R.drawable.amazon
                                    "Disney +" -> R.drawable.disney
                                    else -> R.drawable.error
                                }
                                binding.imgGrupo.setImageResource(drawableApp)
                                binding.txtNombregrupo.text = grupoSnapshot.child("nombre").value.toString()
                                binding.txtPlan.text = grupoSnapshot.child("plan").value.toString()
                                binding.txtPrecio.text = grupoSnapshot.child("precio").value.toString()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("VerInfoGrupoFragment", "Error al leer datos del administrador: ${error.message}")
                            }
                        })
                    } else {
                        Log.e("VerInfoGrupoFragment", "No existe el grupo con ID: $id")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("VerInfoGrupoFragment", "Error al leer datos del grupo: ${error.message}")
                }
            })
        } ?: run {
            Log.e("VerInfoGrupoFragment", "ID de grupo es nulo")
        }
    }




}