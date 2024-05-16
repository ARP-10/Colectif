package com.example.colectif.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.colectif.R
import com.example.colectif.models.Solicitud
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Adaptador para mostrar una lista de solicitudes en la pantalla de solicitudes.
 * Este adaptador se encarga de inflar la vista de cada solicitud, asignar la información correspondiente
 * y gestionar las acciones de aceptar y rechazar solicitudes.
 * @param navController El NavController para la navegación entre fragments.
 * @param context El contexto de la aplicación.
 * @param listaSolicitudes La lista de solicitudes a mostrar.
 */

class AdapterSolicitudes(var navController: NavController,var context: Context, var listaSolicitudes: ArrayList<Solicitud>): RecyclerView.Adapter<AdapterSolicitudes.MyHolder>() {

    var haySolicitudes: Boolean = false

    // Clase interna que actúa como ViewHolder para mantener las referencias de las vistas de cada elemento de la lista
    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nombrePersona: TextView
        var nombreGrupo: TextView
        var btnAceptarSoli: Button
        var btnRechazarSoli: Button
        val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        init {
            nombreGrupo = itemView.findViewById(R.id.textViewGrupoX)
            nombrePersona = itemView.findViewById(R.id.textViewPersonaX)
            btnAceptarSoli = itemView.findViewById(R.id.btnAceptarSoli)
            btnRechazarSoli = itemView.findViewById(R.id.btnRechazarSoli)

        }


    }

    // Infla la vista del adapter desde el diseño XML definido
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_solicitudes, parent, false)
        return MyHolder(view)
    }

    // Devulve el número de solicitudes
    override fun getItemCount(): Int {
        return listaSolicitudes.size
    }

    // Se determina la información de cada solicitud de la lista con su respectivo lugar de la vista, aparte de hacer funcionar los componentes
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        var solicitud = listaSolicitudes[position]

        // Se determina si la solicitud existe o no, ya que al eliminarlas puede quedarse como null
        if (solicitud != null) {

            // Se recoge de la base de datos el nombre del usuario que emite la solicitud y el nombre del grupo al que desea unirse
            var ref = holder.database.getReference("users")
            var ref2 = holder.database.getReference("groups")

            // Se recupera el nombre del usuario
            ref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    holder.nombrePersona.text = snapshot.child(solicitud.idMandatario).child("userName").value.toString()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

            // Se recupera el nombre del grupo
            ref2.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    holder.nombreGrupo.text = snapshot.child(solicitud.idGrupo).child("nombre").value.toString()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })



        } else {

        }

        // Se determina la función del botón aceptar
        holder.btnAceptarSoli.setOnClickListener {
            aceptarSolicitud(solicitud.id,solicitud.idReceptor,solicitud.idMandatario,solicitud.idGrupo)
            notifyDataSetChanged()

            // Se reinicia el fragment por problemas de duplicación en el adapter
            navController.navigate(R.id.action_global_inicioFragment)
            navController.navigate(R.id.action_global_solicitudesFragment)
            notifyDataSetChanged()

        }

        // Se determina la función del botón rechazar
        holder.btnRechazarSoli.setOnClickListener {
            rechazarSolicitud(solicitud.id,solicitud.idReceptor)
            notifyDataSetChanged()

            // Se reinicia el fragment por problemas de duplicación en el adapter
            navController.navigate(R.id.action_global_inicioFragment)
            navController.navigate(R.id.action_global_solicitudesFragment)
            notifyDataSetChanged()
        }

    }

    // Añade solicitud a la lista
    fun addSolicitud(solicitud: Solicitud){
        listaSolicitudes.add(solicitud)
        notifyItemInserted(listaSolicitudes.size - 1 )
        haySolicitudes = true
    }

    // Función al aceptar una solicitud
    fun aceptarSolicitud(idSolicitud: String, idAdmin: String, idUser: String, idGrupo: String) {
        var solicitudAceptada1 = false
        var solicitudAceptada2 = false
        val database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("users")
        val ref2 = database.getReference("solicitudes")
        val ref3 = database.getReference("groups")


        //Suma +1 en el número de grupos del usuario y lo añade a su nodo groups
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!solicitudAceptada1) {
                    var numGruposActual = snapshot.child(idUser).child("numGrupos").getValue(Int::class.java) ?: 0
                    numGruposActual++
                    ref.child(idUser).child("numGrupos").setValue(numGruposActual)
                    ref.child(idUser).child("groups").child(numGruposActual.toString())
                        .setValue(idGrupo)

                    // Desvincular el ValueEventListener después de realizar la operación
                    solicitudAceptada1 = true
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // Suma +1 en el número de usuarios que tiene el grupo y lo añade a su nodo users
        ref3.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (!solicitudAceptada2) {
                    var numUsuariosActual = snapshot.child(idGrupo).child("numUsuarios").getValue(Int::class.java) ?: 0
                    numUsuariosActual++

                    ref3.child(idGrupo).child("users").child(idUser).child("id").setValue(idUser)
                    ref3.child(idGrupo).child("users").child(idUser).child("pagado").setValue(false)
                    ref3.child(idGrupo).child("numUsuarios").setValue(numUsuariosActual)

                    // Desvincular el ValueEventListener después de realizar la operación
                    solicitudAceptada2 = true
                }
            }

                override fun onCancelled(error: DatabaseError) {}

        })

        // Elimina la solicitud de la base de datos, tanto del nodo principal Solicitudes como dentro del usuario emisor
        val refSolicitudesUsuario = ref.child(idAdmin).child("solicitudes")
        refSolicitudesUsuario.orderByValue().equalTo(idSolicitud).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        // Aqui elimina dentro del usuario
        ref2.child(idSolicitud).removeValue()
    }

    // Función al rechazar la solicitud
    fun rechazarSolicitud(idSolicitud: String,idAdmin: String){
        var database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        var ref = database.getReference("users")
        var ref2 = database.getReference("solicitudes")
        val refSolicitudesUsuario = ref.child(idAdmin).child("solicitudes")

        // Elimina la solicitud de la base de datos, tanto del nodo principal Solicitudes como dentro del usuario emisor
        refSolicitudesUsuario.orderByValue().equalTo(idSolicitud).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        // Aqui elimina dentro del usuario
        ref2.child(idSolicitud).removeValue()
    }




}