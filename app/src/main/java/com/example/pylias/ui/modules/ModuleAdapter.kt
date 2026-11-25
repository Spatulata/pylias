package com.example.pylias.ui.modules

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pylias.R
import com.example.pylias.data.WordModule
import com.example.pylias.databinding.ItemModuleBinding

class ModuleAdapter(
    private val onClick: (WordModule) -> Unit,
    private val onDelete: (WordModule) -> Unit
) : RecyclerView.Adapter<ModuleAdapter.VH>() {

    private val items = mutableListOf<WordModule>()

    fun submit(list: List<WordModule>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemModuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size

    inner class VH(private val binding: ItemModuleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(module: WordModule) {
            binding.moduleName.text = module.name
            binding.moduleWordsCount.text =
                binding.root.context.getString(R.string.words_count, module.words.size)
            binding.root.setOnClickListener { onClick(module) }
            binding.deleteButton.setOnClickListener { onDelete(module) }
        }
    }
}
