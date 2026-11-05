# Changelog

All notable changes to lib-ui. Versions follow [Semantic Versioning](https://semver.org/)
(in 0.x, only a new minor version may break the interface; CI enforces it).

## [0.2.1]

### Added
- `tokens/`: `ThemePalette` (every colour role a UI draws, plus a theme's typefaces and texture, and droidtop's own
  palette as `DEFAULT`), `FocusRingInks` and `noticeColors`/`noticeInputs`. They are droidtop runtime-common's
  `GamingThemeColors`, `FocusRingInks` and `NoticeInputs.kt`, moved unchanged but for the palette's name, so the
  shared UI reads a palette without droidtop's theme engine (Droidtop/tracker#867, ui-shared step 2). New tests.

## [0.2.0]

### Added
- `notices/`: the notice colours shared by droidtop and Enginehost (`ColorMath`, `NoticeColors`, `NoticeChecks`),
  Droidtop/notices moved here with its three commits, unchanged but for its build file, which is now a module of
  this build. droidtop pins Droidtop/notices at its last commit (64ad439), so this is the same code.
  Droidtop/tracker#867 step 1.
- The public API is checked in (`contract/kotlin/api/contract.api`, `notices/api/notices.api`) and CI holds the code to
  it: `apiCheck`, `scripts/check-api-compat.sh` for Kotlin and `cargo-semver-checks` for the Rust contract fail a
  break the version does not announce (Droidtop/tracker#867).

## [0.1.0]

### Added
- `contract/`: the backend contract (`ok`, `unavailable`, `error`, `pending`) as Pythia's web UI and backend
  already speak it, written down, with one set of fixtures and an implementation in Kotlin
  (`dev.droidtop.lib.ui.contract.Answer`), Rust (`ui-contract`), Python (`ui_contract`) and JavaScript
  (`contract.mjs`). Each decodes every fixture the same way, and each has a guard that turns a failure
  (an exception, a Rust panic) into an `error` answer. Droidtop/tracker#867 D8.
