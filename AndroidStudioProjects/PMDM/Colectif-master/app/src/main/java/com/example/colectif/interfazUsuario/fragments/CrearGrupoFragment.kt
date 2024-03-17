package com.example.colectif.interfazUsuario.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.colectif.databinding.FragmentCrearGrupoBinding

class CrearGrupoFragment: Fragment() {

    private lateinit var binding: FragmentCrearGrupoBinding

    // Permite asociar elementos de la Activity (InicioActivity) con el fragment
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCrearGrupoBinding.inflate(inflater, container, false)
        return binding.root
    }



    // Permite desasociar elementos de la Activity (InicioActivity) con el fragment
    override fun onDetach() {
        super.onDetach()
    }
}