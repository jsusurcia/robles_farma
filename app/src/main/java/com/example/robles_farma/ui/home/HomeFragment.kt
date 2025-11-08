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
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter

class HomeFragment : Fragment() {

    private lateinit var especialidadAdapter: EspMasBuscadasAdapter
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EspMasBuscadasVM by viewModels()

    private lateinit var busquedaAdapter: ArrayAdapter<String>
    private val busquedaHandler = Handler(Looper.getMainLooper())
    private val BUSQUEDA_DELAY_MS: Long = 300

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupEspecialidadRecyclerView()
        setupBusquedaAutoComplete()
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

    private fun setupBusquedaAutoComplete() {
        // 1. Inicializa el adapter de búsqueda (al inicio vacío)
        busquedaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf<String>())
        binding.autoCompleteSearch.setAdapter(busquedaAdapter)

        // 2. Configura el listener de texto (con debouncing)
        binding.autoCompleteSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                // Cancela la búsqueda anterior
                busquedaHandler.removeCallbacksAndMessages(null)
                // Programa una nueva búsqueda después del retraso
                busquedaHandler.postDelayed({
                    val query = editable.toString()
                    viewModel.buscarEspecialidades(query)
                }, BUSQUEDA_DELAY_MS)
            }
        })

        // 3. Configura el listener de CLIC en una sugerencia
        binding.autoCompleteSearch.setOnItemClickListener { parent, view, position, id ->
            val selectedName = parent.getItemAtPosition(position) as String

            // Busca la especialidad completa en la lista de resultados
            val selectedEspecialidad = viewModel.searchResults.value?.find { it.nombre == selectedName }

            if (selectedEspecialidad != null) {
                // Aquí va tu lógica (navegar, etc.)
                Toast.makeText(
                    requireContext(),
                    "Seleccionado: ${selectedEspecialidad.nombre} (ID: ${selectedEspecialidad.idEspecialidad})",
                    Toast.LENGTH_SHORT
                ).show()

                // Opcional: Limpiar el buscador después de seleccionar
                binding.autoCompleteSearch.setText("", false)
            }
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
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpiando el handler para evitar memory leaks
        busquedaHandler.removeCallbacksAndMessages(null)
        _binding = null
    }
}
