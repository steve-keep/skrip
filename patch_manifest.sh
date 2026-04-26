#!/bin/bash
mkdir -p app/src/test
if [ ! -f app/src/test/AndroidManifest.xml ]; then
cat << 'XML' > app/src/test/AndroidManifest.xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application>
        <activity android:name="androidx.activity.ComponentActivity" android:exported="true" />
    </application>
</manifest>
XML
fi
