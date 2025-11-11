package com.example.robles_farma.ui.perfil

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.robles_farma.databinding.FragmentConfiguracionBinding
import com.example.robles_farma.R

class ConfiguracionFragment : Fragment() {

    // Constantes para SharedPreferences
    private val PREFS_NAME = "theme_prefs"
    private val KEY_THEME = "selected_theme"

    // ViewBinding
    private var _binding: FragmentConfiguracionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla el layout usando ViewBinding
        _binding = FragmentConfiguracionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Cargar la preferencia guardada y marcar el RadioButton correcto
        val savedMode = loadThemePreference()
        updateRadioButtons(savedMode)

        // 2. Escuchar cambios en el RadioGroup
        binding.themeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                R.id.radioLight -> AppCompatDelegate.MODE_NIGHT_NO
                R.id.radioDark -> AppCompatDelegate.MODE_NIGHT_YES
                R.id.radioSystem -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }

            // 3. Aplicar el tema y guardarlo
            AppCompatDelegate.setDefaultNightMode(mode)
            saveThemePreference(mode)
        }
    }

    private fun saveThemePreference(mode: Int) {
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_THEME, mode).apply()
    }

    private fun loadThemePreference(): Int {
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Devuelve el tema guardado, o "Sistema" por defecto si no hay nada guardado
        return prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    private fun updateRadioButtons(mode: Int) {
        // Marcamos el botón que corresponde al modo actual
        when (mode) {
            AppCompatDelegate.MODE_NIGHT_NO -> binding.radioLight.isChecked = true
            AppCompatDelegate.MODE_NIGHT_YES -> binding.radioDark.isChecked = true
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> binding.radioSystem.isChecked = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpia la referencia al binding para evitar memory leaks
        _binding = null
    }
}