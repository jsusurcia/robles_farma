package com.example.robles_farma.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.robles_farma.adapter.EspMasBuscadasAdapter
import com.example.robles_farma.databinding.FragmentHomeBinding
import com.example.robles_farma.viewmodels.EspMasBuscadasVM

class HomeFragment : Fragment() {

    private lateinit var especialidadAdapter: EspMasBuscadasAdapter
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EspMasBuscadasVM by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupEspecialidadRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Registramos los observadores
        observeViewModel()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.listarEspecialidades()
        }

        // Le pedimos al ViewModel que cargue los datos
        viewModel.listarEspecialidades()
    }

    private fun setupEspecialidadRecyclerView() {
        // Inicializa el adapter y le decimos qué hacer al hacer clic
        especialidadAdapter = EspMasBuscadasAdapter { specialty ->
            // Aquí va la lógica del clic
            // Por ejemplo, navegar a otra pantalla con los doctores de esa especialidad
            Toast.makeText(requireContext(), "Clic en: ${specialty.nombre}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerViewSpecialties.apply {
            // Le asignamos el adapter
            adapter = especialidadAdapter
            // Le decimos que es una cuadrícula de 2 columnas
            // (Aunque ya lo pusimos en el XML, es bueno asegurarlo aquí)
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    // Función para observar los LiveData del ViewModel
    private fun observeViewModel() {
        // Observador para la lista de especialidades
        viewModel.especialidades.observe(viewLifecycleOwner, Observer { listaEspecialidades ->
            // Cuando la lista cambie, se la pasamos al adapter
            especialidadAdapter.submitList(listaEspecialidades)
        })

        // Observador para el estado de carga (opcional pero recomendado)
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                // Si está cargando:
                // Solo mostramos el ProgressBar central si NO fue el usuario
                // quien activó la recarga (así evitamos dos spinners).
                if (!binding.swipeRefreshLayout.isRefreshing) {
                    binding.progressBar.visibility = View.VISIBLE
                }
            } else {
                // Si terminó de cargar:
                // Ocultamos AMBOS indicadores.
                binding.progressBar.visibility = View.GONE
                binding.swipeRefreshLayout.isRefreshing = false
            }
        })

        // Observador para los errores
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMsg ->
            // Mostramos un Toast si hay un error
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
        })
    }
}
