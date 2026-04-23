sed -i '46a\
import androidx.lifecycle.lifecycleScope\
import kotlinx.coroutines.launch\
import com.bitperfect.core.services.DriveOffsetRepository\
' app/src/main/kotlin/com/bitperfect/app/MainActivity.kt

sed -i '55a\
    private lateinit var driveOffsetRepository: DriveOffsetRepository\
' app/src/main/kotlin/com/bitperfect/app/MainActivity.kt

sed -i '68a\
        driveOffsetRepository = DriveOffsetRepository(this)\
        lifecycleScope.launch {\
            driveOffsetRepository.initialize()\
        }\
' app/src/main/kotlin/com/bitperfect/app/MainActivity.kt
