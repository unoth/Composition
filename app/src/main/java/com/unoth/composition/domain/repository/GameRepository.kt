package com.unoth.composition.domain.repository

import com.unoth.composition.domain.entity.GameSettings
import com.unoth.composition.domain.entity.Level
import com.unoth.composition.domain.entity.Question

interface GameRepository {
    fun generateQuestion(maxSumValue: Int, countOfOptions: Int): Question

    fun getGameSettings(level: Level): GameSettings
}