# Changelog

All notable changes to lib-ui. Versions follow [Semantic Versioning](https://semver.org/)
(0.x releases may change anything).

## [0.2.0]

### Added
- `notices/`: the notice colours shared by droidtop and Enginehost (`ColorMath`, `NoticeColors`, `NoticeChecks`),
  Droidtop/notices moved here with its three commits, unchanged but for its build file, which is now a module of
  this build. droidtop pins Droidtop/notices at its last commit (64ad439), so this is the same code.
  Droidtop/tracker#867 step 1.

## [0.1.0]

### Added
- `contract/`: the backend contract (`ok`, `unavailable`, `error`, `pending`) as Pythia's web UI and backend
  already speak it, written down, with one set of fixtures and an implementation in Kotlin
  (`dev.droidtop.lib.ui.contract.Answer`), Rust (`ui-contract`), Python (`ui_contract`) and JavaScript
  (`contract.mjs`). Each decodes every fixture the same way, and each has a guard that turns a failure
  (an exception, a Rust panic) into an `error` answer. Droidtop/tracker#867 D8.
