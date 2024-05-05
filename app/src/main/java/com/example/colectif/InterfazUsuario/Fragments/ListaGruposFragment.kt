package com.example.colectif.InterfazUsuario.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.colectif.Adapter.AdapterListCatalogo
import com.example.colectif.Adapter.AdapterListGrupos
import com.example.colectif.Objetos.ObjetoGrupos
import com.example.colectif.R
import com.example.colectif.databinding.FragmentListaGruposBinding
import com.example.colectif.models.CatalogoGrupos
import com.example.colectif.models.Grupo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListaGruposFragment: Fragment(), AdapterListGrupos.OnInfoButtonClickListener {

    private lateinit var binding: FragmentListaGruposBinding
    private lateinit var listaGrupos: ArrayList<Grupo>
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var adapterListCatalogo: AdapterListCatalogo
    private lateinit var  adapterListGrupos: AdapterListGrupos
    private lateinit var listaNetflix: ArrayList<Grupo>
    private lateinit var listaAmazon: ArrayList<Grupo>
    private lateinit var listaDisney: ArrayList<Grupo>
    private lateinit var listaSpotify: ArrayList<Grupo>
    private lateinit var catalogo: ArrayList<CatalogoGrupos>
    private var idGrupo:String? = null

    //permite asociar elementos del activity con el fragment
    // Recoger id grupo
    override fun onAttach(context: Context) {
        super.onAttach(context)
        idGrupo = arguments?.getString("idGrupo", "")
    }

    // Este metodo es llamado para que el fragmento cree su jerarquia de vistas asociada.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListaGruposBinding.inflate(inflater, container, false)
        return binding.root
    }

    //Llamado después de que la vista asociada con el fragmento se ha creado
    //configuración adicional que el fragment necesite
    //despues de que su vista haya sido creada
    //y antes de que se muestre al usuario
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        listaGrupos = ArrayList()
        listaNetflix = ArrayList()
        Log.v("ola2", listaNetflix.toString())
        listaAmazon = ArrayList()
        listaDisney = ArrayList()
        listaSpotify = ArrayList()
        catalogo = ArrayList()




        super.onViewCreated(view, savedInstanceState)






        // Obtener referencia al RecyclerView desde el archivo de diseño
        val recyclerView: RecyclerView = binding.FragmentRecyclerView

        // Configurar un LinearLayoutManager para organizar los elementos verticalmente
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recuperarGrupos()
        // Crear un adaptador para el RecyclerView que esté vinculado a la lista de catálogos y grupos
        adapterListCatalogo = context?.let { AdapterListCatalogo(it,catalogo) }!!

        // Establecer el adaptador en el RecyclerView
        recyclerView.adapter = adapterListCatalogo

        // Inicializar adapterListGrupos
        adapterListGrupos = AdapterListGrupos(requireContext(), ArrayList())

        adapterListGrupos.infoButtonClickListener = this

    }

    override fun onInfoButtonClick(grupo: Grupo) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Información del grupo")
        alertDialogBuilder.setMessage("Nombre: ${grupo.nombre}\n" +
                "Administrador: ${grupo.administrador}\n" +
                "Plan: ${grupo.plan}\n" +
                "Precio: ${grupo.precio}")
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

    }

    //permite desasociar elementos del activity con el fragment
    override fun onDetach() {
        super.onDetach()
    }

    private fun recuperarGrupos() {
        var ref = database.getReference("groups")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.v("ListaGrupos", "Snapshot exists")

                    for (snapshot in snapshot.children) {
                        // Comparar el max ususarios permitidos con el num actual
                        //val numMax = snapshot.child("numMax").getValue(Int::class.java) ?: 0
                        //val numUsuarios = snapshot.child("numUsuarios").getValue(Int::class.java) ?: 0
                        //Log.v("ListaGrupos", "numMax: $numMax, numUsuarios: $numUsuarios")

                        if (!snapshot.child("administrador").value.toString().equals(auth.currentUser!!.uid)) {
                        //if (numUsuarios < numMax && !snapshot.child("administrador").value.toString().equals(auth.currentUser!!.uid)) {
                            if (snapshot.child("app").value.toString().equals("Netflix")) {
                                var grupo = Grupo(
                                    snapshot.child("id").value.toString(),
                                    snapshot.child("administrador").value.toString(),
                                    snapshot.child("app").value.toString(),
                                    snapshot.child("contrasenia").value.toString(),
                                    snapshot.child("email").value.toString(),
                                    snapshot.child("imagen").value.toString().toInt(),
                                    snapshot.child("nombre").value.toString(),
                                    snapshot.child("plan").value.toString(),
                                    snapshot.child("precio").value.toString(),
                                )
                                listaNetflix.add(grupo)
                            }
                            if (snapshot.child("app").value.toString().equals("Amazon Prime")) {
                                var grupo = Grupo(
                                    snapshot.child("id").value.toString(),
                                    snapshot.child("administrador").value.toString(),
                                    snapshot.child("app").value.toString(),
                                    snapshot.child("contrasenia").value.toString(),
                                    snapshot.child("email").value.toString(),
                                    snapshot.child("imagen").value.toString().toInt(),
                                    snapshot.child("nombre").value.toString(),
                                    snapshot.child("plan").value.toString(),
                                    snapshot.child("precio").value.toString(),
                                )
                                listaAmazon.add(grupo)

                            }
                            if (snapshot.child("app").value.toString().equals("Spotify")) {
                                var grupo = Grupo(
                                    snapshot.child("id").value.toString(),
                                    snapshot.child("administrador").value.toString(),
                                    snapshot.child("app").value.toString(),
                                    snapshot.child("contrasenia").value.toString(),
                                    snapshot.child("email").value.toString(),
                                    snapshot.child("imagen").value.toString().toInt(),
                                    snapshot.child("nombre").value.toString(),
                                    snapshot.child("plan").value.toString(),
                                    snapshot.child("precio").value.toString(),
                                )
                                listaSpotify.add(grupo)

                            }
                            if (snapshot.child("app").value.toString().equals("Disney +")) {
                                var grupo = Grupo(
                                    snapshot.child("id").value.toString(),
                                    snapshot.child("administrador").value.toString(),
                                    snapshot.child("app").value.toString(),
                                    snapshot.child("contrasenia").value.toString(),
                                    snapshot.child("email").value.toString(),
                                    snapshot.child("imagen").value.toString().toInt(),
                                    snapshot.child("nombre").value.toString(),
                                    snapshot.child("plan").value.toString(),
                                    snapshot.child("precio").value.toString(),
                                )
                                listaDisney.add(grupo)

                            }
                        }


                    }
                    adapterListCatalogo.addCatalogo(CatalogoGrupos("Netflix", listaNetflix))
                    adapterListCatalogo.addCatalogo(CatalogoGrupos("Amazon Prime", listaAmazon))
                    adapterListCatalogo.addCatalogo(CatalogoGrupos("Spotify", listaSpotify))
                    adapterListCatalogo.addCatalogo(CatalogoGrupos("Disney +", listaDisney))


                } else {
                Log.v("ListaGrupos", "Snapshot doesn't exist")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }



}