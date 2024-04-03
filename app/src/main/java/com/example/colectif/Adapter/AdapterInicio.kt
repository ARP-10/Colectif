package com.example.colectif.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.colectif.Objetos.ObjetoGrupos
import com.example.colectif.R
import com.example.colectif.models.Grupo
import kotlin.math.log

class AdapterInicio(var contexto: Context, var lista:ArrayList<Grupo>):
    RecyclerView.Adapter<AdapterInicio.MyHolder>() {

        init{
            lista = ArrayList()
        }

    // Para el click del boton
    interface OnItemClickListener {
        fun onItemClick(position: Int, groupId: String)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }


    ///




    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nombreGrupo: TextView
        var plan: TextView
        var admin: TextView
        var boton: Button

        init {
            nombreGrupo = itemView.findViewById(R.id.nombreGrupo)
            plan = itemView.findViewById(R.id.plan)
            admin = itemView.findViewById(R.id.admin)
            boton = itemView.findViewById(R.id.btnVerGrupo)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterInicio.MyHolder {
        val view = LayoutInflater.from(contexto).inflate(R.layout.recycler_inicio, parent, false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val grupo = lista[position]
        holder.nombreGrupo.text = grupo.nombre
        holder.admin.text = grupo.administrador
        holder.plan.text = grupo.plan
        Log.v("grupo", "holaa")

        // Para gestionar el click del boton
        holder.boton.setOnClickListener {
            itemClickListener?.onItemClick(position, grupo.id)
        }
    }


    fun addGrupo(grupo:Grupo){
        this.lista.add(grupo)
        notifyItemInserted(lista.size-1)
    }


}