# Changelog

All notable changes to lib-ui. Versions follow [Semantic Versioning](https://semver.org/)
(0.x releases may change anything).

## [Unreleased]

### Added
- `contract/`: the backend contract (`ok`, `unavailable`, `error`, `pending`) as Pythia's web UI and backend
  already speak it, written down, with one set of fixtures and an implementation in Kotlin
  (`dev.droidtop.lib.ui.contract.Answer`), Rust (`ui-contract`), Python (`ui_contract`) and JavaScript
  (`contract.mjs`). Each decodes every fixture the same way, and each has a guard that turns a failure
  (an exception, a Rust panic) into an `error` answer. Droidtop/tracker#867 D8.
