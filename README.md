# lib-ui

The shared UI library for droidtop, Pythia and (later) Enginehost: one of the shared library families of
Droidtop/tracker#867. The libraries belong to no program; each program has its own scaffold and pins a
tagged release of each library it uses (#867 D2).

| Part | Holds | Status |
|---|---|---|
| [`contract/`](contract/README.md) | The backend contract every UI speaks: `ok`, `unavailable`, `error` or `pending` (#867 D8), in Kotlin, Rust, Python and JavaScript, tested against one set of fixtures | **here** |
| Compose Multiplatform components | droidtop's `ui-shared` and Droidtop/notices, for Android and desktop | next (#867 step 3) |
| Design tokens | one source generating `DesignTokens.kt` and the test UI's `tokens.css` | next (#867 step 3) |
| HTML test UI and bench | an HTML representation of the UI for prototyping and testing, rendered locally only, seeded from Pythia's web UI (#867 D9, D12) | next (#867 step 3) |

Licence: GPL-3.0 (see `LICENSE`).

## Build and test

CI (`.github/workflows/ci.yml`) runs every implementation against `contract/fixtures/answers.json`:

```bash
./gradlew test                                        # Kotlin
(cd contract/rust && cargo test)                      # Rust
python3 -I -m unittest discover -s contract/python    # Python
node --test contract/js/                              # JavaScript
```

Work lands on `main` from `ci/*` branches once CI is green.

## Versions

The library's version is `lib.version` in `gradle.properties`; the Rust crate's version follows it, and CI
fails if they differ. A release is a tag `vX.Y.Z`: CI creates it on a green `main` whose version is not yet
tagged, so to release, raise the version (and the CHANGELOG) in the change that needs it. Each program pins a tag.
