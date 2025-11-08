package com.example.robles_farma.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.robles_farma.databinding.ItemEspecialidadCardBinding
import com.example.robles_farma.model.EspecialidadData


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

            // provisional
            binding.imageViewSpecialtyIcon.setImageResource(com.example.robles_farma.R.drawable.ic_perfil)

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