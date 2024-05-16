package com.example.colectif.InterfazUsuario.Fragments


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.colectif.Adapter.AdapterUsuarioAdmin
import com.example.colectif.R
import com.example.colectif.databinding.FragmentVerGrupoAdminBinding
import com.example.colectif.models.UsuarioGrupo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Fragmento que muestra los detalles de un grupo para el administrador.
 * Muestra información como el nombre del administrador, la aplicación del grupo, la contraseña,
 * el correo electrónico, el plan, el precio, los usuarios en el grupo y sus estados de pago.
 * Utiliza Firebase Realtime Database para recuperar y mostrar los datos del grupo y sus usuarios.
 */
class VerGrupoAdminFragment : Fragment() {
    private lateinit var binding: FragmentVerGrupoAdminBinding
    private var idGrupo:String? = null
    private lateinit var adaptadorUsuariosAdmin : AdapterUsuarioAdmin
    private lateinit var listaUsuarios: ArrayList<UsuarioGrupo>
    private lateinit var database: FirebaseDatabase
    private lateinit var textPassword: TextView
    private lateinit var txtShowPassword: TextView

    //Asocia elementos del activity con el fragment actual, en este caso recoge el id del grupo
    override fun onAttach(context: Context) {
        super.onAttach(context)
        idGrupo = arguments?.getString("idGrupo", "")
    }

    // Este método infla el diseño del fragmento y devuelve la vista correspondiente
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVerGrupoAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Aqui se inicializan los componentes y se desarrolla todas las funcionalidades del fragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        listaUsuarios = ArrayList()

        // Configuración del RecyclerView
        adaptadorUsuariosAdmin = context?.let { AdapterUsuarioAdmin(it, listaUsuarios) }!!
        binding.recyclerVerUsuariosAdmin.adapter = adaptadorUsuariosAdmin
        binding.recyclerVerUsuariosAdmin.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // Añadir los usuarios al recycler, exceptuando al administrador
        recogerUsuarios()

        var ref = database.getReference("groups")
        var ref2 = database.getReference("users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(childSnapshot: DataSnapshot) {

                // Obtener los valores de cada hijo
                val administradorId = childSnapshot.child(idGrupo!!).child("administrador").value.toString()
                val app = childSnapshot.child(idGrupo!!).child("app").value.toString()
                val contrasenia = childSnapshot.child(idGrupo!!).child("contrasenia").value.toString()
                val email = childSnapshot.child(idGrupo!!).child("email").value.toString()
                val nombre = childSnapshot.child(idGrupo!!).child("nombre").value.toString()
                val plan = childSnapshot.child(idGrupo!!).child("plan").value.toString()

                val precioString = childSnapshot.child(idGrupo!!).child("precio").value.toString()
                val precio = precioString.substringBefore(" ")
                    .replace(",", ".")
                    .toDoubleOrNull() ?: 0.0
                var numUsuariosActual = childSnapshot.child(idGrupo!!).child("numUsuarios").value.toString().toDouble()

                // Inicializar elementos para mostrar/ocultar contraseña
                textPassword = view.findViewById(R.id.txt_password)
                txtShowPassword = view.findViewById(R.id.txt_show_password)

                // Estado inicial de la visibilidad de la contraseña
                var passwordVisible = false

                // Llamar a la función para mostrar/ocultar la contraseña al inicio
                togglePasswordVisibility(passwordVisible, contrasenia)

                // Establecer el onClickListener para mostrar/ocultar la contraseña
                txtShowPassword.setOnClickListener {
                    passwordVisible = !passwordVisible
                    togglePasswordVisibility(passwordVisible, contrasenia)
                }

                // Obtener el nombre del admin de la base de datos de "users"
                ref2.child(administradorId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val nombreAdmin = snapshot.child("userName").value.toString()

                        // Actualizar la interfaz de usuario con los datos recuperados
                        binding.txtAdministrador.text = nombreAdmin
                        binding.txtCorreo.text = email

                        val drawableApp = when (app) {
                            "Netflix" -> R.drawable.netflix2
                            "Spotify" -> R.drawable.spotify2
                            "Amazon Prime" -> R.drawable.amazon2
                            "Disney +" -> R.drawable.disney2
                            else -> R.drawable.error
                        }
                        binding.imgGrupo.setImageResource(drawableApp)
                        binding.txtNombregrupo.text = nombre
                        binding.txtPlan.text = plan
                        numUsuariosActual = precio / numUsuariosActual
                        // Redondear el resultado de la división a dos decimales
                        val gastosRedondeados = String.format("%.2f", numUsuariosActual)

                        binding.txtPrecio.text = "$precio €"
                        binding.txtGastos.text = "$gastosRedondeados €"

                        // Comprobar si los datos llegan correctamente
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })




            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    // Recoge a los usuarios que pertenecen al grupo, sin contar con el admin, para poder colocar si han pagado o para expulsarlos
    fun recogerUsuarios() {
        val ref = database.getReference("groups").child(idGrupo!!).child("users")
        val ref2 = database.getReference("users")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    // Hace el bucle tantas veces como usuarios haya en el grupo
                    for (snapshot in snapshot.children) {
                        var idUsuario = snapshot.key
                        if (idUsuario != "1") {
                            var pagado = snapshot.child("pagado").getValue(Boolean::class.java) ?: false // Recoge el dato de si ha pagado o no el usuario
                            ref2.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(usuaruiosSnapshot: DataSnapshot) {
                                    if (usuaruiosSnapshot.exists()) {
                                        var nombreUsuario = usuaruiosSnapshot.child(idUsuario!!).child("userName").value.toString()
                                        if (nombreUsuario != "null") {

                                            // Añade el usuario y sus datos al adapter
                                            adaptadorUsuariosAdmin.addUsuarioAdmin(
                                                UsuarioGrupo(
                                                    idUsuario,
                                                    nombreUsuario,
                                                    idGrupo!!,
                                                    pagado
                                                )
                                            )
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Manejar errores de base de datos
                                }
                            })
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

    }

    // Función para mostrar u ocultar la contraseña
    private fun togglePasswordVisibility(showPassword: Boolean, password: String) {
        if (showPassword) {
            // Mostrar la contraseña
            textPassword.text = password
            txtShowPassword.text = "Ocultar"
        } else {
            // Ocultar la contraseña
            textPassword.text = "********"
            txtShowPassword.text = "Mostrar"
        }
    }
}