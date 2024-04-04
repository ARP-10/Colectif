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


class AdapterListGrupos(var cardview_grupos: ArrayList<Grupo>) :
    RecyclerView.Adapter<AdapterListGrupos.GruposViewHolder>() {

        init {
            cardview_grupos = ArrayList()
        }

    inner class GruposViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val imagenGrupo: ImageView = itemView.findViewById(R.id.grupo)
        val administrador: TextView = itemView.findViewById(R.id.text_administrador)
        val plan: TextView = itemView.findViewById(R.id.text_Plan)
        val precio: TextView = itemView.findViewById(R.id.text_precio)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GruposViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_horizontal_grupos, parent, false)
        return GruposViewHolder(view)
    }

    override fun onBindViewHolder(holder: GruposViewHolder, position: Int) {
        var grupo = cardview_grupos[position]
        //holder.imagenGrupo.setImageResource(grupo.imagen)
        holder.administrador.text = grupo.administrador
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