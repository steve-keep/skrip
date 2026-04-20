import re

with open("core/src/test/kotlin/com/bitperfect/core/engine/DriveCapabilityDetectorTest.kt", "r") as f:
    content = f.read()

if "import com.bitperfect.driver.IScsiDriver" not in content:
    content = content.replace("import io.mockk.mockk", "import io.mockk.mockk\nimport com.bitperfect.driver.IScsiDriver")

content = content.replace("val cmd = arg<ByteArray>(1)", "val cmd = it.invocation.args[1] as ByteArray")

with open("core/src/test/kotlin/com/bitperfect/core/engine/DriveCapabilityDetectorTest.kt", "w") as f:
    f.write(content)
print("Success")
