package com.example.colectif.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.colectif.R
import com.example.colectif.models.CatalogoGrupos

class AdapterListCatalogo (private val recycler_lista_catalogo: List<CatalogoGrupos>) :
    RecyclerView.Adapter<AdapterListCatalogo.CategoriasViewHolder>() {

    inner class CategoriasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreCatalogo: TextView = itemView.findViewById(R.id.text_nombreCatalogo)
        val recyclerGrupos: RecyclerView = itemView.findViewById(R.id.recyclerview_gruposCatalogo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriasViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_catalogo, parent, false)
        return CategoriasViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriasViewHolder, position: Int) {
        val catalogoGrupos = recycler_lista_catalogo[position]
        holder.nombreCatalogo.text = catalogoGrupos.catalogo

        holder.recyclerGrupos.setHasFixedSize(true)
        holder.recyclerGrupos.layoutManager = GridLayoutManager(holder.itemView.context, 3)
        val adapter = AdapterListGrupos(catalogoGrupos.grupos)
        holder.recyclerGrupos.adapter = adapter
    }

    override fun getItemCount(): Int {
        return recycler_lista_catalogo.size
    }
}
