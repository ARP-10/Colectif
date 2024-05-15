package com.example.colectif.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.colectif.R
import android.content.Context

/**
 * Adaptador para mostrar una lista de nombres de usuarios, en la pantalla de ver grupos como usuario.
 * Este adaptador se encarga de inflar la vista de cada elemento de la lista y mostrar el nombre del usuario correspondiente.
 * @param contexto El contexto de la aplicación.
 * @param lista La lista de nombres de usuarios a mostrar.
 */
class AdapterUsuarios (var contexto: Context, var lista:ArrayList<String>):
    RecyclerView.Adapter<AdapterUsuarios.MyHolder>() {

    init{
        lista = ArrayList()
    }

    // Clase interna que actúa como ViewHolder para mantener las referencias de las vistas de cada elemento de la lista
    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nombreUsuario: TextView

        init {
            nombreUsuario = itemView.findViewById(R.id.txt_nombre_usuario)
        }
    }

    // Infla la vista del adapter desde el diseño XML definido
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterUsuarios.MyHolder {
        val view = LayoutInflater.from(contexto).inflate(R.layout.recycler_usuarios, parent, false)
        return MyHolder(view)
    }

    // Se determina el nombre de cada usuario de la lista con su respectivo lugar de la vista
    override fun onBindViewHolder(holder: AdapterUsuarios.MyHolder, position: Int) {
        val nombre = lista[position]
        holder.nombreUsuario.text = nombre
    }

    // Devuelve el número de usuarios que hay en la lista
    override fun getItemCount(): Int {
        return lista.size
    }

    // Añade un nuevo nombre a la lista de usuario
    fun addUsuario(nombre : String) {
        this.lista.add(nombre)
        notifyItemInserted(lista.size-1)
    }


}