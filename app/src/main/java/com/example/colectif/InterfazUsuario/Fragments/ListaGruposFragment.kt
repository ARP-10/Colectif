package com.example.colectif.InterfazUsuario.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

class ListaGruposFragment: Fragment(){

    private lateinit var binding: FragmentListaGruposBinding
    private lateinit var listaGrupos: ArrayList<Grupo>
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var adapterListCatalogo: AdapterListCatalogo
    private lateinit var listaNetflix: ArrayList<Grupo>
    private lateinit var listaAmazon: ArrayList<Grupo>
    private lateinit var listaDisney: ArrayList<Grupo>
    private lateinit var listaSpotify: ArrayList<Grupo>
    private lateinit var catalogo: ArrayList<CatalogoGrupos>

    //permite asociar elementos del activity con el fragment
    override fun onAttach(context: Context) {
        super.onAttach(context)
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





        catalogo.add(CatalogoGrupos("Amazon Prime",recuperarGrupos()[1]))
        catalogo.add(CatalogoGrupos("Disney +",recuperarGrupos()[2]))
        catalogo.add(CatalogoGrupos("Spotify",recuperarGrupos()[3]))

        // Obtener referencia al RecyclerView desde el archivo de diseño
        val recyclerView: RecyclerView = binding.FragmentRecyclerView

        // Configurar un LinearLayoutManager para organizar los elementos verticalmente
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recuperarGrupos()
        // Crear un adaptador para el RecyclerView que esté vinculado a la lista de catálogos y grupos
        adapterListCatalogo = AdapterListCatalogo(catalogo)

        // Establecer el adaptador en el RecyclerView
        recyclerView.adapter = adapterListCatalogo

    }

    ////permite desasociar elementos del activity con el fragment
    override fun onDetach() {
        super.onDetach()
    }

    private fun recuperarGrupos(): ArrayList<ArrayList<Grupo>>{
        var ref = database.getReference("groups")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                    for(ds in snapshot.children) {
                        if (ds.child("app").value.toString().equals("Netflix")) {
                            Log.v("ola0",ds.child("nombre").value.toString())
                            var grupo = Grupo(
                                ds.child("aministrador").value.toString(),
                                ds.child("nombre").value.toString(),
                                ds.child("app").value.toString(),
                                ds.child("plan").value.toString(),
                                ds.child("precio").value.toString(),
                                ds.child("email").value.toString(),
                                ds.child("contrasenia").value.toString(),
                                ds.child("imagen").value.toString()
                                    .toInt()
                            )
                            listaNetflix.add(grupo)
                            adapterListCatalogo.addCatalogo(CatalogoGrupos("Netflix",listaNetflix))
                            Log.v("ola4", catalogo.toString())

                        }
                        if (ds.child("app").value.toString().equals("Amazon Prime")) {
                            var grupo = Grupo(
                                ds.child("aministrador").value.toString(),
                                ds.child("nombre").value.toString(),
                                ds.child("app").value.toString(),
                                ds.child("plan").value.toString(),
                                ds.child("precio").value.toString(),
                                ds.child("email").value.toString(),
                                ds.child("contrasenia").value.toString(),
                                ds.child("imagen").value.toString()
                                    .toInt()
                            )
                            listaAmazon.add(grupo)
                            adapterListCatalogo.addCatalogo(CatalogoGrupos("Amazon Prime",listaAmazon))
                        }
                        if (ds.child("app").value.toString().equals("Spotify")) {
                            var grupo = Grupo(
                                ds.child("aministrador").value.toString(),
                                ds.child("nombre").value.toString(),
                                ds.child("app").value.toString(),
                                ds.child("plan").value.toString(),
                                ds.child("precio").value.toString(),
                                ds.child("email").value.toString(),
                                ds.child("contrasenia").value.toString(),
                                ds.child("imagen").value.toString()
                                    .toInt()
                            )
                            listaSpotify.add(grupo)
                            adapterListCatalogo.addCatalogo(CatalogoGrupos("Spotify",listaSpotify))
                        }
                        if (ds.child("app").value.toString().equals("Disney +")) {
                            var grupo = Grupo(
                                ds.child("aministrador").value.toString(),
                                ds.child("nombre").value.toString(),
                                ds.child("app").value.toString(),
                                ds.child("plan").value.toString(),
                                ds.child("precio").value.toString(),
                                ds.child("email").value.toString(),
                                ds.child("contrasenia").value.toString(),
                                ds.child("imagen").value.toString()
                                    .toInt()
                            )
                            listaDisney.add(grupo)
                            adapterListCatalogo.addCatalogo(CatalogoGrupos("Disney +",listaDisney))
                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        var listas: ArrayList<ArrayList<Grupo>> = ArrayList()
        listas.add(listaNetflix)
        listas.add(listaAmazon)
        listas.add(listaDisney)
        listas.add(listaSpotify)
        return listas

    }

}