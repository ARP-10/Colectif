package com.example.colectif.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.colectif.R
import com.example.colectif.models.Grupo

/**
 * Adaptador para mostrar una lista de grupos en la pantalla de inicio.
 * Este adaptador se encarga de inflar la vista del grupo, asignar la información de cada grupo a su lugar en la vista
 * y gestionar el clic en el botón para ver más detalles del grupo.
 * @param contexto El contexto de la aplicación.
 * @param lista La lista de grupos a mostrar.
 */

class AdapterInicio(var contexto: Context, var lista:ArrayList<Grupo>):
    RecyclerView.Adapter<AdapterInicio.MyHolder>() {


        init{
            lista = ArrayList()
        }
    private var listaCompleta: ArrayList<Grupo> = lista

    // Interfaz para gestionar el clic en el botón de ver el grupo
    interface OnItemClickListener {
        fun onItemClick(position: Int, groupId: String)
    }

    private var itemClickListener: OnItemClickListener? = null

    // Es la función listener para que InicioFragment llame al botón
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }

    // Clase interna que actúa como ViewHolder para mantener las referencias de las vistas de cada elemento de la lista
    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nombreGrupo: TextView
        var plan: TextView
        var admin: TextView
        var boton: AppCompatImageButton

        init {
            nombreGrupo = itemView.findViewById(R.id.nombreGrupo)
            plan = itemView.findViewById(R.id.plan)
            admin = itemView.findViewById(R.id.admin)
            boton = itemView.findViewById(R.id.btnVerGrupo)
        }


    }

    // Infla la vista del adapter desde el diseño XML definido
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterInicio.MyHolder {
        val view = LayoutInflater.from(contexto).inflate(R.layout.recycler_inicio, parent, false)
        return MyHolder(view)
    }

    // Devuelve el números de grupos que hay en la lista
    override fun getItemCount(): Int {
        return lista.size
    }

    // Se determina la información de cada grupo de la lista con su respectivo lugar de la vista, aparte de hacer funcionar los componentes
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val grupo = lista[position]
        holder.nombreGrupo.text = grupo.nombre
        holder.admin.text = grupo.administrador
        holder.plan.text = grupo.plan

        // Cambia el icono segun la app
        val imagenGrupo = holder.itemView.findViewById<ImageView>(R.id.img_logo_grupo)
        val imagen = when (grupo.app) {
            "Netflix" -> R.drawable.netflix2
            "Amazon Prime" -> R.drawable.amazon2
            "Spotify" -> R.drawable.spotify2
            "Disney +" -> R.drawable.disney2
            else -> R.drawable.error
        }
        imagenGrupo.setImageResource(imagen)

        // Gestionar el clic del botón para ver el grupo
        holder.boton.setOnClickListener {
            itemClickListener?.onItemClick(position, grupo.id)

        }
    }

    // Agrega grupos a la lista del adapter y notifica su inserción
    fun addGrupo(grupo:Grupo){
        this.lista.add(grupo)
        notifyItemInserted(lista.size-1)
    }

    // A través del Spinner del InicioFragment, filtra los grupos por aplicación
    fun filtrarLista(app : String){
        if(app.equals("Todos")){
            this.lista = listaCompleta
        } else {
            this.lista = listaCompleta.filter{  //Filtrado de la lista
                it.app.equals(app, true)
            } as ArrayList<Grupo>
        }
        notifyDataSetChanged()
    }


}