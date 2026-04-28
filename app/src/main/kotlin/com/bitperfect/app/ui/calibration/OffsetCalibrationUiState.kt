package com.bitperfect.app.ui.calibration

data class OffsetCalibrationUiState(
    val steps: List<CalibrationStepState> = listOf(
        CalibrationStepState.WaitingForDisc,
        CalibrationStepState.WaitingForDisc,
        CalibrationStepState.WaitingForDisc
    ),
    val activeStepIndex: Int = 0,
    val calibrationResult: CalibrationResult? = null
)
