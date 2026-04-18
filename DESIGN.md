# BitPerfect — Design Language & Component Reference

> **Audience:** Software engineers building on or extending the BitPerfect mobile app.  
> **Scope:** Color tokens, typography, spacing, component anatomy, states, and interaction patterns observed across all five screens.

---

## 1. Design Philosophy

BitPerfect is a **precision audio tool**. The visual language reflects this: dark, high-contrast, technical, and serious — like a professional audio interface or a high-end piece of hi-fi equipment. Every design decision leans into credibility and clarity. There is no decorative noise; every element earns its place.

**Core tenets:**
- **Darkness as authority** — near-black backgrounds communicate focus and signal quality
- **Green as verification** — the accent color exclusively signals correctness, success, and active states
- **Monospace for data** — technical values (paths, logs, CRC hashes) always use a monospace face
- **Density with breathing room** — data-rich screens avoid cramping via consistent internal card padding

---

## 2. Color Tokens

### 2.1 Background Palette

| Token | Hex | Usage |
|---|---|---|
| `--bg-base` | `#0A0A0A` | App root / screen background |
| `--bg-surface` | `#141414` | Cards, panels, modal surfaces |
| `--bg-surface-raised` | `#1C1C1C` | Elevated elements, input fields, inner card rows |
| `--bg-surface-hover` | `#222222` | Hover / pressed state for list rows |
| `--bg-overlay` | `rgba(255,255,255,0.04)` | Subtle differentiator between adjacent sections |

### 2.2 Accent / Brand

| Token | Hex | Usage |
|---|---|---|
| `--accent-primary` | `#3DDC68` | Primary CTA buttons, active toggles, progress bars, "secure" badges, AccurateRip badges |
| `--accent-primary-dim` | `#2AAF4F` | Pressed/active state of primary accent |
| `--accent-primary-subtle` | `rgba(61,220,104,0.12)` | Background tint on active list rows, badge fills |
| `--accent-primary-text` | `#3DDC68` | Status labels, badge text (e.g. "AR ACCURATE", "100% Secure") |

### 2.3 Semantic Colors

| Token | Hex | Usage |
|---|---|---|
| `--color-success` | `#3DDC68` | Verified / complete states (same as accent) |
| `--color-error` | `#E53935` | Abort button background, destructive actions |
| `--color-warning` | `#F5A623` | "Metadata Missing" label, warning icons |
| `--color-info-blue` | `#2979FF` | AccurateRip verified checkmark overlay (blue circle on toggle) |

### 2.4 Text Palette

| Token | Hex | Usage |
|---|---|---|
| `--text-primary` | `#FFFFFF` | Headings, track names, prominent labels |
| `--text-secondary` | `rgba(255,255,255,0.60)` | Body copy, descriptions, sub-labels |
| `--text-tertiary` | `rgba(255,255,255,0.35)` | Placeholders, timestamps ("2 hours ago"), disabled text |
| `--text-mono` | `#3DDC68` | Monospace code output, log entries, file path previews |
| `--text-mono-dim` | `rgba(61,220,104,0.55)` | Secondary monospace (e.g. output path preview line) |

### 2.5 Border / Divider

| Token | Hex | Usage |
|---|---|---|
| `--border-default` | `rgba(255,255,255,0.08)` | Card borders, list dividers |
| `--border-active` | `#3DDC68` | Left-edge highlight on the currently active list row |
| `--border-input` | `rgba(255,255,255,0.12)` | Input field borders |

---

## 3. Typography

### 3.1 Typefaces

| Role | Family | Notes |
|---|---|---|
| **Display / Headings** | Sans-serif, slightly wide tracking | Bold weight. Used for screen titles ("Extraction Telemetry", "The Dark Side of the Moon") |
| **UI / Body** | System sans-serif (SF Pro / Roboto equivalent) | Regular and medium weights for all body copy and labels |
| **Monospace / Technical** | Monospace (Courier-style or JetBrains Mono equivalent) | Exclusively for file paths, log output, naming scheme templates, CRC hashes |

### 3.2 Type Scale

| Style | Size | Weight | Line Height | Letter Spacing | Usage |
|---|---|---|---|---|---|
| `display-xl` | 32px | 700 | 1.15 | −0.5px | Album title (track detail screen) |
| `heading-lg` | 28px | 700 | 1.2 | −0.3px | Screen section title ("Extraction Telemetry") |
| `heading-md` | 22px | 700 | 1.25 | −0.2px | Section headings ("Storage & Paths", "Verification") |
| `heading-sm` | 17px | 600 | 1.3 | 0 | Card titles ("AccurateRip", "C2 Error Pointers"), track names |
| `body-md` | 15px | 400 | 1.5 | 0 | Descriptions, body copy |
| `body-sm` | 13px | 400 | 1.45 | 0 | Sub-labels, secondary metadata |
| `label-caps` | 11px | 600 | 1.0 | +1.0px | ALL-CAPS section labels ("ACTIVE HARDWARE", "READ SPEED", "TABLE OF CONTENTS") |
| `mono-md` | 14px | 400 | 1.5 | 0 | Naming scheme input, log terminal |
| `mono-sm` | 12px | 400 | 1.6 | 0 | Output path preview |
| `data-xl` | 48px | 700 | 1.0 | −1px | Large telemetry values ("14.2x", "+667") |
| `data-unit` | 14px | 400 | 1.0 | +0.5px | Unit labels next to data values ("SAMPLES", "x") |

---

## 4. Spacing System

BitPerfect uses a **base-8 spacing scale**.

| Token | Value | Usage |
|---|---|---|
| `--space-2` | 2px | Micro gaps (badge inner padding vertical) |
| `--space-4` | 4px | Icon-to-label gaps, tight inline spacing |
| `--space-8` | 8px | Badge horizontal padding, label-to-control gaps |
| `--space-12` | 12px | List row vertical padding, input field vertical padding |
| `--space-16` | 16px | Card internal padding, horizontal screen margin |
| `--space-20` | 20px | Between card sections within a screen |
| `--space-24` | 24px | Between major section groups |
| `--space-32` | 32px | Between top-level screen sections |
| `--space-48` | 48px | Section title bottom margin on content-heavy screens |

**Screen horizontal margin:** `16px` on both sides consistently.

**Card internal padding:** `16px` horizontal, `16px` vertical.

**List row padding:** `12px` vertical, `16px` horizontal (within a card).

---

## 5. Shape & Elevation

### 5.1 Border Radius

| Token | Value | Usage |
|---|---|---|
| `--radius-sm` | 6px | Badges, small chips ("FLAC", "16-bit/44.1kHz"), tag pills |
| `--radius-md` | 10px | Input fields, secondary buttons ("Browse") |
| `--radius-lg` | 14px | Cards, surface panels |
| `--radius-xl` | 20px | Primary CTA button ("Begin Secure Rip") |
| `--radius-full` | 9999px | Toggle switches, circular icon containers, progress ring |

### 5.2 Shadows / Elevation

The app is predominantly flat. Elevation is implied through background color contrast rather than drop shadows.

| Level | Description |
|---|---|
| Level 0 | `--bg-base` (`#0A0A0A`) — raw screen |
| Level 1 | `--bg-surface` (`#141414`) — standard card |
| Level 2 | `--bg-surface-raised` (`#1C1C1C`) — input fields, inner rows inside cards |
| Level 3 | Active/focused input: `1px` border using `--border-active` (`#3DDC68`) |

---

## 6. Iconography

- **Style:** Outlined/stroked, minimal fill, rounded terminals — consistent with SF Symbols or Material Symbols Rounded style
- **Size:** 20px standard UI icons; 24px in navigation bar; 16px for inline context icons
- **Color:** `--text-secondary` for inactive; `--accent-primary` for active/verified states
- **Logo mark:** Concentric circle with center dot — monochrome white in nav, full green on splash/brand instances

---

## 7. Components

### 7.1 Navigation Bar

```
┌─────────────────────────────────────────────┐
│  ← Back       Screen Title      Logo + Name  │
│  [icon 24px]  [heading-md bold] [brand mark] │
└─────────────────────────────────────────────┘
```

- **Height:** ~56px
- **Background:** `--bg-base` (no elevation change from screen)
- **Back arrow:** Left-pointing chevron, `--text-primary`, 24px
- **Screen title:** `heading-md`, centered or left-aligned
- **Brand mark:** Top-right, logo icon + "BitPerfect" in `--accent-primary`, `heading-sm` weight 600
- **Divider:** None visible — blends into screen background

---

### 7.2 Cards / Surface Panels

```
┌──────────────────────────────────────────────┐  ← radius-lg (14px)
│  [Label]                                     │  ← bg-surface (#141414)
│                                              │  ← padding: 16px all sides
│  [Content]                                   │
│                                              │
└──────────────────────────────────────────────┘
```

- **Background:** `--bg-surface`
- **Border:** `1px solid --border-default` (`rgba(255,255,255,0.08)`)
- **Border radius:** `--radius-lg` (14px)
- **Padding:** 16px
- **Margin from screen edge:** 16px each side

**Card with internal list rows** (e.g. Settings toggles, Extraction Queue):
- Rows are separated by `1px` dividers using `--border-default`
- Row padding: `12px` vertical, `16px` horizontal
- No border-radius on individual rows — only the outer card clips them

---

### 7.3 Buttons

#### Primary CTA Button
```
┌──────────────────────────────────────────┐
│  🚀  Begin Secure Rip                    │
└──────────────────────────────────────────┘
```
- **Background:** `--accent-primary` (`#3DDC68`)
- **Text color:** `#000000` (black on green)
- **Font:** `heading-sm`, weight 700
- **Height:** 56px
- **Border radius:** `--radius-xl` (20px)
- **Icon:** Leading icon, 20px, black
- **Full width** with `16px` horizontal margin
- **Position:** Pinned to bottom of screen (safe-area aware)

#### Destructive Button (Abort)
- **Background:** `--color-error` (`#E53935`)
- **Text color:** `#FFFFFF`
- Same sizing and radius as Primary CTA

#### Secondary Button (Browse)
- **Background:** `--bg-surface-raised` (`#1C1C1C`)
- **Text color:** `--text-primary`
- **Font:** `body-md`, weight 500
- **Height:** 44px
- **Border radius:** `--radius-md` (10px)
- **Border:** `1px solid --border-input`

---

### 7.4 Toggle Switch

Two visual states observed:

**ON (Active):**
- Track background: `--accent-primary` (`#3DDC68`)
- Thumb: White circle
- Size: ~51×31px (iOS-style)
- Optional secondary verified badge: Blue checkmark circle (20px) overlaid at right edge of thumb — used on "AccurateRip" to indicate additional verified state

**OFF (Inactive):**
- Track background: `rgba(255,255,255,0.15)` (dark gray)
- Thumb: White circle

---

### 7.5 Input Fields

#### Text Input (Path / Naming Scheme)
```
┌──────────────────────────────────────────┐
│  🗂  /Volumes/AudioArchive...            │
└──────────────────────────────────────────┘
```
- **Background:** `--bg-surface-raised`
- **Border:** `1px solid --border-input`
- **Border radius:** `--radius-md` (10px)
- **Height:** 44px
- **Padding:** `0 12px`
- **Text:** `mono-md`, `--text-primary`
- **Placeholder / overflow:** Truncated with ellipsis
- **Leading icon:** Folder/file icon, 18px, `--text-secondary`

**Focused / Valid state:**
- Border color changes to `--accent-primary`
- "Valid syntax" badge appears inline at label level (top-right of field group)

#### Monospace Code Block (Output Preview)
- **Background:** `--bg-surface-raised` (slightly darker)
- **Text:** `mono-sm`, `--text-mono-dim`
- **No border**, slightly inset appearance
- Used below naming scheme input to show rendered output example

---

### 7.6 Status / Validation Badge

```
  ┌───────────────┐
  │  Valid syntax  │   or   │  AR ACCURATE  │
  └───────────────┘
```
- **Background:** `--accent-primary-subtle` (`rgba(61,220,104,0.12)`)
- **Text:** `label-caps`, `--accent-primary`, weight 600
- **Border radius:** `--radius-sm` (6px)
- **Padding:** `2px 8px`
- **Optional leading icon:** Checkmark (✓) for verified states

**Warning variant:**
- Background: `rgba(245,166,35,0.12)`
- Text: `--color-warning` (`#F5A623`)
- Used for "Metadata Missing"

---

### 7.7 Format / Spec Tags (Pills)

```
  ┌──────┐  ┌─────────────┐
  │ FLAC │  │ 16-bit/44.1kHz │
  └──────┘  └─────────────┘
```
- **Background:** `--bg-surface-raised`
- **Text:** `label-caps` or `body-sm`, `--text-secondary`, weight 500
- **Border:** `1px solid --border-default`
- **Border radius:** `--radius-sm` (6px)
- **Padding:** `4px 8px`
- **Display:** Inline-flex, gap `6px` between multiple tags

---

### 7.8 Progress Ring (Circular)

Used on the active rip progress screen.

- **Size:** ~160px diameter
- **Track color:** `rgba(255,255,255,0.08)` (background arc)
- **Fill color:** `--accent-primary` (`#3DDC68`)
- **Stroke width:** ~10px
- **Center content:**
  - Percentage value: `data-xl` (48px, bold, white)
  - Sub-label: `body-sm`, `--accent-primary` ("Bit-for-Bit")
- **Animation:** Stroke-dasharray driven, smooth progress

---

### 7.9 Progress Bar (Linear)

Used in extraction queue track rows.

- **Height:** 3px
- **Background track:** `rgba(255,255,255,0.10)`
- **Fill:** `--accent-primary`
- **Border radius:** `--radius-full`

---

### 7.10 Extraction Queue List Row

Three states:

**Completed row:**
```
  ┌────────────────────────────────────────────┐
  │  [01]  Angel              ✓  AR ACCURATE   │
  │        6:18 · FLAC 16/44.1                 │
  └────────────────────────────────────────────┘
```
- Track number badge: dark pill, `--text-tertiary`, `mono-sm`
- Title: `heading-sm`, `--text-primary`
- Duration + format: `body-sm`, `--text-tertiary`
- Status badge: Green checkmark + "AR ACCURATE" text in `--accent-primary`

**Active (currently ripping) row:**
- Left border: `3px solid --accent-primary`
- Background: `--accent-primary-subtle`
- Title: Bold white
- Progress bar below title
- Sector position shown: `mono-sm`, `--text-tertiary`
- Percentage: `--accent-primary`, `body-sm`, right-aligned

**Queued (pending) row:**
- Track number and title: `--text-tertiary` (dimmed)
- Clock icon: right-aligned, `--text-tertiary`

---

### 7.11 Hardware / Drive Card

```
┌────────────────────────────────────────────────┐
│  [icon]  Pioneer BDR-XD07B      ┌──────────┐   │
│          ● Online / Ready to Rip │  USB 3.0 │   │
│                                  └──────────┘  › │
└────────────────────────────────────────────────┘
```
- Full-width card, `--bg-surface`
- Drive icon: square hardware icon, 36px, `--text-secondary`
- Status dot: 8px filled circle — green (`--accent-primary`) when online
- Connection badge: `--bg-surface-raised` pill with `label-caps` text
- Chevron `›`: Right-aligned, indicates navigable row

---

### 7.12 Recent Archive / Album Card

```
┌────────────────────────────────────────────────┐
│           [Album Art 80×80px]                  │
│           Discovery                            │
│           Daft Punk                            │
│     ┌──────┐ ┌────────────────┐                │
│     │ FLAC │ │ 16-bit / 44.1kHz│               │
│     └──────┘ └────────────────┘                │
│  ✓  100% Secure  ·  AccurateRip Verified       │
│     2 hours ago                                │
└────────────────────────────────────────────────┘
```
- **Album art:** 80×80px, `--radius-md` (10px), object-fit: cover
- **Missing art state:** Gray placeholder with centered disc icon
- **Title:** `heading-sm`, white
- **Artist:** `body-sm`, `--text-secondary`
- **Status line:** Checkmark icon + status text in `--accent-primary` (success) or `--color-warning` (warning)
- **Sub-status:** `body-sm`, `--text-tertiary`
- **Timestamp:** `body-sm`, `--text-tertiary`

---

### 7.13 Telemetry Data Card

Used on the Extraction Telemetry / Diagnostics screen.

```
┌────────────────────────────────────────────────┐
│  READ SPEED                              [icon] │
│                                                 │
│  14.2 x                                         │
│  ████████████░░░░░░░░░░░░░░░                   │
└────────────────────────────────────────────────┘
```
- Label: `label-caps`, `--text-tertiary`
- Icon: 20px, top-right, `--text-secondary`
- Value: `data-xl`, white
- Unit: `data-unit`, `--text-secondary`, vertically bottom-aligned with value
- Optional sub-bar: Linear progress bar, green fill

---

### 7.14 Log / Terminal Output Block

```
┌─ ● ● ●  SCSI_VERBOSE_LOG.log ─────────────────┐
│                                                 │
│  [00:00:01] [SYS] Initializing SCSI transport  │
│  [00:00:06] [READ] LBA 000026 → 014582 ...     │
│  [00:00:07] [VERIFY] Track 01 CRC: A4B7C98F    │
│                                                 │
└────────────────────────────────────────────────┘
```
- **Background:** `--bg-surface` with slight inner shadow/border
- **Title bar:** Faux macOS window chrome — red/amber/green traffic-light dots (decorative), filename in `mono-md`, `--text-tertiary`
- **Log text:** `mono-sm`, `--text-primary`
- **Tag tokens** (`[SYS]`, `[READ]`, `[VERIFY]`, `[RECOVER]`): Bold weight, same monospace face
- **Tag colors:**
  - `[SYS]` — `--text-tertiary`
  - `[READ]` — `--text-primary` (white)
  - `[VERIFY]` — `--accent-primary`
  - `[RECOVER]` — `--color-warning`
  - `[INFO]` — `--text-secondary`
- Scrollable, vertically, no horizontal scroll

---

### 7.15 Track List Row (Album View)

```
  01   Speak to Me                          1:13
  06 ▌ Money                   PREVIEW      6:22   ← active row
  10   Eclipse                              2:06
```
- Track number: `mono-sm`, `--text-tertiary`, fixed 24px width
- Track title: `body-md`, `--text-primary`
- Duration: `body-sm`, `--text-tertiary`, right-aligned
- **Active row indicator:** `3px` left border in `--accent-primary`, row background `--accent-primary-subtle`
- **Preview badge:** `label-caps` pill, `--bg-surface-raised` background, `--text-secondary` text
- Row height: ~48px
- Divider: none (open list, relies on row padding)

---

### 7.16 Section Header

Appears above major grouped sections within a screen:

```
  Storage & Paths
  Configure where and how your rips are saved.
```
- Title: `heading-md`, white, weight 700
- Subtitle: `body-md`, `--text-secondary`
- Margin below before first card: `16px`
- Margin above (from previous section): `32px`

---

### 7.17 Active Status Banner

```
  ┌──────────────────────────────────────┐
  │  ⠿  SECURE RIP ACTIVE               │
  └──────────────────────────────────────┘
```
- **Background:** `--accent-primary-subtle`
- **Border:** `1px solid --accent-primary`
- **Border radius:** `--radius-md`
- **Text:** `label-caps`, `--accent-primary`, weight 700
- **Leading icon:** Animated waveform bars (3-bar audio animation), `--accent-primary`
- **Padding:** `10px 16px`

---

### 7.18 Speed / Mode Chip (Inline)

```
  ┌──────────────┐   ┌────────────────────┐
  │  4.2X SPEED  │   │  C2 ERROR CHECK    │
  └──────────────┘   └────────────────────┘
```
- **Background:** `--bg-surface-raised`
- **Border:** `1px solid --border-default`
- **Border radius:** `--radius-sm`
- **Text:** `label-caps`, `--text-secondary`
- **Leading icon:** Small context icon (speedometer, gear) in `--accent-primary`
- **Padding:** `6px 10px`

---

## 8. States

| State | Visual Treatment |
|---|---|
| **Default** | Base colors as documented |
| **Hover / Pressed** | Background shifts to `--bg-surface-hover` (`#222222`); no scale transform |
| **Active (selected row)** | Left border `3px --accent-primary`; background `--accent-primary-subtle` |
| **Disabled** | Opacity `0.35`; non-interactive |
| **Loading / In-progress** | Green progress bar or ring; animated waveform icon |
| **Success** | Green checkmark badge; "AR ACCURATE" or "100% Secure" label |
| **Warning** | Orange/amber icon and label ("Metadata Missing", "Not in Database") |
| **Error** | Red destructive button; not used inline for form errors in observed screens |
| **Toggle ON** | Green track, white thumb, optional blue verified overlay |
| **Toggle OFF** | Dark gray track, white thumb |

---

## 9. Motion & Animation

- **Transitions:** `200ms ease-out` for state changes (toggle, row highlight)
- **Progress ring:** Smooth `stroke-dashoffset` transition, no easing snap
- **Waveform animation:** 3-bar equalizer bars animating independently at ~120bpm cadence (CSS keyframes or Lottie)
- **Log streaming:** New lines append with no animation — raw feel is intentional
- **Screen transitions:** Standard platform push transition (no custom animation observed)

---

## 10. Layout Patterns

### Screen Structure
```
┌─────────────────────────────────┐
│  Navigation Bar (56px)          │
├─────────────────────────────────┤
│                                 │
│  Screen content (scrollable)    │
│  16px horizontal margin         │
│  32px between major sections    │
│                                 │
├─────────────────────────────────┤
│  Pinned CTA (if present, 56px)  │
│  + safe area bottom padding     │
└─────────────────────────────────┘
```

### Information Hierarchy Pattern (repeated across screens)
1. `label-caps` section identifier (e.g. "ACTIVE HARDWARE", "READ SPEED")
2. Primary value or heading in `heading-md` or `data-xl`
3. Secondary detail in `body-sm` `--text-secondary`
4. Status badge or action at trailing edge

---

## 11. Platform Considerations

- **Target platform:** iOS (primary — SF Pro inferred) with potential Android parity
- **Safe area:** Bottom CTA buttons respect home indicator safe area (`padding-bottom: env(safe-area-inset-bottom)`)
- **Status bar:** Dark content — assumes dark status bar icons on black background
- **Minimum tap target:** 44×44px across all interactive elements
- **Scroll behavior:** Native momentum scrolling; no custom scrollbars
- **Dark mode only** — no light mode variant observed

---

## 12. Quick Reference — Token Summary

```css
:root {
  /* Backgrounds */
  --bg-base:            #0A0A0A;
  --bg-surface:         #141414;
  --bg-surface-raised:  #1C1C1C;
  --bg-surface-hover:   #222222;

  /* Accent */
  --accent-primary:         #3DDC68;
  --accent-primary-dim:     #2AAF4F;
  --accent-primary-subtle:  rgba(61, 220, 104, 0.12);

  /* Semantic */
  --color-success:   #3DDC68;
  --color-error:     #E53935;
  --color-warning:   #F5A623;
  --color-info-blue: #2979FF;

  /* Text */
  --text-primary:    #FFFFFF;
  --text-secondary:  rgba(255, 255, 255, 0.60);
  --text-tertiary:   rgba(255, 255, 255, 0.35);
  --text-mono:       #3DDC68;
  --text-mono-dim:   rgba(61, 220, 104, 0.55);

  /* Borders */
  --border-default:  rgba(255, 255, 255, 0.08);
  --border-active:   #3DDC68;
  --border-input:    rgba(255, 255, 255, 0.12);

  /* Spacing */
  --space-2:   2px;
  --space-4:   4px;
  --space-8:   8px;
  --space-12:  12px;
  --space-16:  16px;
  --space-20:  20px;
  --space-24:  24px;
  --space-32:  32px;
  --space-48:  48px;

  /* Radius */
  --radius-sm:   6px;
  --radius-md:   10px;
  --radius-lg:   14px;
  --radius-xl:   20px;
  --radius-full: 9999px;
}
```
