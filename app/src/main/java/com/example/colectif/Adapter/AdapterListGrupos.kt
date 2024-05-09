package com.example.colectif.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.colectif.R
import com.example.colectif.models.Grupo
import com.example.colectif.models.Solicitud
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Collections


class AdapterListGrupos(var context: Context, var cardview_grupos: ArrayList<Grupo>) :
    RecyclerView.Adapter<AdapterListGrupos.GruposViewHolder>() {

    private var listaCompleta: ArrayList<Grupo> = cardview_grupos

    interface OnInfoButtonClickListener {
        fun onInfoButtonClick(grupo: Grupo)
    }
    var infoButtonClickListener: OnInfoButtonClickListener? = null

    init {
        cardview_grupos = ArrayList()
    }

    inner class GruposViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenGrupo: ImageView = itemView.findViewById(R.id.grupo)
        val administrador: TextView = itemView.findViewById(R.id.text_administrador)
        val plan: TextView = itemView.findViewById(R.id.text_Plan)
        val nombreGrupo: TextView = itemView.findViewById(R.id.text_grupo)
        val precio: TextView = itemView.findViewById(R.id.text_precio)
        val imageButton: AppCompatImageButton = itemView.findViewById(R.id.button_unirse_grupo)
        val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        val imagenUsuario: ImageView = itemView.findViewById(R.id.imagenUsuarioGrupo)
        val btnInfoGrupo: AppCompatImageButton = itemView.findViewById(R.id.btn_info_grupo)

        init {
            btnInfoGrupo.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val grupo = cardview_grupos[position]
                    // Obtener el NavController desde el contexto del itemView
                    val navController = Navigation.findNavController(itemView)

                    // Crear un Bundle para pasar el id del grupo a VerInfoGrupoFragment
                    val bundle = bundleOf("idGrupo" to grupo.id)

                    // Navegar a VerInfoGrupoFragment con el id del grupo
                    navController.navigate(R.id.action_listaGruposFragment_to_verInfoGrupoFragment, bundle)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GruposViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_grupos, parent, false)
        return GruposViewHolder(view)
    }

    override fun onBindViewHolder(holder: GruposViewHolder, position: Int) {
        var grupo = cardview_grupos[position]
        var app = grupo.app
        var auth = FirebaseAuth.getInstance()

        when (app) {
            "Netflix" -> holder.imagenGrupo.setImageResource(R.drawable.netflix)
            "Disney +" -> holder.imagenGrupo.setImageResource(R.drawable.disney)
            "Amazon Prime" -> holder.imagenGrupo.setImageResource(R.drawable.amazon)
            "Spotify" -> holder.imagenGrupo.setImageResource(R.drawable.spotify)
            else -> {
                holder.imagenGrupo.setImageResource(R.drawable.error)
            }
        }

        var ref = holder.database.getReference("users")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                holder.administrador.text = snapshot.child(grupo.administrador).child("name").value.toString()
                Glide.with(context).load(snapshot.child(grupo.administrador).child("imagen").value.toString().toUri()).into(holder.imagenUsuario)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })



        holder.plan.text = grupo.plan
        holder.precio.text = grupo.precio
        holder.nombreGrupo.text = grupo.nombre
        holder.imageButton.setOnClickListener {
            enviarSolicitud(auth.currentUser!!.uid, grupo.administrador, grupo.id, holder.itemView)
        }


    }

    override fun getItemCount(): Int {
        return cardview_grupos.size
    }

    fun addGrupo(grupo : Grupo){
        cardview_grupos.add(grupo)
        notifyItemInserted(cardview_grupos.size - 1)
    }

    /*
    fun filtrarLista(filtro: String) {
        if (filtro == "Todos") {
            cardview_grupos = listaCompleta
        } else if(filtro == "Más antiguos"){
            cardview_grupos = ArrayList(listaCompleta.sortedBy { it.fecha })
        } else if(filtro == "Más recientes"){
            cardview_grupos = ArrayList(listaCompleta.sortedByDescending { it.fecha })
        }
        notifyDataSetChanged()
    }*/

    fun enviarSolicitud(idUser: String, idAdmin: String, idGrupo: String, view: View){
        var solicitud = Solicitud(idAdmin,idUser,idGrupo)
        var database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("solicitudes")
        var nuevaId= ""

        val newRef = ref.push()
        newRef.setValue(solicitud)
        nuevaId = newRef.key!!
        ref.child(nuevaId).child("id").setValue(nuevaId)

        var ref2 = database.getReference("users")
        ref2.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var numSolicitudActual = snapshot.child(idAdmin).child("numSolicitudes").getValue(Int::class.java) ?: 0
                numSolicitudActual++
                ref2.child(idAdmin).child("numSolicitudes").setValue(numSolicitudActual)
                database.getReference("users").child(idAdmin).child("solicitudes").child(numSolicitudActual.toString()).setValue(nuevaId)

                Snackbar.make(view, "Solicitud enviada exitosamente", Snackbar.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })



    }

    fun filtrarLista(texto: String) {
        listaCompleta = if (texto.isEmpty()) {
            listaCompleta // Si no hay texto, mostrar todos los grupos
        } else {
            // Filtrar grupos cuyo nombre contenga el texto ingresado
            listaCompleta.filter { it.nombre.contains(texto, ignoreCase = true) } as ArrayList<Grupo>
        }
        notifyDataSetChanged() // Notificar al Adapter que la lista ha cambiado
    }


}