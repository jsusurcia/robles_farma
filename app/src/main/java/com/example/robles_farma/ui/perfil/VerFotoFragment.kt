package com.example.robles_farma.ui.perfil

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.robles_farma.R
import com.example.robles_farma.databinding.FragmentVerFotoBinding

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

        // 1. Recibe SOLO la URL (ya no necesitamos el token para verla)
        val fotoUrl = arguments?.getString("fotoUrl")

        Log.d("VerFotoDebug", "Intentando cargar URL: $fotoUrl")

        if (!fotoUrl.isNullOrEmpty()) {

            // 2. Carga SIMPLE con Glide
            // Quitamos LazyHeaders y GlideUrl porque Cloudinary es público
            Glide.with(requireContext())
                .load(fotoUrl)
                .placeholder(R.drawable.default_user_image)
                .error(R.drawable.default_user_image)
                // Quitamos skipMemoryCache y DiskCacheStrategy.NONE
                // Queremos que use caché para que cargue instantáneo
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("VerFotoDebug", "Error cargando imagen: ${e?.message}")
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("VerFotoDebug", "Imagen cargada OK")
                        return false
                    }
                })
                .into(binding.imageViewFotoCompleta) // Asegúrate que este ID exista en tu XML
        } else {
            Toast.makeText(requireContext(), "Error: URL de imagen vacía", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}