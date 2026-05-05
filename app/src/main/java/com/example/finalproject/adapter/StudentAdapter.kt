package com.example.finalproject.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.database.entity.StudentEntity
import com.example.finalproject.databinding.ItemStudentBinding

class StudentAdapter(
    private val onItemClick: (StudentEntity) -> Unit,
    private val onEditClick: (StudentEntity) -> Unit,
    private val onDeleteClick: (StudentEntity) -> Unit
) : ListAdapter<StudentEntity, StudentAdapter.StudentViewHolder>(DiffCallback()) {

    inner class StudentViewHolder(private val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(student: StudentEntity) {
            val initials = student.name.split(" ")
                .take(2).joinToString("") { it.first().uppercase() }
            binding.tvInitials.text = initials
            binding.tvName.text = student.name
            binding.tvNim.text = student.nim
            binding.tvProdi.text = student.prodi

            val colors = listOf(
                0xFF5C6BC0.toInt(), 0xFF26A69A.toInt(), 0xFFEF5350.toInt(),
                0xFFAB47BC.toInt(), 0xFF42A5F5.toInt(), 0xFFFF7043.toInt()
            )
            val colorIndex = (student.name.hashCode() and 0x7FFFFFFF) % colors.size
            binding.avatarCircle.setBackgroundColor(colors[colorIndex])

            binding.root.setOnClickListener { onItemClick(student) }

            binding.btnEdit.setOnClickListener { it.stopPropagation(); onEditClick(student) }
            binding.btnDelete.setOnClickListener { it.stopPropagation(); onDeleteClick(student) }
        }

        private fun android.view.View.stopPropagation() {
            this.setOnClickListener { }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getStudentAt(position: Int): StudentEntity = getItem(position)

    class DiffCallback : DiffUtil.ItemCallback<StudentEntity>() {
        override fun areItemsTheSame(oldItem: StudentEntity, newItem: StudentEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: StudentEntity, newItem: StudentEntity) = oldItem == newItem
    }
}