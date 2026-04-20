import re

with open("core/src/main/kotlin/com/bitperfect/core/engine/DriveCapabilityDetector.kt", "r") as f:
    content = f.read()

# Add missing import for IScsiDriver
if "import com.bitperfect.driver.IScsiDriver" not in content:
    content = content.replace("import kotlinx.coroutines.withContext", "import kotlinx.coroutines.withContext\nimport com.bitperfect.driver.IScsiDriver")

with open("core/src/main/kotlin/com/bitperfect/core/engine/DriveCapabilityDetector.kt", "w") as f:
    f.write(content)
print("Success")
