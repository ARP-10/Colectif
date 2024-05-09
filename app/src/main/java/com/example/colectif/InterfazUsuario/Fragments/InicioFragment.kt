package com.example.colectif.InterfazUsuario.Fragments

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.PendingIntentCompat.Flags
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.colectif.Adapter.AdapterInicio
import com.example.colectif.InterfazUsuario.Activities.InicioActivity
import com.example.colectif.InterfazUsuario.Activities.LoginActivity
import com.example.colectif.R
import com.example.colectif.databinding.FragmentInicioBinding
import com.example.colectif.models.Grupo
import com.example.colectif.models.ImagenPerfil
import com.example.colectif.models.Solicitud
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.values
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.FirebaseStorageKtxRegistrar
import com.google.firebase.storage.ktx.storage
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class
InicioFragment: Fragment() {
    private lateinit var binding: FragmentInicioBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var sharedP: SharedPreferences
    private lateinit var adaptadorRecycler: AdapterInicio
    private var listaGrupos: ArrayList<Grupo>
    var uri: Uri? = null

    init {
        listaGrupos = ArrayList()
    }





    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inicialización de Firebase
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        database =
            FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")

        // Obtención del nombre de usuario
        var idUser = auth.currentUser!!.uid
        var ref = database.getReference("users")
        sharedP =
            requireContext().getSharedPreferences("com.example.colectif", Context.MODE_PRIVATE)

        comprobarImagen()

        // Configuración del RecyclerView
        adaptadorRecycler = context?.let { AdapterInicio(it, listaGrupos) }!!
        binding.recyclerView.adapter = adaptadorRecycler
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        recogerGrupos()

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.nombreUsuario.text =
                    snapshot.child(idUser).child("userName").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        // Para hacer que funcione el boton:
        adaptadorRecycler.setOnItemClickListener(object : AdapterInicio.OnItemClickListener {

            override fun onItemClick(position: Int, groupId: String) {
                Log.v("Boton", "Clic en el botón")
                var ref2 = database.getReference("groups")

                ref2.child(groupId).child("administrador")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val adminId = snapshot.value.toString()
                            val idUser = auth.currentUser!!.uid
                            Log.v("id admin", "ID del administrador: $adminId")

                            if (idUser == adminId) {
                                // El usuario es administrador del grupo
                                Log.v("Boton", "El usuario es administrador")

                                val bundle = Bundle()
                                bundle.putString("idGrupo", groupId)

                                findNavController().navigate(
                                    R.id.action_inicioFragment_to_verGrupoAdminFragment,
                                    bundle
                                )

                            } else {
                                // El usuario no es administrador del grupo
                                Log.v("Boton", "El usuario no es administrador")

                                val bundle = Bundle()
                                bundle.putString("idGrupo", groupId)

                                findNavController().navigate(
                                    R.id.action_inicioFragment_to_verGrupoFragment,
                                    bundle
                                )
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Manejar errores
                        }
                    })
            }
        })
        binding.imagenUsuario.setOnClickListener {
            seleccionarImagen()

        }

        binding.SpinnerMisGrupos.onItemSelectedListener =
            object : NavigationBarView.OnItemSelectedListener,
                OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val seleccionado = parent!!.adapter.getItem(position).toString()
                    adaptadorRecycler.filtrarLista(seleccionado)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onNavigationItemSelected(item: MenuItem): Boolean {
                    TODO("Not necessary")
                }

            }
    }

    // menu superior
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }


    override fun onDetach() {
        super.onDetach()
    }

    // Guarda la URI de la imagen seleccionada en SharedPreferences
    private fun saveImage(uri: String){
        val sharedPreferences = requireContext().getSharedPreferences("com.example.colectif",Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("imagenUri",uri)
        editor.commit()
    }

    private fun comprobarImagen(){
        val ref = database.reference.child("users").child(auth.currentUser!!.uid).child("imagen")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                context?.let { Glide.with(it).load(snapshot.value.toString().toUri()).error(R.drawable.foto_perfil).into(binding.imagenUsuario) }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== 101 && resultCode== RESULT_OK){
            uri = data!!.data
            binding.imagenUsuario.setImageURI(uri)
            saveImage(uri.toString())
            subirImagen()
        }
    }

    // Método para seleccionar una imagen de la galería
    private fun seleccionarImagen() {
        Dexter.withContext(context)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    intent.type = "image/*"
                    startActivityForResult(intent, 101)
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
                }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                    // Mostrar explicación al usuario y dar opción para volver a intentarlo
                    AlertDialog.Builder(context)
                        .setTitle("Permiso necesario")
                        .setMessage("Necesitamos permiso para acceder a tus imágenes. Por favor, concede el permiso en la configuración.")
                        .setPositiveButton("Ok") { dialog, which ->
                            token?.continuePermissionRequest()
                            dialog.dismiss()
                        }
                        .setNegativeButton("Cancelar") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                }
            })
            .check()
    }


    // Método para subir la imagen seleccionada a Firebase
    private fun subirImagen(){
        val reference = storage!!.reference.child("fotos_perfil").child(auth.currentUser!!.uid).child(System.currentTimeMillis().toString() + "")
        reference.putFile(uri!!).addOnSuccessListener {
            reference.downloadUrl.addOnSuccessListener { uri ->
                val actualizacion = HashMap<String,String>()
                actualizacion.put("imagen",uri.toString())
                database!!.reference.child("users").child(auth.currentUser!!.uid).updateChildren(
                    actualizacion as Map<String, Any>)
            }
        }
    }

    // Método para obtener y mostrar los grupos del usuario
    fun recogerGrupos() {

        val ref = database.getReference("users")
        val ref2 = database.getReference("groups")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for (i in 1 until snapshot.child(auth.currentUser!!.uid).child("numGrupos").value.toString().toInt() + 1) {
                        val idGrupo = snapshot.child(auth.currentUser!!.uid).child("groups").child(i.toString()).value.toString()

                        ref2.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(gruposnapshot: DataSnapshot) {
                                if (gruposnapshot.exists()) {
                                    val administradorId = gruposnapshot.child(idGrupo).child("administrador").value.toString()

                                    ref.child(administradorId).addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(adminSnapshot: DataSnapshot) {
                                            if (adminSnapshot.exists()) {
                                                val nombreAdmin = adminSnapshot.child("name").value.toString()

                                                adaptadorRecycler.addGrupo(
                                                    Grupo(
                                                        idGrupo,
                                                        nombreAdmin,
                                                        gruposnapshot.child(idGrupo).child("app").value.toString(),
                                                        gruposnapshot.child(idGrupo).child("contrasenia").value.toString(),
                                                        gruposnapshot.child(idGrupo).child("email").value.toString(),
                                                        gruposnapshot.child(idGrupo).child("imagen").value.toString().toInt(),
                                                        gruposnapshot.child(idGrupo).child("nombre").value.toString(),
                                                        gruposnapshot.child(idGrupo).child("plan").value.toString(),
                                                        gruposnapshot.child(idGrupo).child("precio").value.toString(),
                                                        gruposnapshot.child(idGrupo).child("fecha").value.toString()
                                                    )
                                                )
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            // Manejar errores de base de datos
                                        }
                                    })
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Manejar errores de base de datos
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores de base de datos
            }
        })


    
    }


}







