package com.example.colectif.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.colectif.R
import com.example.colectif.models.Grupo


class AdapterListGrupos(private val cardview_grupos: List<Grupo>) :
    RecyclerView.Adapter<AdapterListGrupos.GruposViewHolder>() {

    inner class GruposViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenGrupo: ImageView = itemView.findViewById(R.id.grupo)
        val administrador: TextView = itemView.findViewById(R.id.text_administrador)
        val plan: TextView = itemView.findViewById(R.id.text_Plan)
        val precio: TextView = itemView.findViewById(R.id.text_precio)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GruposViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_grupos, parent, false)
        return GruposViewHolder(view)
    }

    override fun onBindViewHolder(holder: GruposViewHolder, position: Int) {
        holder.imagenGrupo.setImageResource(cardview_grupos[position].imagen)
        holder.administrador.text = cardview_grupos[position].administrador
        holder.plan.text = cardview_grupos[position].plan
        holder.precio.text = cardview_grupos[position].precio
    }

    override fun getItemCount(): Int {
        return cardview_grupos.size
    }

}