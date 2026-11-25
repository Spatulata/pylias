package com.example.pylias.ui.results

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pylias.R
import com.example.pylias.data.GameSession
import com.example.pylias.databinding.FragmentResultsBinding

class ResultsFragment : Fragment() {

    private var _binding: FragmentResultsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val result = GameSession.result ?: run {
            findNavController().navigate(R.id.action_resultsFragment_to_homeFragment)
            return
        }

        binding.percentText.text = getString(R.string.percent_format, result.percent)
        binding.statsText.text = getString(R.string.answered_stats, result.correct, result.skipped, result.total)
        binding.gradeText.text = result.grade.toString()

        binding.playAgainButton.setOnClickListener {
            val config = GameSession.config
            if (config != null) {
                val action = ResultsFragmentDirections.actionResultsFragmentToGameSetupFragment(config.moduleId)
                findNavController().navigate(action)
            } else {
                findNavController().navigate(R.id.action_resultsFragment_to_homeFragment)
            }
        }

        binding.homeButton.setOnClickListener {
            findNavController().navigate(R.id.action_resultsFragment_to_homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
