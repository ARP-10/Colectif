package com.example.colectif.InterfazUsuario.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.colectif.databinding.ActivityInicioBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class InicioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInicioBinding
    private lateinit var idUser: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        idUser = intent.getStringExtra("id").toString()



        var database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        var ref = database.getReference("users")



        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.txtNombre.text = snapshot.child(idUser).child("name").value.toString()
                binding.txtApellido.text = snapshot.child(idUser).child("firstSurName").value.toString()
                binding.txtUsuario.text = snapshot.child(idUser).child("userName").value.toString()
                binding.txtCorreo.text = snapshot.child(idUser).child("mail").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })





    }
}