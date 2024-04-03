package com.example.colectif.InterfazUsuario.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.colectif.R
import com.example.colectif.databinding.FragmentCrearGrupoBinding
import com.example.colectif.databinding.FragmentVerGrupoBinding
import com.example.colectif.models.Grupo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class VerGrupoFragment : Fragment() {

    private lateinit var binding: FragmentVerGrupoBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

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
        database = FirebaseDatabase.getInstance().getReference("groups")
        /*
        // Recibir el ID
        val groupId = arguments?.getString("groupId")

        groupId?.let { id ->
            val grupoRef = database.child(id)
            grupoRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val grupo = snapshot.getValue(Grupo::class.java)
                        grupo?.let {
                            // Establecer los datos del grupo en los TextView correspondientes
                            binding.txtNombregrupo.text = it.nombre
                            binding.txtAdministrador.text = it.administrador
                            binding.txtApp.text = it.app
                            binding.txtPlan.text = it.plan
                            binding.txtCorreo.text = it.email
                            binding.txtPassword.text = it.contrasenia

                            // Para la imagen del grupo, puedes cargarla desde Firebase Storage si tienes una URL de imagen en tu modelo de datos
                            // Si tienes la URL de la imagen, puedes usar una biblioteca como Glide o Picasso para cargarla en el ImageView
                            // Por ejemplo:
                            // Glide.with(requireContext()).load(it.imagenUrl).into(binding.imgGrupo)
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            })


        }*/
    }
    override fun onDetach() {
        super.onDetach()

    }

}