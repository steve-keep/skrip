# BitPerfect Design System: The High-Fidelity Diagnostic

## 1. Overview
The Creative North Star for this design system is **"The High-Fidelity Diagnostic."** It aims for technical precision combined with editorial depth, moving away from rigid engineering dashboards toward a sophisticated, breathable experience.

## 2. Colors & Surface Architecture
- **Primary:** #2B3896 (Technic Authority)
- **Secondary:** #6F48B2 (Modern Fluidity)
- **Tertiary:** #6C3400 (Warning/Accent without alarm)
- **Background:** #F8F9FF (Ethereal calm)

### The "No-Line" Rule
Explicitly avoid 1px solid borders for sectioning. Boundaries are defined solely through background shifts.

### Surface Hierarchy (Light Mode)
- **Base Layer:** Background (#F8F9FF)
- **Primary Containers:** Surface Container (#ECEEF3)
- **Nested Detail:** Surface Container High (#E7E8EE)
- **Prominence:** Surface Container Highest (#E1E2E8)
- **Deep Recess:** Surface Container Low (#F2F3F9)
- **Glass/Floating:** Surface Container Lowest (#FFFFFF) with opacity & blur.

## 3. Typography
A dual-font approach balancing human readability with technical authority.
- **Display & Headlines (Manrope):** Geometric, modern energy.
- **Body & Labels (Inter):** Neutral, high-legibility for technical logs.

## 4. Elevation & Depth
Depth is achieved through **Tonal Layering** rather than traditional drop shadows.
- Lift is created by placing lighter surfaces on darker ones (e.g., Lvl 0 on Lvl -1).

## 5. Components
- **Buttons:** Rounded (Radius: full), Gradient transitions, authoritative text.
- **Input Fields:** Soft corner radius (0.5rem), high-contrast labels above the field.
- **Technical Logs:** No divider lines. Use vertical white space (1.5rem) or alternate background shifts.
- **Diagnostic Cards:** Large corner radius (1.5rem / 24dp).

## 6. Iconography & Logo
- **App Logo:** Abstract CD disc dissolving into "bits".
- **Style:** Flat design, high contrast.
- **Logo Context:** Transparent background on UI, but the logo element itself should have a dark background context when used in headers.
