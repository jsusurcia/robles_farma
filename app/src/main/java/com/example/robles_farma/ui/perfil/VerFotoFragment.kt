package com.example.robles_farma.ui.perfil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.example.robles_farma.R
import com.example.robles_farma.databinding.FragmentVerFotoBinding // Importa el ViewBinding

class VerFotoFragment : Fragment() {

    private var _binding: FragmentVerFotoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerFotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Recibe los datos enviados desde PerfilFragment
        val fotoUrl = arguments?.getString("fotoUrl")
        val token = arguments?.getString("token")

        if (fotoUrl != null && token != null) {

            // 2. Prepara las cabeceras (headers) para Glide, igual que en PerfilFragment
            val headers = LazyHeaders.Builder()
                .addHeader("Authorization", "Bearer $token")
                .build()

            val glideUrl = GlideUrl(fotoUrl, headers)

            // 3. Carga la imagen en el ImageView usando Glide
            Glide.with(requireContext())
                .load(glideUrl)
                .placeholder(R.drawable.default_user_image) // Un placeholder mientras carga
                .error(R.drawable.default_user_image) // Una imagen de error si falla
                .skipMemoryCache(true) // Opcional: igual que en tu PerfilFragment
                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
                .into(binding.imageViewFotoCompleta)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}