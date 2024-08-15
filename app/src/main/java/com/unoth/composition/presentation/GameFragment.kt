package com.unoth.composition.presentation

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.unoth.composition.databinding.FragmentGameBinding
import com.unoth.composition.domain.entity.GameResult

class GameFragment : Fragment() {
    private val args by navArgs<GameFragmentArgs>()

    private val viewModelFactory by lazy {
        ViewModelFactory(requireActivity().application, args.level)
    }

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]
    }

    private val tvOptions by lazy {
        mutableListOf<TextView>().apply {
            add(binding.tvOption1)
            add(binding.tvOption2)
            add(binding.tvOption3)
            add(binding.tvOption4)
            add(binding.tvOption5)
            add(binding.tvOption6)
        }
    }

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding ?: throw RuntimeException("FragmentGameBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setClickListenersForOptions()
    }

    private fun setClickListenersForOptions() {
        for (tvOptions in tvOptions) {
            tvOptions.setOnClickListener {
                viewModel.choiceAnswer(tvOptions.text.toString().toInt())
            }
        }
    }

    private fun observeViewModel() {
        viewModel.question.observe(viewLifecycleOwner) {
            binding.tvSum.text = it.sum.toString()
            binding.tvLeftNumber.text = it.visibleNumber.toString()
            for (i in 0 until tvOptions.size) {
                tvOptions[i].text = it.options[i].toString()
            }
        }
        viewModel.formattedTime.observe(viewLifecycleOwner) {
            binding.tvTimer.text = it
        }
        viewModel.percentOfRightAnswer.observe(viewLifecycleOwner) {
            binding.progressBar.setProgress(it, true)
        }
        viewModel.minPercent.observe(viewLifecycleOwner) {
            binding.progressBar.secondaryProgress = it
        }
        viewModel.progressAnswers.observe(viewLifecycleOwner) {
            binding.tvAnswersProgress.text = it
        }
        viewModel.enoughRightAnswers.observe(viewLifecycleOwner) {
            val color = setColorForState(it)
            binding.tvAnswersProgress.setTextColor(color)
        }
        viewModel.enoughPercentAnswers.observe(viewLifecycleOwner) {
            val color = setColorForState(it)
            binding.progressBar.progressTintList = ColorStateList.valueOf(color)
        }
        viewModel.gameResult.observe(viewLifecycleOwner) {
            launchGameFinishedFragment(it)
        }
    }

    private fun setColorForState(goodState: Boolean): Int {
        val colorResId = if (goodState) {
            android.R.color.holo_green_dark
        } else {
            android.R.color.holo_red_dark
        }
        return ContextCompat.getColor(requireContext(), colorResId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun launchGameFinishedFragment(gameResult: GameResult) {
        findNavController().navigate(
            GameFragmentDirections.actionGameFragmentToGameFinishedFragment(
                gameResult
            )
        )
    }
}