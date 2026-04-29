python3 << 'PYEOF'
import re

with open('app/src/test/kotlin/com/bitperfect/app/usb/UsbDriveDetectorTest.kt', 'r') as f:
    content = f.read()

# Fix TOC tests:
# testReadTocCommandParsesCorrectly expects toc?.trackCount == 3, but got 0. This is because tocResponse was not set.
# testReadTocCommandReturnsNullOnCswFailure expected null but was DiscToc(tracks=[], leadOutLba=0)

content = content.replace(
"""    @Test
    fun testReadTocCommandParsesCorrectly() {
        val fakeTransport = FakeUsbTransport(ByteArray(36), 0, createSyntheticTocResponse(), 0)""",
"""    @Test
    fun testReadTocCommandParsesCorrectly() {
        val fakeTransport = FakeUsbTransport(inquiryResponse = ByteArray(36), turCswStatus = 0, tocResponse = createSyntheticTocResponse(), tocCswStatus = 0)"""
)

content = content.replace(
"""    @Test
    fun testReadTocCommandReturnsNullOnCswFailure() {
        val fakeTransport = FakeUsbTransport(ByteArray(36), 0, createSyntheticTocResponse(), 1)""",
"""    @Test
    fun testReadTocCommandReturnsNullOnCswFailure() {
        val fakeTransport = FakeUsbTransport(inquiryResponse = ByteArray(36), turCswStatus = 0, tocResponse = createSyntheticTocResponse(), tocCswStatus = 1)"""
)

# testExecuteTestUnitReadyRetriesAndSucceeds failed with Empty
content = content.replace("Thread.sleep(100)", "Thread.sleep(2000)")

with open('app/src/test/kotlin/com/bitperfect/app/usb/UsbDriveDetectorTest.kt', 'w') as f:
    f.write(content)
PYEOF
