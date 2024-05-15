package com.example.colectif.InterfazUsuario.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.colectif.Adapter.AdapterSolicitudes
import com.example.colectif.databinding.FragmentSolicitudesBinding
import com.example.colectif.models.Solicitud
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Fragmento que muestra las solicitudes enviadas por otros usuarios al usuario actual.
 * Utiliza Firebase Realtime Database para recuperar y mostrar las solicitudes.
 */
class SolicitudesFragment: Fragment() {
    private lateinit var binding: FragmentSolicitudesBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var adapterSolicitudes: AdapterSolicitudes
    private lateinit var database: FirebaseDatabase


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSolicitudesBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Este método infla el diseño del fragmento y devuelve la vista correspondiente
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        var listaSolicitudes = ArrayList<Solicitud>()
        val navController = Navigation.findNavController(view)

        // Crear un adaptador para el RecyclerView que esté vinculado a la lista de catálogos y grupos
        adapterSolicitudes = context?.let { AdapterSolicitudes(navController,it,listaSolicitudes) }!!

        // Establecer el adaptador en el RecyclerView
        binding.recyclerSolicitudes.adapter = adapterSolicitudes

        // Configurar un LinearLayoutManager para organizar los elementos verticalmente
        binding.recyclerSolicitudes.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recogerListaSolicitudes()



    }

    override fun onDetach() {
        super.onDetach()
    }

    // Recupera todas las solicitudes, de la base de datos, que tenga el usuario
    private fun recogerListaSolicitudes(){
        var ref = database.getReference("users")
        var ref2 = database.getReference("solicitudes")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                for(i in 1 until snapshot.child(auth.currentUser!!.uid).child("numSolicitudes").value.toString().toInt() + 1){
                    val idSolicitud = snapshot.child(auth.currentUser!!.uid).child("solicitudes").child(i.toString()).value.toString()
                    ref2.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {

                            // Hacer que no aparezcan los nulos / borrados de la bbdd
                            if (snapshot.child(idSolicitud).child("idGrupo").value.toString() != "null" && snapshot.child(idSolicitud).child("idMandatario").value.toString() != "null" && snapshot.child(idSolicitud).child("idReceptor").value.toString() != "null") {

                                // Añade al adapter todas las solicitudes
                                adapterSolicitudes.addSolicitud(
                                    Solicitud(
                                        snapshot.child(idSolicitud).child("id").value.toString(),
                                        snapshot.child(idSolicitud).child("idReceptor").value.toString(),
                                        snapshot.child(idSolicitud).child("idMandatario").value.toString(),
                                        snapshot.child(idSolicitud).child("idGrupo").value.toString()
                                    ))
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }



}