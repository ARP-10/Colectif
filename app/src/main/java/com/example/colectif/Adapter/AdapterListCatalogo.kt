package com.example.colectif.Adapter

import android.content.Context
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

class AdapterListCatalogo (var context: Context, private val recycler_lista_catalogo: ArrayList<CatalogoGrupos>) :
    RecyclerView.Adapter<AdapterListCatalogo.CategoriasViewHolder>() {

    private lateinit var adapter: AdapterListGrupos
    private var listaCompletaTodosGrupos: List<Grupo> = ArrayList()

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
        // Guardar la lista completa cuando se establece el adaptador
        listaCompletaTodosGrupos = catalogoGrupos.grupos

        holder.nombreCatalogo.text = catalogoGrupos.catalogo
        holder.nombreCatalogo.setOnClickListener {
            toggleRecyclerViewVisibility(holder.recyclerGrupos)
        }
        holder.recyclerGrupos.setHasFixedSize(true)
        holder.recyclerGrupos.layoutManager = GridLayoutManager(holder.itemView.context,1, RecyclerView.HORIZONTAL,false)
        adapter = AdapterListGrupos(context,ArrayList<Grupo>())
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

    fun filtrarLista(filtro: String){
        // Si el filtro está vacío, restaurar la lista completa de grupos en cada categoría
        if (filtro.isEmpty()) {
            // TODO: cargar el fragment
/*
            recycler_lista_catalogo.forEach { catalogoGrupos ->
                catalogoGrupos.grupos = ArrayList(listaCompletaTodosGrupos) // Restaurar la lista completa
                }*/
        } else {
            // Filtrar la lista de grupos dentro de cada categoría
            recycler_lista_catalogo.forEach { catalogoGrupos ->
                catalogoGrupos.grupos = catalogoGrupos.grupos.filter { it.nombre.contains(filtro, ignoreCase = true) } as ArrayList<Grupo>
            }
        }
        notifyDataSetChanged()
    }

    private fun toggleRecyclerViewVisibility(recyclerView: RecyclerView) {
        recyclerView.visibility = if (recyclerView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    fun restoreListaCompleta() {
        /*
        recycler_lista_catalogo.clear()
        recycler_lista_catalogo.addAll(listaCompletaTodosGrupos)
        notifyDataSetChanged()*/
    }



}