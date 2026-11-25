package com.example.pylias.ui.game

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pylias.R
import com.example.pylias.data.GameResult
import com.example.pylias.data.GameSession
import com.example.pylias.databinding.FragmentGameBinding
import java.util.Locale

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private var words = mutableListOf<String>()
    private var currentIndex = 0
    private var correctCount = 0
    private var skippedCount = 0

    private var timer: CountDownTimer? = null
    private var isOvertime = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val config = GameSession.config ?: run {
            findNavController().navigateUp()
            return
        }
        words = GameSession.words.toMutableList()
        if (words.isEmpty()) {
            words.add("Python")
        }

        showWord()
        updateScore()

        val turnMillis = config.turnTimeSeconds * 1000L
        timer = object : CountDownTimer(turnMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val secs = (millisUntilFinished / 1000).toInt()
                binding.timerText.text = String.format(Locale.getDefault(), "%02d:%02d", secs / 60, secs % 60)
            }

            override fun onFinish() {
                startOvertime(config.extraTimeSeconds)
            }
        }.start()

        binding.speakButton.setOnClickListener {
            correctCount++
            nextWord()
        }
        binding.passButton.setOnClickListener {
            skippedCount++
            nextWord()
        }

        var initialY = 0f
        var initialTouchY = 0f

        binding.wordCard.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialTouchY = event.rawY
                    initialY = v.translationY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaY = event.rawY - initialTouchY
                    v.translationY = initialY + deltaY
                    val rotation = (deltaY / v.width) * 10f
                    v.rotation = rotation.coerceIn(-10f, 10f)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val deltaY = v.translationY
                    val threshold = v.height * 0.2f

                    if (deltaY < -threshold) {
                        // Swiped UP -> Correct
                        v.animate()
                            .translationY(-v.height.toFloat() * 1.5f)
                            .alpha(0f)
                            .setDuration(200)
                            .withEndAction {
                                v.translationY = 0f
                                v.rotation = 0f
                                v.alpha = 1f
                                correctCount++
                                nextWord()
                            }
                            .start()
                    } else if (deltaY > threshold) {
                        // Swiped DOWN -> Pass
                        v.animate()
                            .translationY(v.height.toFloat() * 1.5f)
                            .alpha(0f)
                            .setDuration(200)
                            .withEndAction {
                                v.translationY = 0f
                                v.rotation = 0f
                                v.alpha = 1f
                                skippedCount++
                                nextWord()
                            }
                            .start()
                    } else {
                        // Snap back
                        v.animate()
                            .translationY(0f)
                            .rotation(0f)
                            .setDuration(150)
                            .start()
                    }
                    v.performClick()
                    true
                }
                else -> false
            }
        }
        binding.wordCard.setOnClickListener {
            // no-op
        }
    }

    private fun startOvertime(extraSeconds: Int) {
        if (extraSeconds <= 0) {
            finishGame()
            return
        }
        isOvertime = true
        binding.overtimeLabel.visibility = View.VISIBLE
        val overtimeMillis = extraSeconds * 1000L
        timer = object : CountDownTimer(overtimeMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val secs = (millisUntilFinished / 1000).toInt()
                binding.timerText.text = String.format(Locale.getDefault(), "+%02d:%02d", secs / 60, secs % 60)
            }

            override fun onFinish() {
                finishGame()
            }
        }.start()
    }

    private fun showWord() {
        if (currentIndex < words.size) {
            binding.wordText.text = words[currentIndex]
            binding.wordCard.translationY = 0f
            binding.wordCard.rotation = 0f
            binding.wordCard.alpha = 1f
        } else {
            finishGame()
        }
    }

    private fun nextWord() {
        currentIndex++
        updateScore()
        showWord()
    }

    private fun updateScore() {
        binding.scoreText.text = "↑ $correctCount  ↓ $skippedCount"
    }

    private fun finishGame() {
        timer?.cancel()
        GameSession.result = GameResult(
            correct = correctCount,
            skipped = skippedCount,
            moduleName = GameSession.moduleName
        )
        findNavController().navigate(R.id.action_gameFragment_to_resultsFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        _binding = null
    }
}
