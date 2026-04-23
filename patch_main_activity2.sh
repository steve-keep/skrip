sed -i '/private lateinit var driveOffsetRepository: DriveOffsetRepository/d' app/src/main/kotlin/com/bitperfect/app/MainActivity.kt
sed -i '58a\
    private lateinit var driveOffsetRepository: DriveOffsetRepository\
' app/src/main/kotlin/com/bitperfect/app/MainActivity.kt

sed -i 's/SettingsScreen(/SettingsScreen(\n                                            driveOffsetRepository = driveOffsetRepository,/g' app/src/main/kotlin/com/bitperfect/app/MainActivity.kt
