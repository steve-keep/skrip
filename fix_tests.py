import re

with open("core/src/test/kotlin/com/bitperfect/core/engine/DriveCapabilityDetectorTest.kt", "r") as f:
    content = f.read()

# Fix string length (Vendor is 8 chars)
content = content.replace('"TestVendor      "', '"TestVend"')
content = content.replace('assertEquals("TestVendor"', 'assertEquals("TestVend"')

with open("core/src/test/kotlin/com/bitperfect/core/engine/DriveCapabilityDetectorTest.kt", "w") as f:
    f.write(content)

with open("core/src/test/kotlin/com/bitperfect/core/engine/VirtualScsiDriverTest.kt", "r") as f:
    content2 = f.read()

# Fix the assert at line 39 in VirtualScsiDriverTest.kt
# line 39 is: assertEquals(0x01.toByte(), response[8])
# In VirtualScsiDriver.kt handleGetConfiguration: response[8] = 0x01
# Wait, let's see why it failed. "expected:<1> but was:<0>"
# In VirtualScsiDriver.kt, I added handleGetConfiguration but maybe `executeScsiCommand` is not routing to it properly?
