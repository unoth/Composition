package com.unoth.composition.domain.usecase

import com.unoth.composition.domain.entity.GameSettings
import com.unoth.composition.domain.entity.Level
import com.unoth.composition.domain.repository.GameRepository

class GetGameSettingsUseCase(private val repository: GameRepository) {
    operator fun invoke(level: Level): GameSettings {
        return repository.getGameSettings(level)
    }
}