package com.example.colectif.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.colectif.R

class AdapterUsuarioAdmin(var contexto: Context, var lista:ArrayList<String>):
    RecyclerView.Adapter<AdapterUsuarioAdmin.MyHolder>() {

    init{
        lista = ArrayList()
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nombreUsuario: TextView

        init {
            nombreUsuario = itemView.findViewById(R.id.txt_nombre_usuario_admin)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterUsuarioAdmin.MyHolder {
        val view = LayoutInflater.from(contexto).inflate(R.layout.recycler_usuarios_admin, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val nombre = lista[position]
        holder.nombreUsuario.text = nombre
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    fun addUsuarioAdmin(nombre : String) {
        this.lista.add(nombre)
        notifyItemInserted(lista.size-1)
    }
}