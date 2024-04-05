package com.example.colectif.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.colectif.R
import com.example.colectif.models.CatalogoGrupos
import com.example.colectif.models.Grupo

class AdapterListCatalogo (private val recycler_lista_catalogo: ArrayList<CatalogoGrupos>) :
    RecyclerView.Adapter<AdapterListCatalogo.CategoriasViewHolder>() {

        private lateinit var adapter: AdapterListGrupos

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
        holder.recyclerGrupos.layoutManager = GridLayoutManager(holder.itemView.context,3, RecyclerView.HORIZONTAL,false)
        adapter = AdapterListGrupos(ArrayList<Grupo>())
        for (i in 0 until catalogoGrupos.grupos.size){
            adapter.addGrupo(catalogoGrupos.grupos[i])
        }
        holder.recyclerGrupos.adapter = adapter

    }

    override fun getItemCount(): Int {
        return recycler_lista_catalogo.size
    }

    fun addCatalogo(catalogoGrupos: CatalogoGrupos){
        recycler_lista_catalogo.add(catalogoGrupos)
        notifyItemInserted(recycler_lista_catalogo.size-1)
    }

}
