package com.example.colectif.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.colectif.R
import com.example.colectif.models.Grupo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AdapterListGrupos(var cardview_grupos: ArrayList<Grupo>) :
    RecyclerView.Adapter<AdapterListGrupos.GruposViewHolder>() {

        init {
            cardview_grupos = ArrayList()
        }

    inner class GruposViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenGrupo: ImageView = itemView.findViewById(R.id.grupo)
        val administrador: TextView = itemView.findViewById(R.id.text_administrador)
        val plan: TextView = itemView.findViewById(R.id.text_Plan)
        val precio: TextView = itemView.findViewById(R.id.text_precio)
        val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")




    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GruposViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_grupos, parent, false)
        return GruposViewHolder(view)
    }

    override fun onBindViewHolder(holder: GruposViewHolder, position: Int) {
        var grupo = cardview_grupos[position]
        var app = grupo.app
        if(app.equals("Netflix")){
            holder.imagenGrupo.setImageResource(R.drawable.netflix)
        }
        if(app.equals("Disney +")){
            holder.imagenGrupo.setImageResource(R.drawable.disney)
        }
        if(app.equals("Amazon Prime")){
            holder.imagenGrupo.setImageResource(R.drawable.amazon)
        }
        if(app.equals("Spotify")){
            holder.imagenGrupo.setImageResource(R.drawable.spotify)
        }

        var ref = holder.database.getReference("users")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                holder.administrador.text = "Admin: " + snapshot.child(grupo.administrador).child("name").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })



        holder.plan.text = grupo.plan
        holder.precio.text = grupo.precio


    }

    override fun getItemCount(): Int {
        return cardview_grupos.size
    }

    fun addGrupo(grupo : Grupo){
        cardview_grupos.add(grupo)
        notifyItemInserted(cardview_grupos.size - 1)
    }


}