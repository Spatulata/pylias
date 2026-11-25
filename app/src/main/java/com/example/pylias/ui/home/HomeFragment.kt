package com.example.pylias.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pylias.R
import com.example.pylias.data.ModuleRepository
import com.example.pylias.data.WordModule
import com.example.pylias.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var modules: List<WordModule> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startButton.setOnClickListener { startGame() }
    }

    override fun onResume() {
        super.onResume()
        refreshModules()
    }

    private fun refreshModules() {
        modules = ModuleRepository.get(requireContext()).getModules()
        val names = modules.map { "${it.name} (${it.words.size})" }
        binding.moduleSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            names.ifEmpty { listOf(getString(R.string.no_modules)) }
        )
        binding.emptyHint.visibility = if (modules.isEmpty()) View.VISIBLE else View.GONE
        binding.startButton.isEnabled = modules.isNotEmpty()
        binding.previewWord.text = modules.firstOrNull()?.words?.firstOrNull() ?: "def"
    }

    private fun startGame() {
        if (modules.isEmpty()) {
            Toast.makeText(requireContext(), R.string.no_modules, Toast.LENGTH_SHORT).show()
            return
        }
        val selected = modules.getOrNull(binding.moduleSpinner.selectedItemPosition) ?: return
        if (selected.words.isEmpty()) {
            Toast.makeText(requireContext(), R.string.module_empty_words, Toast.LENGTH_SHORT).show()
            return
        }
        val action = HomeFragmentDirections.actionHomeFragmentToGameSetupFragment(selected.id)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
