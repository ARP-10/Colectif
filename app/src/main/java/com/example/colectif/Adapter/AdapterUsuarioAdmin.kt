package com.example.colectif.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.colectif.R
import com.example.colectif.models.UsuarioGrupo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterUsuarioAdmin(var contexto: Context, var lista:ArrayList<UsuarioGrupo>, ):
    RecyclerView.Adapter<AdapterUsuarioAdmin.MyHolder>() {

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
    init{
        lista = ArrayList()
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nombreUsuario: TextView
        var btnEcharGrupo: ImageButton = itemView.findViewById(R.id.btn_echar_grupo)

        init {
            nombreUsuario = itemView.findViewById(R.id.txt_nombre_usuario_admin)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterUsuarioAdmin.MyHolder {
        val view = LayoutInflater.from(contexto).inflate(R.layout.recycler_usuarios_admin, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val usuario = lista[position]
        holder.nombreUsuario.text = usuario.nombreUsuario
        holder.btnEcharGrupo.setOnClickListener {
            // TODO: Activar alerta
            mostrarMensaje(contexto, "Expulsar del grupo", "¿Deseas expulsar a este usuario del grupo?", usuario.id, usuario.idGrupo, position)

        }
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    fun addUsuarioAdmin(usuarioGrupo : UsuarioGrupo) {
        this.lista.add(usuarioGrupo)
        notifyItemInserted(lista.size-1)
    }

    private fun mostrarMensaje(contexto: Context, titulo: String, mensaje: String, usuarioId: String, idGrupo: String, position: Int) {
        val builder = AlertDialog.Builder(contexto)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)

        builder.setPositiveButton("Sí, estoy de acuerdo") { dialog, _ ->
            echarUsuarioDelGrupo(usuarioId, idGrupo, position)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    fun echarUsuarioDelGrupo(usuarioId: String, idGrupo: String, position: Int) {
        val ref = database.getReference("groups").child(idGrupo).child("users")
        val ref2 = database.getReference("users").child(usuarioId).child("groups")
        val ref3 = database.getReference("groups").child(idGrupo)

        ref.orderByValue().equalTo(usuarioId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue()
                    Log.v("timon", snapshot.ref.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        // borrar el grupo al usuario
        ref2.orderByValue().equalTo(idGrupo).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue()
                    Log.v("timon", snapshot.ref.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        ref3.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var numUsuariosActual = snapshot.child("numUsuarios").getValue(Int::class.java) ?: 0
                numUsuariosActual-- // Restar 1 al número actual de usuarios
                Log.d("AdapterUsuarioAdmin", "Número de usuarios actual antes de restar: $numUsuariosActual")

                ref3.child("numUsuarios").setValue(numUsuariosActual)
                // Navegar al fragmento principal
                //val navController = contexto.findNavController(R.id.verGrupoAdminFragment)
                //navController.navigate(R.id.inicioFragment)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AdapterUsuarioAdmin", "Error al actualizar el número de usuarios: ${error.message}")
            }
        })

        notifyItemRemoved(position)

        // TODO: poner aviso antes de echar
    }




}