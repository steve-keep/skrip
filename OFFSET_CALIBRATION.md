# Offset Calibration Algorithm

This document defines the methodology for determining the sample-accurate read offset of an optical drive using a reference disc (Key Disc) present in the AccurateRip database.

## 1. Overview

Every CD drive starts reading audio a consistent number of samples "too early" or "too late" compared to the pressing master. To achieve BitPerfect rips, we must detect this offset and correct for it.

We use the AccurateRip database. For a known disc, the database stores the checksum (ARv2) for track 1 at an offset of exactly `0` samples (assuming a perfect drive).

If we extract track 1, we can compute checksums at various offsets until we match the database. The offset that produces the match is our drive's read offset.

## 2. Process Flow

### Step A: Key Disc Verification
When a user inserts a disc during the calibration wizard:
1. Read the disc TOC.
2. Compute the AccurateRip disc IDs (id1, id2, id3).
3. Request `http://www.accuraterip.com/accuraterip/{a}/{b}/{c}/dAR{id1}-{id2}-{id3}.bin`.
4. If a 200 OK response with parsable pressings is returned, the disc is a **Key Disc**.
5. Save the list of expected checksums for Track 1.

### Step B: The Shifted-Window Scan
When the user taps "Start Scan":
1. Read Track 1 into a memory buffer (or process it in chunks).
2. For candidate offsets in the range `-5000` to `+5000` samples:
   - Apply the candidate offset to the audio data (shifting bytes left or right).
   - Compute the ARv2 checksum of the shifted audio.
   - Compare the computed checksum against the expected checksums from Step A.
3. If a match is found:
   - Record the candidate offset.
   - Transition to `Success` state.
4. The wizard repeats this for 3 separate discs to build confidence.

### Step C: Result Confidence
After all 3 steps succeed, we have 3 candidate offsets: `o1, o2, o3`.

**Rule:** At least two candidates must agree exactly to constitute a "Pass".
- If `o1 == o2`, the final offset is `o1`. Confidence is Pass.
- If all three are completely different, Confidence is Fail.

The final confirmed offset is then stored to the device preferences and applied to all future rips.

*(Note: The actual checksum calculation code will be implemented in `AccurateRipVerifier`.)*
