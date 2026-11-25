package com.example.pylias.ui.modules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pylias.R
import com.example.pylias.data.ModuleRepository
import com.example.pylias.data.WordModule
import com.example.pylias.databinding.FragmentModuleEditBinding

class ModuleEditFragment : Fragment() {

    private var _binding: FragmentModuleEditBinding? = null
    private val binding get() = _binding!!
    private val args: ModuleEditFragmentArgs by navArgs()
    private lateinit var repository: ModuleRepository
    private lateinit var module: WordModule
    private lateinit var adapter: WordAdapter
    private val words = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModuleEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = ModuleRepository.get(requireContext())
        module = repository.getModule(args.moduleId) ?: run {
            findNavController().navigateUp()
            return
        }
        words.clear()
        words.addAll(module.words)

        binding.moduleNameInput.setText(module.name)
        adapter = WordAdapter { word ->
            words.remove(word)
            adapter.submit(words)
        }
        binding.wordsList.layoutManager = LinearLayoutManager(requireContext())
        binding.wordsList.adapter = adapter
        adapter.submit(words)

        binding.addWordButton.setOnClickListener { addWord() }
        binding.wordInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addWord()
                true
            } else false
        }
        binding.saveButton.setOnClickListener { save() }
    }

    private fun addWord() {
        val value = binding.wordInput.text?.toString()?.trim().orEmpty()
        if (value.isEmpty()) return
        if (words.any { it.equals(value, ignoreCase = true) }) {
            Toast.makeText(requireContext(), "Уже есть в модуле", Toast.LENGTH_SHORT).show()
            return
        }
        words.add(0, value)
        adapter.submit(words)
        binding.wordInput.text?.clear()
    }

    private fun save() {
        module.name = binding.moduleNameInput.text?.toString()?.trim()
            .orEmpty()
            .ifBlank { getString(R.string.default_module_name) }
        module.words = words.toMutableList()
        repository.saveModule(module)
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
