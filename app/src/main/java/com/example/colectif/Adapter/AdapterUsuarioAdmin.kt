package com.example.colectif.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.colectif.InterfazUsuario.Fragments.VerGrupoAdminFragment
import com.example.colectif.R
import com.example.colectif.models.UsuarioGrupo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterUsuarioAdmin(var contexto: Context, var lista:ArrayList<UsuarioGrupo>):
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
            echarUsuarioDelGrupo(usuario.id, usuario.idGrupo, position)
        }
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    fun addUsuarioAdmin(usuarioGrupo : UsuarioGrupo) {
        this.lista.add(usuarioGrupo)
        notifyItemInserted(lista.size-1)
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
                TODO("Not yet implemented")
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
                numUsuariosActual--
                ref3.child("numUsuarios").setValue(numUsuariosActual)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        notifyItemRemoved(position)

        // TODO: poner aviso antes de echar
    }

}