package com.example.pylias.ui.modules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pylias.R
import com.example.pylias.data.ModuleRepository
import com.example.pylias.data.WordModule
import com.example.pylias.databinding.FragmentModulesBinding

class ModulesFragment : Fragment() {

    private var _binding: FragmentModulesBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: ModuleRepository
    private lateinit var adapter: ModuleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = ModuleRepository.get(requireContext())
        adapter = ModuleAdapter(
            onClick = { openEdit(it.id) },
            onDelete = { confirmDelete(it) }
        )
        binding.modulesList.layoutManager = LinearLayoutManager(requireContext())
        binding.modulesList.adapter = adapter
        binding.createButton.setOnClickListener {
            val created = repository.createModule(getString(R.string.default_module_name))
            openEdit(created.id)
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.submit(repository.getModules())
    }

    private fun openEdit(moduleId: String) {
        val action = ModulesFragmentDirections.actionModulesFragmentToModuleEditFragment(moduleId)
        findNavController().navigate(action)
    }

    private fun confirmDelete(module: WordModule) {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.delete_module_confirm, module.name))
            .setPositiveButton(R.string.delete) { _, _ ->
                repository.deleteModule(module.id)
                adapter.submit(repository.getModules())
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
