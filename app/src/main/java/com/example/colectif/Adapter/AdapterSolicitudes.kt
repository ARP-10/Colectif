package com.example.colectif.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.colectif.R
import com.example.colectif.models.Solicitud
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterSolicitudes(var listaSolicitudes: ArrayList<Solicitud>): RecyclerView.Adapter<AdapterSolicitudes.MyHolder>() {
    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nombrePersona: TextView
        var nombreGrupo: TextView
        var btnAceptarSoli: Button
        var btnRechazarSoli: Button
        val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")

        init {
            nombreGrupo = itemView.findViewById(R.id.textViewGrupoX)
            nombrePersona = itemView.findViewById(R.id.textViewPersonaX)
            btnAceptarSoli = itemView.findViewById(R.id.btnAceptarSoli)
            btnRechazarSoli = itemView.findViewById(R.id.btnRechazarSoli)

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_solicitudes, parent, false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return listaSolicitudes.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        var solicitud = listaSolicitudes[position]
        var ref = holder.database.getReference("users")
        var ref2 = holder.database.getReference("groups")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                holder.nombrePersona.text = snapshot.child(solicitud.idMandatario).child("name").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        ref2.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                holder.nombreGrupo.text = snapshot.child(solicitud.idGrupo).child("nombre").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    fun addSolicitud(solicitud: Solicitud){
        listaSolicitudes.add(solicitud)
        notifyItemInserted(listaSolicitudes.size - 1 )
    }
}