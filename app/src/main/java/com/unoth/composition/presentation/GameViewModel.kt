package com.unoth.composition.presentation

import android.annotation.SuppressLint
import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.unoth.composition.R
import com.unoth.composition.data.GameRepositoryImpl
import com.unoth.composition.domain.entity.GameResult
import com.unoth.composition.domain.entity.GameSettings
import com.unoth.composition.domain.entity.Level
import com.unoth.composition.domain.entity.Question
import com.unoth.composition.domain.usecase.GenerateQuestionUseCase
import com.unoth.composition.domain.usecase.GetGameSettingsUseCase

class GameViewModel(
    private val application: Application,
    private val level: Level
) : ViewModel() {

    private lateinit var gameSettings: GameSettings

    private val repository = GameRepositoryImpl
    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)

    private var timer: CountDownTimer? = null

    private var countOfRightAnswers = 0
    private var countOfQuestions = 0

    private val _formattedTime = MutableLiveData<String>()
    val formattedTime: LiveData<String>
        get() = _formattedTime

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question

    private val _percentOfRightAnswer = MutableLiveData<Int>()
    val percentOfRightAnswer: LiveData<Int>
        get() = _percentOfRightAnswer

    private val _progressAnswers = MutableLiveData<String>()
    val progressAnswers: LiveData<String>
        get() = _progressAnswers

    private val _enoughRightAnswers = MutableLiveData<Boolean>()
    val enoughRightAnswers: LiveData<Boolean>
        get() = _enoughRightAnswers

    private val _enoughPercentAnswers = MutableLiveData<Boolean>()
    val enoughPercentAnswers: LiveData<Boolean>
        get() = _enoughPercentAnswers

    private val _minPercent = MutableLiveData<Int>()
    val minPercent: LiveData<Int>
        get() = _minPercent

    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult>
        get() = _gameResult


    private fun startGame() {
        getGameSetting()
        generateQuestion()
        startTimer()
        updateProgress()
    }

    init {
        startGame()
    }

    fun choiceAnswer(number: Int) {
        val rightAnswer = question.value?.rightAnswer
        if (number == rightAnswer) {
            countOfRightAnswers++
        }
        countOfQuestions++
        updateProgress()
        generateQuestion()
    }

    private fun getGameSetting() {
        this.gameSettings = getGameSettingsUseCase(level)
        _minPercent.value = gameSettings.minPercentOfRightAnswer
    }

    private fun startTimer() {
        timer = object : CountDownTimer(
            gameSettings.gameTimeInSeconds * MILLIS_IN_SEC,
            MILLIS_IN_SEC
        ) {
            override fun onTick(millisUntilFinished: Long) {
                _formattedTime.value = formatTime(millisUntilFinished)
            }

            override fun onFinish() {
                finishGame()
            }
        }
        timer?.start()
    }

    @SuppressLint("DefaultLocale")
    private fun formatTime(millisUntilFinished: Long): String {
        val sec = millisUntilFinished / MILLIS_IN_SEC
        val min = sec / SEC_IN_MIN
        val leftSec = sec - (min * SEC_IN_MIN)

        return String.format("%02d:%02d", min, leftSec)
    }

    private fun generateQuestion() {
        _question.value = generateQuestionUseCase(gameSettings.maxSumValue)
    }

    @SuppressLint("StringFormatMatches")
    private fun updateProgress() {
        val percentProgress = percentOfProgress()
        _percentOfRightAnswer.value = percentProgress
        _progressAnswers.value = String.format(
            application.resources.getString(R.string.progress_answers),
            countOfRightAnswers,
            gameSettings.minCountOfRightAnswers
        )
        _enoughRightAnswers.value =
            countOfRightAnswers >= gameSettings.minCountOfRightAnswers //min for true
        _enoughPercentAnswers.value =
            percentProgress >= gameSettings.minPercentOfRightAnswer //min for true
    }

    private fun percentOfProgress(): Int {
        return ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
    }

    private fun finishGame() {
        _gameResult.value = GameResult(
            enoughRightAnswers.value == true && enoughPercentAnswers.value == true,
            countOfRightAnswers,
            countOfQuestions,
            gameSettings
        )
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    companion object {
        private const val MILLIS_IN_SEC = 1000L
        private const val SEC_IN_MIN = 60
    }
}