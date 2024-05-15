package com.example.colectif.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.colectif.R
import com.example.colectif.models.CatalogoGrupos
import com.example.colectif.models.Grupo

/**
 * Adaptador para mostrar una lista de catálogos de grupos en la pantalla de lista de grupos.
 * Este adaptador se encarga de inflar la vista de cada catálogo, asignar la información correspondiente
 * y gestionar la visibilidad de un RecyclerView secundario que muestra los grupos dentro de cada catálogo.
 * @param context El contexto de la aplicación.
 * @param recycler_lista_catalogo La lista de catálogos de grupos a mostrar.
 */
class AdapterListCatalogo (var context: Context, private val recycler_lista_catalogo: ArrayList<CatalogoGrupos>) :
    RecyclerView.Adapter<AdapterListCatalogo.CategoriasViewHolder>() {

    private lateinit var adapter: AdapterListGrupos
    private var listaCompletaTodosGrupos: List<Grupo> = ArrayList()


    // Clase interna que actúa como ViewHolder para mantener las referencias de las vistas de cada elemento de la lista
    inner class CategoriasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreCatalogo: TextView = itemView.findViewById(R.id.text_nombreCatalogo)
        val recyclerGrupos: RecyclerView = itemView.findViewById(R.id.recyclerview_gruposCatalogo)
    }

    // Infla la vista del adapter desde el diseño XML definido
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriasViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_catalogo, parent, false)
        return CategoriasViewHolder(view)
    }

    // Se determina la información de cada catalogo de la lista con su respectivo lugar de la vista y se llama al segundo recycler horizontal para los grupos
    override fun onBindViewHolder(holder: CategoriasViewHolder, position: Int) {

        val catalogoGrupos = recycler_lista_catalogo[position]

        // Guardar la lista completa cuando se establece el adaptador
        listaCompletaTodosGrupos = catalogoGrupos.grupos
        holder.nombreCatalogo.text = catalogoGrupos.catalogo

        // Cada vez que se pulse en el nombre desaparecera o aparecera el segundo recycler
        holder.nombreCatalogo.setOnClickListener {
            toggleRecyclerViewVisibility(holder.recyclerGrupos)
        }

        // Se genera el segundo adapter y se añade todos los grupos correspondientes del catálogo
        holder.recyclerGrupos.setHasFixedSize(true)
        holder.recyclerGrupos.layoutManager = GridLayoutManager(holder.itemView.context,1, RecyclerView.HORIZONTAL,false)
        adapter = AdapterListGrupos(context,ArrayList<Grupo>())
        for (i in 0 until catalogoGrupos.grupos.size){
            adapter.addGrupo(catalogoGrupos.grupos[i])
        }
        holder.recyclerGrupos.adapter = adapter




    }

    // Devuelve el número de catálogos
    override fun getItemCount(): Int {
        return recycler_lista_catalogo.size
    }

    // Añade un catálogo junto a sus grupos
    fun addCatalogo(catalogoGrupos: CatalogoGrupos){
        recycler_lista_catalogo.add(catalogoGrupos)
        notifyItemInserted(recycler_lista_catalogo.size-1)
    }

    fun filtrarLista(filtro: String){

        // Si el filtro está vacío, restaurar la lista completa de grupos en cada categoría
        if (filtro.isEmpty()) {
        } else {

            // Filtrar la lista de grupos dentro de cada categoría
            recycler_lista_catalogo.forEach { catalogoGrupos ->
                catalogoGrupos.grupos = catalogoGrupos.grupos.filter { it.nombre.contains(filtro, ignoreCase = true) } as ArrayList<Grupo>
            }
        }
        notifyDataSetChanged()
    }

    // Cambia la visibilidad del segundo recycler, por si se ha pulsado el nombre del catálogo
    private fun toggleRecyclerViewVisibility(recyclerView: RecyclerView) {
        recyclerView.visibility = if (recyclerView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }



}