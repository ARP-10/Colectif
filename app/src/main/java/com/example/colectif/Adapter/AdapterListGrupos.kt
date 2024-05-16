package com.example.colectif.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageButton
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

/**
 * Adaptador para mostrar una lista de grupos en la pantalla de lista de grupos.
 * Este adaptador se encarga de inflar la vista de cada grupo, asignar la información correspondiente
 * y gestionar el envío de solicitudes para unirse a un grupo.
 * @param context El contexto de la aplicación.
 * @param cardview_grupos La lista de grupos a mostrar.
 */

class AdapterListGrupos(var context: Context, var cardview_grupos: ArrayList<Grupo>) :
    RecyclerView.Adapter<AdapterListGrupos.GruposViewHolder>() {

    init {
        cardview_grupos = ArrayList()
    }

    // Clase interna que actúa como ViewHolder para mantener las referencias de las vistas de cada elemento de la lista
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

        // Se incia el botón para visualizar la información del grupo
        init {
            btnInfoGrupo.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {

                    // Se determina en que grupo específico se está pulsando con su posicion en el adapter
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

    // Infla la vista del adapter desde el diseño XML definido
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GruposViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_grupos, parent, false)
        return GruposViewHolder(view)
    }

    // Se determina la información de cada grupo de la lista con su respectivo lugar de la vista, aparte de hacer funcionar los componentes
    override fun onBindViewHolder(holder: GruposViewHolder, position: Int) {
        var grupo = cardview_grupos[position]
        var app = grupo.app
        var auth = FirebaseAuth.getInstance()

        // Dependiendo de la aplicación se colocará una imagen
        when (app) {
            "Netflix" -> holder.imagenGrupo.setImageResource(R.drawable.netflix2)
            "Disney +" -> holder.imagenGrupo.setImageResource(R.drawable.disney2)
            "Amazon Prime" -> holder.imagenGrupo.setImageResource(R.drawable.amazon)
            "Spotify" -> holder.imagenGrupo.setImageResource(R.drawable.spotify)
            else -> {
                holder.imagenGrupo.setImageResource(R.drawable.error)
            }
        }

        // Se recupera de la base de datos el nombre y la imagen del administrador del grupo con su id
        var ref = holder.database.getReference("users")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                holder.administrador.text = snapshot.child(grupo.administrador).child("userName").value.toString()
                Glide.with(context).load(snapshot.child(grupo.administrador).child("imagen").value.toString().toUri()).into(holder.imagenUsuario)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        // Se sigue colocando los datos en sus respectivos lugares
        holder.plan.text = grupo.plan
        holder.precio.text = grupo.precio
        holder.nombreGrupo.text = grupo.nombre

        // Es el botón que se encarga de mandar solicitudes
        holder.imageButton.setOnClickListener {


            comprobarSolicitudPendiente(auth.currentUser!!.uid, grupo.id, holder.itemView,grupo.administrador) // Primero se comprueba si no hay ya una solicitud pendiente
        }


    }

    // Devulve el número de grupos
    override fun getItemCount(): Int {
        return cardview_grupos.size
    }

    // Añade grupos a la lista
    fun addGrupo(grupo : Grupo){
        cardview_grupos.add(grupo)
        notifyItemInserted(cardview_grupos.size - 1)
    }

    // Hace aparecer un cuadro de diálogo para confirmar si el usuario desea unirse a ese grupo y en ese caso, mandarle la solicitud
    private fun mostrarMensaje(contexto: Context, titulo: String, mensaje: String, usuarioId: String, idGrupo: String, view: View, adminId: String) {
        val builder = AlertDialog.Builder(contexto)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)

        builder.setPositiveButton("Sí, quiero enviar una solicitud") { dialog, _ ->
            enviarSolicitud(usuarioId, adminId, idGrupo, view)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }


    // Crea la solicitud en la base de datos y se lo envía al admin del grupo
    fun enviarSolicitud(idUser: String, idAdmin: String, idGrupo: String, view: View){
        var solicitud = Solicitud(idAdmin,idUser,idGrupo)
        var database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("solicitudes")
        var nuevaId= ""

        // Se guarda la Solicitud en el nodo Solicitudes y la nueva id generada se guarda en la variable nuevaId, para añadirsela a la propia solicitud
        val newRef = ref.push()
        newRef.setValue(solicitud)
        nuevaId = newRef.key!!
        ref.child(nuevaId).child("id").setValue(nuevaId)

        // Actualiza el número de solicitudes del administrador
        var ref2 = database.getReference("users")
        ref2.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var numSolicitudActual = snapshot.child(idAdmin).child("numSolicitudes").getValue(Int::class.java) ?: 0
                numSolicitudActual++
                ref2.child(idAdmin).child("numSolicitudes").setValue(numSolicitudActual)

                // Guarda la solicitud en el nodo solicitudes del administrador
                database.getReference("users").child(idAdmin).child("solicitudes").child(numSolicitudActual.toString()).setValue(nuevaId)

                Snackbar.make(view, "Solicitud enviada exitosamente", Snackbar.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })



    }

    // Comprueba a través de la base de datos que no existe una solicitud igual que la que se quiere enviar
    fun comprobarSolicitudPendiente(userId: String, idGrupo: String, view: View, adminId: String) {
        val database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("solicitudes")

        // Buscar solicitudes pendientes para el usuario y el grupo especificado
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var solicitudPendiente = false
                for (solicitudSnapshot in snapshot.children) {

                    // Recoge la información de la base de datos
                    val grupoId = solicitudSnapshot.child("idGrupo").value.toString()
                    val receptorId = solicitudSnapshot.child("idReceptor").value.toString()
                    val mandatarioId = solicitudSnapshot.child("idMandatario").value.toString()

                    // Determina si la solicitud ya existe
                    if (grupoId == idGrupo && mandatarioId == userId && receptorId == adminId) {
                        solicitudPendiente = true
                        break
                    }
                }
                if (solicitudPendiente) {

                    // Si ya existe la solicitud
                    Snackbar.make(view, "Ya has enviado una solicitud a este grupo", Snackbar.LENGTH_SHORT).show()
                } else {
                    // Si no hay solicitud pendiente, pedir confirmación de que se desea enviar una solicitud
                    mostrarMensaje(context, "Unirse al grupo", "¿Deseas enviar una solicitud a este grupo?", userId, idGrupo, view, adminId)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(view, "Error al comprobar las solicitudes pendientes", Snackbar.LENGTH_SHORT).show()
            }
        })
    }


}