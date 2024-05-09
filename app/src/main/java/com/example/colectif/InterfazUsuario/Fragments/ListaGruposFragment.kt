package com.example.colectif.InterfazUsuario.Fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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
import com.google.android.material.navigation.NavigationBarView
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
   // private var currentUserID = auth.currentUser?.uid

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
        listaAmazon = ArrayList()
        listaDisney = ArrayList()
        listaSpotify = ArrayList()
        catalogo = ArrayList()
        var idUser = auth.currentUser!!.uid




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


        binding.editBuscar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                adapterListCatalogo.filtrarLista(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.imgReset.setOnClickListener{
            findNavController().navigate(
                R.id.listaGruposFragment
            )
        }
    }

    // Se esta usando esto ??
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

                    for (snapshot in snapshot.children) {
                        // Comparar el max ususarios permitidos con el num actual

                        val numMax = convertToInt(snapshot.child("numMax").value.toString())
                        val numUsuarios = convertToInt(snapshot.child("numUsuarios").value.toString())
                        val administrador = snapshot.child("administrador").value.toString()
                        val usuariosSnapshot = snapshot.child("usuarios")

                        // Verificar si el usuario actual es administrador o ya es miembro del grupo
                        val isCurrentUserAdmin = administrador == auth.currentUser?.uid
                        val isCurrentUserMember = usuariosSnapshot.hasChild(auth.currentUser?.uid ?: "")

                        // Verificar si el grupo no está completo y el usuario actual no es admin ni miembro
                        if (numUsuarios < numMax && !isCurrentUserAdmin && !isCurrentUserMember) {
                        /*
                        val numMax = convertToInt(snapshot.child("numMax").value.toString())
                        val numUsuarios = convertToInt(snapshot.child("numUsuarios").value.toString())

                        //if (!snapshot.child("administrador").value.toString().equals(auth.currentUser!!.uid)) {

                        if (numUsuarios < numMax && !snapshot.child("administrador").value.toString().equals(auth.currentUser!!.uid)) {*/
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
                                    snapshot.child("fecha").value.toString()
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
                                    snapshot.child("fecha").value.toString()
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
                                    snapshot.child("fecha").value.toString()
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
                                    snapshot.child("fecha").value.toString()
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
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun convertToInt(value: String): Int {
        return try {
            value.toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }



}