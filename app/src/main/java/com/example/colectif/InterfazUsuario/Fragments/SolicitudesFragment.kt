package com.example.colectif.InterfazUsuario.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.colectif.Adapter.AdapterSolicitudes
import com.example.colectif.databinding.FragmentSolicitudesBinding
import com.example.colectif.models.Solicitud
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        var listaSolicitudes = ArrayList<Solicitud>()
        adapterSolicitudes = AdapterSolicitudes(listaSolicitudes)
        binding.recyclerSolicitudes.adapter = adapterSolicitudes
        binding.recyclerSolicitudes.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recogerListaSolicitudes()



    }

    override fun onDetach() {
        super.onDetach()
    }

    private fun recogerListaSolicitudes(){
        var ref = database.getReference("users")
        var ref2 = database.getReference("solicitudes")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(i in 0 until snapshot.child(auth.currentUser!!.uid).child("numSolicitudes").value.toString().toInt()){
                    val idSolicitud = snapshot.child(auth.currentUser!!.uid).child("solicitudes").child(i.toString()).value.toString()
                    ref2.addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            adapterSolicitudes.addSolicitud(
                                Solicitud(
                                snapshot.child(idSolicitud).child("idReceptor").value.toString(),
                                snapshot.child(idSolicitud).child("idMandatario").value.toString(),
                                snapshot.child(idSolicitud).child("idGrupo").value.toString()
                            ))
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