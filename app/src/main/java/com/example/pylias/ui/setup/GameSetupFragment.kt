package com.example.pylias.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.pylias.R
import com.example.pylias.data.GameConfig
import com.example.pylias.data.GameSession
import com.example.pylias.data.ModuleRepository
import com.example.pylias.databinding.FragmentGameSetupBinding

class GameSetupFragment : Fragment() {

    private var _binding: FragmentGameSetupBinding? = null
    private val binding get() = _binding!!
    private val args: GameSetupFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val module = ModuleRepository.get(requireContext()).getModule(args.moduleId)
        if (module == null) {
            findNavController().navigateUp()
            return
        }
        binding.moduleLabel.text = getString(R.string.module_label, module.name)

        // Seek: 10..120 sec (progress 0..110) default 60 => progress 50
        binding.turnTimeSeek.progress = 50
        binding.extraTimeSeek.progress = 10
        updateLabels()

        val listener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateLabels()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        }
        binding.turnTimeSeek.setOnSeekBarChangeListener(listener)
        binding.extraTimeSeek.setOnSeekBarChangeListener(listener)

        binding.playButton.setOnClickListener {
            val turn = binding.turnTimeSeek.progress + 10
            val extra = binding.extraTimeSeek.progress
            GameSession.config = GameConfig(module.id, turn, extra)
            GameSession.words = module.words.shuffled()
            GameSession.moduleName = module.name
            findNavController().navigate(R.id.action_gameSetupFragment_to_gameFragment)
        }
    }

    private fun updateLabels() {
        val turn = binding.turnTimeSeek.progress + 10
        val extra = binding.extraTimeSeek.progress
        binding.turnTimeValue.text = getString(R.string.seconds_format, turn)
        binding.extraTimeValue.text = getString(R.string.seconds_format, extra)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
