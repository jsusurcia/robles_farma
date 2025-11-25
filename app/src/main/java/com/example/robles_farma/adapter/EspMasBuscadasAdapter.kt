package com.example.robles_farma.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.robles_farma.databinding.ItemEspecialidadCardBinding
import com.example.robles_farma.model.EspecialidadData
import coil.load
import com.example.robles_farma.R

class EspMasBuscadasAdapter(
    private val onItemClicked: (EspecialidadData) -> Unit
) : ListAdapter<EspecialidadData, EspMasBuscadasAdapter.EspecialidadViewHolder>(
    EspecialidadDiffCallback
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EspecialidadViewHolder {
        val binding = ItemEspecialidadCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EspecialidadViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EspecialidadViewHolder, position: Int) {
        val especialidad = getItem(position)
        holder.bind(especialidad, onItemClicked)
    }

    class EspecialidadViewHolder(
        private val binding: ItemEspecialidadCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(especialidad: EspecialidadData, onItemClicked: (EspecialidadData) -> Unit) {
            binding.textViewSpecialtyName.text = especialidad.nombre
            //binding.imageViewSpecialtyIcon.setImageResource(especialidad.totalCitas)

            // 2. IMPLEMENTACIÓN DE COIL AQUÍ
            // Gracias a tu configuración en MyApplication.java, esto leerá SVG automáticamente.
            binding.imageViewSpecialtyIcon.load(especialidad.iconoUrl) {
                // Habilita una animación suave de aparición
                crossfade(true)
                // Muestra este ícono mientras la imagen descarga
                placeholder(R.drawable.ic_hora)
                // Muestra este ícono si la URL falla o es nula
                error(R.drawable.hospital_24_px)
            }

            binding.root.setOnClickListener {
                onItemClicked(especialidad)
            }
        }
    }

    companion object {
        private val EspecialidadDiffCallback = object : DiffUtil.ItemCallback<EspecialidadData>() {
            override fun areItemsTheSame(
                oldItem: EspecialidadData,
                newItem: EspecialidadData
            ): Boolean {
                return oldItem.idEspecialidad == newItem.idEspecialidad
            }

            override fun areContentsTheSame(
                oldItem: EspecialidadData,
                newItem: EspecialidadData
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}