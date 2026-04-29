package com.bitperfect.app.ui.calibration

sealed class SaveState {
    data object Idle : SaveState()
    data object Saving : SaveState()
    data object Finished : SaveState()
    data class Error(val message: String) : SaveState()
}

data class OffsetCalibrationUiState(
    val steps: List<CalibrationStepState> = listOf(
        CalibrationStepState.WaitingForDisc,
        CalibrationStepState.WaitingForDisc,
        CalibrationStepState.WaitingForDisc
    ),
    val activeStepIndex: Int = 0,
    val calibrationResult: CalibrationResult? = null,
    val saveState: SaveState = SaveState.Idle
)
