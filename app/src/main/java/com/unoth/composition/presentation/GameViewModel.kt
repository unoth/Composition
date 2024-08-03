package com.unoth.composition.presentation

import android.annotation.SuppressLint
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.unoth.composition.data.GameRepositoryImpl
import com.unoth.composition.domain.entity.GameSettings
import com.unoth.composition.domain.entity.Level
import com.unoth.composition.domain.usecase.GenerateQuestionUseCase
import com.unoth.composition.domain.usecase.GetGameSettingsUseCase

class GameViewModel : ViewModel() {

    private lateinit var level: Level
    private lateinit var gameSettings: GameSettings

    private val repository = GameRepositoryImpl
    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)

    private var timer: CountDownTimer? = null

    private val _formattedTime = MutableLiveData<String>()
    val formattedTime: LiveData<String>
        get() = _formattedTime

    fun startGame(level: Level) {
        getGameSetting(level)
        startTimer()
    }

    private fun getGameSetting(level: Level) {
        this.level = level
        this.gameSettings = getGameSettingsUseCase(level)
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

    private fun finishGame() {
        //Implementation
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