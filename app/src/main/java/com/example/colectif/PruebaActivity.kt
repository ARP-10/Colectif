package com.example.colectif

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.colectif.databinding.ActivityPruebaBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PruebaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPruebaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPruebaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        var ref = database.getReference("users")



        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // TODO: Buscar la forma de automatizar la busqueda del ID
                binding.txtNombre.text = snapshot.child("HGSU00AinkTste6en3IRjUXasUl1").child("name").value.toString()
                binding.txtApellido.text = snapshot.child("HGSU00AinkTste6en3IRjUXasUl1").child("firstSurName").value.toString()
                binding.txtUsuario.text = snapshot.child("HGSU00AinkTste6en3IRjUXasUl1").child("userName").value.toString()
                binding.txtCorreo.text = snapshot.child("HGSU00AinkTste6en3IRjUXasUl1").child("mail").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })





    }
}