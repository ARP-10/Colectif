package com.example.colectif.InterfazUsuario.Fragments

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class InicioFragment: Fragment() {
    private lateinit var binding: FragmentInicioBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var sharedP: SharedPreferences
    private lateinit var adaptadorRecycler: AdapterInicio
    private lateinit var listaGrupos: ArrayList<Grupo>
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
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        var idUser = auth.currentUser!!.uid
        var ref = database.getReference("users")
        sharedP = requireContext().getSharedPreferences("com.example.colectif", Context.MODE_PRIVATE)
        comprobarImagen()
        adaptadorRecycler = context?.let { AdapterInicio(it,listaGrupos) }!!

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
                Log.v("prueba1", groupId)
                val bundle = Bundle()
                bundle.putString("idGrupo", groupId)
                val fragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                findNavController().navigate(R.id.action_inicioFragment_to_verGrupoFragment, bundle)
                //fragmentTransaction.replace(R.id.nav_host_fragment_content_main, VerGrupoFragment())
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
                Log.v("prueba1", groupId)
            }


        })


        binding.btnCerrarSesion.setOnClickListener {
            logOut()
        }
        binding.imagenUsuario.setOnClickListener{
            seleccionarImagen()


            //subirImagen()
        }
    }

    override fun onDetach() {
        super.onDetach()
    }

    private fun logOut(){
        auth.signOut()
        val sharedP: SharedPreferences
        sharedP = requireContext().getSharedPreferences("com.example.colectif",Context.MODE_PRIVATE)
        val editor = sharedP.edit()
        editor.putBoolean("estado",false)
        editor.apply()
        startActivity(Intent(context, LoginActivity::class.java))
        activity?.finish()
    }

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


    private fun seleccionarImagen(){
        Dexter.withContext(context).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object: PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_PICK
                    startActivityForResult(intent,101)
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(context,"Permiso denegado", Toast.LENGTH_SHORT).show()
                }

                override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {

                }
                }).check()
    }

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

    fun recogerGrupos(){
        var ref  = database.getReference("users")
        var ref2 = database.getReference("groups")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for (i in 1 until snapshot.child(auth.currentUser!!.uid)
                        .child("numGrupos").value.toString().toInt() + 1) {
                        var idGrupo = snapshot.child(auth.currentUser!!.uid).child("groups")
                            .child(i.toString()).value.toString()
                        ref2.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    adaptadorRecycler.addGrupo(
                                        Grupo(
                                            idGrupo,
                                            snapshot.child(idGrupo).child("aministrador").value.toString(),
                                            snapshot.child(idGrupo).child("app").value.toString(),
                                            snapshot.child(idGrupo).child("contrasenia").value.toString(),
                                            snapshot.child(idGrupo).child("email").value.toString(),
                                            snapshot.child(idGrupo).child("imagen").value.toString().toInt(),
                                            snapshot.child(idGrupo).child("nombre").value.toString(),
                                            snapshot.child(idGrupo).child("plan").value.toString(),
                                            snapshot.child(idGrupo).child("precio").value.toString()

                                        )
                                    )
                                }

                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }


}







