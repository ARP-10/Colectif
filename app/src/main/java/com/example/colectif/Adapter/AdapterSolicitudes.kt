package com.example.colectif.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.colectif.InterfazUsuario.Fragments.SolicitudesFragment
import com.example.colectif.R
import com.example.colectif.interfaces.SolicitudListener
import com.example.colectif.models.Solicitud
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AdapterSolicitudes(var navController: NavController,var context: Context, var listaSolicitudes: ArrayList<Solicitud>): RecyclerView.Adapter<AdapterSolicitudes.MyHolder>() {

    var haySolicitudes: Boolean = false


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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_solicitudes, parent, false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return listaSolicitudes.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        var solicitud = listaSolicitudes[position]
        if (solicitud != null) {
            var ref = holder.database.getReference("users")
            var ref2 = holder.database.getReference("groups")
            ref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    holder.nombrePersona.text = snapshot.child(solicitud.idMandatario).child("name").value.toString()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
            ref2.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    holder.nombreGrupo.text = snapshot.child(solicitud.idGrupo).child("nombre").value.toString()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })



        } else {

        }

        holder.btnAceptarSoli.setOnClickListener {
            aceptarSolicitud(solicitud.id,solicitud.idReceptor,solicitud.idMandatario,solicitud.idGrupo)
            notifyDataSetChanged()
            navController.navigate(R.id.action_global_inicioFragment)
            navController.navigate(R.id.action_global_solicitudesFragment)
            notifyDataSetChanged()

        }

        holder.btnRechazarSoli.setOnClickListener {
            rechazarSolicitud(solicitud.id,solicitud.idReceptor)
            notifyDataSetChanged()
            navController.navigate(R.id.action_global_inicioFragment)
            navController.navigate(R.id.action_global_solicitudesFragment)
            notifyDataSetChanged()
        }

    }


    fun addSolicitud(solicitud: Solicitud){
        listaSolicitudes.add(solicitud)
        notifyItemInserted(listaSolicitudes.size - 1 )
        haySolicitudes = true

        (context as? SolicitudListener)?.onSolicitudesActualizadas(haySolicitudes)
    }

    fun aceptarSolicitud(idSolicitud: String, idAdmin: String, idUser: String, idGrupo: String) {
        var solicitudAceptada1 = false
        var solicitudAceptada2 = false
        val database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("users")
        val ref2 = database.getReference("solicitudes")
        val ref3 = database.getReference("groups")

        // Verificar si la solicitud ya ha sido aceptada
        // Si la solicitud ya ha sido aceptada, salir del método
        // Esto es importante para evitar operaciones innecesarias en la base de datos
        // y para evitar que se disparen múltiples veces las actualizaciones

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!solicitudAceptada1) {
                    var numGruposActual =
                        snapshot.child(idUser).child("numGrupos").getValue(Int::class.java) ?: 0
                    numGruposActual++
                    ref.child(idUser).child("numGrupos").setValue(numGruposActual)
                    ref.child(idUser).child("groups").child(numGruposActual.toString())
                        .setValue(idGrupo)

                    // Desvincular el ValueEventListener después de realizar la operación
                    solicitudAceptada1 = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error de manera adecuada
            }
        })

        ref3.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (!solicitudAceptada2) {
                    var numUsuariosActual =
                        snapshot.child(idGrupo).child("numUsuarios").getValue(Int::class.java) ?: 0
                    numUsuariosActual++

                    ref3.child(idGrupo).child("users").child(idUser).child("id").setValue(idUser)
                    ref3.child(idGrupo).child("users").child(idUser).child("pagado").setValue(false)
                    ref3.child(idGrupo).child("numUsuarios").setValue(numUsuariosActual)

                    // Desvincular el ValueEventListener después de realizar la operación
                    solicitudAceptada2 = true
                }
            }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar el error de manera adecuada
                }

        })

        val refSolicitudesUsuario = ref.child(idAdmin).child("solicitudes")
        refSolicitudesUsuario.orderByValue().equalTo(idSolicitud).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue()
                    Log.v("timon3", snapshot.ref.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        ref2.child(idSolicitud).removeValue()
    }

    fun rechazarSolicitud(idSolicitud: String,idAdmin: String){
        var database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        var ref = database.getReference("users")
        var ref2 = database.getReference("solicitudes")
        val refSolicitudesUsuario = ref.child(idAdmin).child("solicitudes")
        refSolicitudesUsuario.orderByValue().equalTo(idSolicitud).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue()
                    Log.v("timon", snapshot.ref.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        ref2.child(idSolicitud).removeValue()


    }




}