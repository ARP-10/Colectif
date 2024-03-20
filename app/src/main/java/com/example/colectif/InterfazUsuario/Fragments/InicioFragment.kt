package com.example.colectif.InterfazUsuario.Fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.example.colectif.InterfazUsuario.Activities.LoginActivity
import com.example.colectif.R
import com.example.colectif.databinding.FragmentInicioBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.values
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorageKtxRegistrar
import com.google.firebase.storage.ktx.storage

class InicioFragment: Fragment() {
    private lateinit var binding: FragmentInicioBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var sharedP: SharedPreferences
    private var sharedPref: String = "com.example.colectif"


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        var idUser = auth.currentUser!!.uid
        database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        var ref = database.getReference("users")
        binding.imagenUsuario.setImageResource(R.drawable.foto_perfil)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.nombreUsuario.text =
                    snapshot.child(idUser).child("userName").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        binding.btnCerrarSesion.setOnClickListener {
            logOut()
        }
    }

    override fun onDetach() {
        super.onDetach()
    }

    private fun logOut(){
        auth.signOut()
        val sharedP: SharedPreferences
        sharedP = requireContext().getSharedPreferences("com.example.colectif",Context.MODE_PRIVATE)
        val editor = sharedP.edit()
        editor.putBoolean("estado",false)
        editor.apply()
        startActivity(Intent(context, LoginActivity::class.java))
        activity?.finish()
    }



}