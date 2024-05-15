package com.example.colectif.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.colectif.R
import com.example.colectif.models.UsuarioGrupo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Adaptador para gestionar la visualización y acciones de los usuarios dentro de un grupo, en la pantalla de ver grupos como administrador.
 * Este adaptador se encarga de inflar la vista de cada usuario en el grupo, mostrar su información y permitir al administrador
 * expulsar usuarios del grupo, así como actualizar su estado de pago.
 * @param contexto El contexto de la aplicación.
 * @param lista La lista de usuarios del grupo.
 */
class AdapterUsuarioAdmin(var contexto: Context, var lista:ArrayList<UsuarioGrupo>, ):
    RecyclerView.Adapter<AdapterUsuarioAdmin.MyHolder>() {

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    init{
        lista = ArrayList()
    }

    // Clase interna que actúa como ViewHolder para mantener las referencias de las vistas de cada elemento de la lista
    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nombreUsuario: TextView
        var btnEcharGrupo: ImageButton = itemView.findViewById(R.id.btn_echar_grupo)
        var checkBox: CheckBox = itemView.findViewById(R.id.checkbox_usuario)

        init {
            nombreUsuario = itemView.findViewById(R.id.txt_nombre_usuario_admin)
        }
    }

    // Infla la vista del adapter desde el diseño XML definido
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterUsuarioAdmin.MyHolder {
        val view = LayoutInflater.from(contexto).inflate(R.layout.recycler_usuarios_admin, parent, false)
        return MyHolder(view)
    }

    // Se determina la información de cada usuario de la lista con su respectivo lugar de la vista
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val usuario = lista[position]
        holder.nombreUsuario.text = usuario.nombreUsuario

        // Al pulsar se le mostrará un mensaje para expulsar al usuario
        holder.btnEcharGrupo.setOnClickListener {

            mostrarMensaje(contexto, "Expulsar del grupo", "¿Deseas expulsar a este usuario del grupo?", usuario.id, usuario.idGrupo, position)

        }

        // Coloca el checkbox dependiendo de como estaba en la base de datos
        holder.checkBox.isChecked = usuario.pagado


        // Al cambiar el checkbox, se comprueba si es diferente a cómo está en la base de datos y se cambia en ella el valor
        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            val ref = database.getReference("groups").child(usuario.idGrupo).child("users").child(usuario.id)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val pagadoActual = dataSnapshot.child("pagado").getValue(Boolean::class.java) ?: false
                    if (isChecked != pagadoActual) {
                        ref.child("pagado").setValue(isChecked)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Manejo de errores, si es necesario
                }
            })
        }



    }

    // Devuelve el números de usuarios sin contar al administrador
    override fun getItemCount(): Int {
        return lista.size
    }

    // Añade usuario a la lista
    fun addUsuarioAdmin(usuarioGrupo : UsuarioGrupo) {
        if (!usuarioGrupo.id.equals(auth.currentUser!!.uid)) {
            this.lista.add(usuarioGrupo)
            notifyItemInserted(lista.size-1)
        }
    }

    // Hace aparecer un cuadro de diálogo para asegurar de que se quiere expulsar al usuario
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

    // Expulsa al usuario del grupo
    fun echarUsuarioDelGrupo(usuarioId: String, idGrupo: String, position: Int) {
        val ref = database.getReference("groups").child(idGrupo).child("users")
        val ref2 = database.getReference("users").child(usuarioId).child("groups")
        val ref3 = database.getReference("groups").child(idGrupo)

        // Se elimina de la lista del adapter
        lista.removeAt(position)

        // Notificar al adaptador sobre el cambio realizado en la lista de datos
        notifyItemRemoved(position)

        // Se elimina al usuario del grupo
        ref.child(usuarioId).removeValue()

        // Se elimina el grupo del usuario
        ref2.orderByValue().equalTo(idGrupo).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // Se resta -1 al número de usuarios que tiene el grupo
        ref3.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var numUsuariosActual = snapshot.child("numUsuarios").getValue(Int::class.java) ?: 0
                numUsuariosActual-- // Restar 1 al número actual de usuarios
                ref3.child("numUsuarios").setValue(numUsuariosActual)
            }

            override fun onCancelled(error: DatabaseError) {}
        })



    }

}