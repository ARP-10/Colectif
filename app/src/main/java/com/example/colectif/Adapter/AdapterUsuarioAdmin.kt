package com.example.colectif.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.colectif.InterfazUsuario.Fragments.VerGrupoAdminFragment
import com.example.colectif.R
import com.example.colectif.models.UsuarioGrupo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterUsuarioAdmin(var navController: NavController,var contexto: Context, var lista:ArrayList<UsuarioGrupo>, ):
    RecyclerView.Adapter<AdapterUsuarioAdmin.MyHolder>() {

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var pagado: Boolean = false

    init{
        lista = ArrayList()
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nombreUsuario: TextView
        var btnEcharGrupo: ImageButton = itemView.findViewById(R.id.btn_echar_grupo)
        var checkBox: CheckBox = itemView.findViewById(R.id.checkbox_usuario)

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

            mostrarMensaje(contexto, "Expulsar del grupo", "¿Deseas expulsar a este usuario del grupo?", usuario.id, usuario.idGrupo, position)

        }
        holder.checkBox.isChecked = usuario.pagado



        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            var ref = database.getReference("groups")
            if (isChecked != usuario.pagado) { // Verifica si el estado ha cambiado
                // Actualiza el valor de "pagado" en la base de datos solo si ha cambiado
                ref.child(usuario.idGrupo).child("users").child(usuario.id).child("pagado").setValue(isChecked)



            }
        }



    }

    override fun getItemCount(): Int {
        return lista.size
    }

    fun addUsuarioAdmin(usuarioGrupo : UsuarioGrupo) {
        if (!usuarioGrupo.id.equals(auth.currentUser!!.uid)) {
            this.lista.add(usuarioGrupo)
            notifyItemInserted(lista.size-1)
        }
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

        lista.removeAt(position)

        // Notificar al adaptador sobre el cambio realizado en la lista de datos
        notifyItemRemoved(position)

        ref.child(usuarioId).removeValue()
        // borrar el grupo al usuario
        ref2.orderByValue().equalTo(idGrupo).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        ref3.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var numUsuariosActual = snapshot.child("numUsuarios").getValue(Int::class.java) ?: 0
                numUsuariosActual-- // Restar 1 al número actual de usuarios
                Log.d("AdapterUsuarioAdmin", "Número de usuarios actual antes de restar: $numUsuariosActual")

                ref3.child("numUsuarios").setValue(numUsuariosActual)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AdapterUsuarioAdmin", "Error al actualizar el número de usuarios: ${error.message}")
            }
        })



    }





}