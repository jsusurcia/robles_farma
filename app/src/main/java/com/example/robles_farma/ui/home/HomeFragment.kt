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
import android.util.Log
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.example.robles_farma.R
import com.example.robles_farma.sharedpreferences.LoginStorage

class HomeFragment : Fragment() {

    private lateinit var especialidadAdapter: EspMasBuscadasAdapter
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EspMasBuscadasVM by viewModels()

    private lateinit var busquedaAdapter: ArrayAdapter<String>
    private val busquedaHandler = Handler(Looper.getMainLooper())
    private val BUSQUEDA_DELAY_MS: Long = 800

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

        setupSaludo()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.listarEspecialidades()
        }

        // Le pedimos al ViewModel que cargue los datos
        viewModel.listarEspecialidades()
    }

    private fun setupEspecialidadRecyclerView() {
        // Inicializa el adapter y le decimos qué hacer al hacer clic
        especialidadAdapter = EspMasBuscadasAdapter { specialty ->

            // 1. Obtener datos
            val nombreEspecialidad = specialty.nombre
            val idEspecialidad = specialty.idEspecialidad
            val iconoUrl = specialty.iconoUrl

            // 2. Crear Bundle
            val bundle = Bundle()
            bundle.putString("nombre_especialidad", nombreEspecialidad)
            bundle.putInt("id_especialidad", idEspecialidad)

            // 3. Navegamos al fragmento de reservar cita, pasándole el bundle
            try {
                findNavController().navigate(
                    R.id.action_home_to_reservar_cita, // ¡IMPORTANTE! Ver explicación abajo
                    bundle
                )
            } catch (e: Exception) {
                // Manejar error si la navegación falla
                Log.e("HomeFragment", "Error al navegar", e)
                Toast.makeText(requireContext(), "No se pudo abrir la pantalla de citas.", Toast.LENGTH_SHORT).show()
            }
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
        // ---------------------------------------------------------
        // 1. DEFINICIÓN DEL ADAPTADOR (Tu código correcto)
        // ---------------------------------------------------------
        busquedaAdapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf<String>()
        ) {
            private val thisAdapter = this

            override fun getFilter(): android.widget.Filter {
                return object : android.widget.Filter() {
                    override fun performFiltering(constraint: CharSequence?): FilterResults {
                        val results = FilterResults()
                        val currentItems = mutableListOf<String>()
                        for (i in 0 until thisAdapter.count) {
                            thisAdapter.getItem(i)?.let { currentItems.add(it) }
                        }
                        results.values = currentItems
                        results.count = currentItems.size
                        return results
                    }

                    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                        if (results != null && results.count > 0) {
                            thisAdapter.notifyDataSetChanged()
                        } else {
                            thisAdapter.notifyDataSetInvalidated()
                        }
                    }
                }
            }
        }

        // 2. ASIGNAR EL ADAPTADOR A LA VISTA
        binding.autoCompleteSearch.setAdapter(busquedaAdapter)

        // ---------------------------------------------------------
        // 3. TEXT WATCHER (Para llamar a la API al escribir)
        // ---------------------------------------------------------
        binding.autoCompleteSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                // Cancela la búsqueda anterior si el usuario sigue escribiendo
                busquedaHandler.removeCallbacksAndMessages(null)
                // Programa una nueva búsqueda después del retraso (800ms)
                busquedaHandler.postDelayed({
                    val query = editable.toString()
                    viewModel.buscarEspecialidades(query)
                }, BUSQUEDA_DELAY_MS)
            }
        })

        // ---------------------------------------------------------
        // 4. CLICK LISTENER (¡CRUCIAL! Navegación con ID)
        // ---------------------------------------------------------
        binding.autoCompleteSearch.setOnItemClickListener { parent, view, position, id ->
            val selectedName = parent.getItemAtPosition(position) as String

            // Busca el objeto completo en la lista del ViewModel para sacar el ID
            val selectedEspecialidad = viewModel.searchResults.value?.find { it.nombre == selectedName }

            if (selectedEspecialidad != null) {
                // A. Obtenemos los datos
                val nombreEspecialidad = selectedEspecialidad.nombre
                val idEspecialidad = selectedEspecialidad.idEspecialidad // <--- EL ID ES IMPORTANTE

                // B. Creamos el Bundle
                val bundle = Bundle()
                bundle.putString("nombre_especialidad", nombreEspecialidad)
                bundle.putInt("id_especialidad", idEspecialidad) // <--- ENVIAMOS EL ID

                // C. Navegamos
                try {
                    findNavController().navigate(
                        R.id.action_home_to_reservar_cita, // Asegúrate que este ID sea el real de tu nav_graph
                        bundle
                    )
                } catch (e: Exception) {
                    Log.e("HomeFragment", "Error al navegar desde el buscador", e)
                    Toast.makeText(requireContext(), "Error al abrir la pantalla de citas.", Toast.LENGTH_SHORT).show()
                }

                // Limpiar el buscador después de seleccionar
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

        viewModel.searchResults.observe(viewLifecycleOwner, Observer { results ->

            Log.d("HomeFragment", "Observer de searchResults disparado. Resultados: $results")
            if (results == null) return@Observer

            // 1. Mapea la lista de objetos [BusquedaEspecialidadData]
            //    a una lista simple de [String]
            val names = results.map { it.nombre }
            Log.d("HomeFragment", "Nombres para el adapter: $names")
            // 2. Actualiza el adapter del AutoComplete
            busquedaAdapter.clear()
            busquedaAdapter.addAll(names)
            busquedaAdapter.notifyDataSetChanged()

            // 3. --- ¡LA LÍNEA QUE TE FALTA! ---
            // Si hay resultados, fuerza que el dropdown se muestre.
            // Si no hay (names.isEmpty()), el adapter ya se limpió.
            if (names.isNotEmpty()) {
                Log.d("HomeFragment", "¡Llamando a showDropDown()!")
                binding.autoCompleteSearch.showDropDown()
            } else {
                Log.d("HomeFragment", "No hay nombres, no se llama a showDropDown()")
            }
        })


        // Observador para los errores
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMsg ->
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
        })
    }

    private fun setupSaludo() {
        val loginStorage = LoginStorage(requireContext())
        val paciente = loginStorage.paciente

        if (paciente != null && paciente.nombres != null) {
            binding.textViewSaludo.text = "¡Hola, ${paciente.nombres}!"
        }
        else {
            binding.textViewSaludo.text = "¡Hola, desconocido!"
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Limpiando el handler para evitar memory leaks
        busquedaHandler.removeCallbacksAndMessages(null)
        _binding = null
    }
}
