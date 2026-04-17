# BitPerfect Agent Instructions

This project follows a specific design language described in [DESIGN_SYSTEM.md](DESIGN_SYSTEM.md).

## Design Constraints
- **No-Line Rule**: Do not use 1px solid borders for sectioning. Use background tonal shifts.
- **Tonal Layering**: Achieve depth through background colors (Surface Container levels) rather than shadows.
- **Editorial Typography**: Use Manrope (Headlines) and Inter (Body) hierarchy.
- **Logo Context**: The app logo must be displayed on a dark background in headers.

## UI Components
Reusable components are defined in `app/src/main/kotlin/com/bitperfect/app/ui/Components.kt`. Always prefer these over raw Compose components to maintain design consistency.

## Tech Stack
- Jetpack Compose for UI.
- Multi-module architecture: `:app` (UI), `:core` (Logic), `:driver` (NDK).
