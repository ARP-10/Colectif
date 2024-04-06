package com.example.colectif.InterfazUsuario.Fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentResultListener
import com.example.colectif.R
import com.example.colectif.databinding.FragmentVerGrupoBinding
import com.example.colectif.models.Grupo
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
    private lateinit var idGrupo: String


    override fun onAttach(context: Context) {
        super.onAttach(context)
        idGrupo = requireArguments().getString("idGrupo")!!
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

        auth = FirebaseAuth.getInstance()
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/").getReference("groups")
        val databaseReference2: DatabaseReference = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //var childSnapshot = dataSnapshot.children
                    // Obtén los valores de cada hijo
                    val administrador = dataSnapshot.child(idGrupo).child("administrador").value.toString()
                    var nombreAdmin = ""
                    databaseReference2.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            nombreAdmin = snapshot.child(administrador).child("name").value.toString()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                    val app = dataSnapshot.child(idGrupo).child("app").value.toString()
                    val contrasenia = dataSnapshot.child(idGrupo).child("contrasenia").value.toString()
                    val email = dataSnapshot.child(idGrupo).child("email").value.toString()

                    val nombre = dataSnapshot.child(idGrupo).child("nombre").value.toString()
                    val plan = dataSnapshot.child(idGrupo).child("plan").value.toString()
                    val precio = dataSnapshot.child(idGrupo).child("precio").value.toString()

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
                    Log.v("administrador", administrador)
                    Log.d(TAG, "App: $app")
                    Log.d(TAG, "Contraseña: $contrasenia")
                    Log.d(TAG, "Email: $email")

                    Log.d(TAG, "Nombre: $nombre")
                    Log.d(TAG, "Plan: $plan")
                    Log.d(TAG, "Precio: $precio")

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Error al leer los datos", databaseError.toException())
            }
        })

    }





    override fun onDetach() {
        super.onDetach()
    }

}