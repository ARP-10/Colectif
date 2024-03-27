package com.example.colectif.InterfazUsuario.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.colectif.Adapter.AdapterListCatalogo
import com.example.colectif.Objetos.ObjetoGrupos
import com.example.colectif.R
import com.example.colectif.databinding.FragmentListaGruposBinding

class ListaGruposFragment: Fragment(){

    private lateinit var binding: FragmentListaGruposBinding

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
        super.onViewCreated(view, savedInstanceState)

        // Obtener referencia al RecyclerView desde el archivo de diseño
        val recyclerView: RecyclerView = binding.FragmentRecyclerView

        // Configurar un LinearLayoutManager para organizar los elementos verticalmente
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        // Crear un adaptador para el RecyclerView que esté vinculado a la lista de catálogos y grupos
        val adapter = AdapterListCatalogo(ObjetoGrupos.catalogoGrupos)

        // Establecer el adaptador en el RecyclerView
        recyclerView.adapter = adapter

    }


    ////permite desasociar elementos del activity con el fragment
    override fun onDetach() {
        super.onDetach()
    }
}