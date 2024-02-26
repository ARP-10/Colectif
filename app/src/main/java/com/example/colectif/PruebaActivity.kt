package com.example.colectif

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.colectif.databinding.ActivityRegistroBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PruebaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        var ref = database.getReference("users")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var nombre = snapshot.child("nombre").value.toString()
                var primerApellido = snapshot.child("firstSurName").value.toString()
                var usuario = snapshot.child("userName").value.toString()
                var correo = snapshot.child("mail").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })




    }
}