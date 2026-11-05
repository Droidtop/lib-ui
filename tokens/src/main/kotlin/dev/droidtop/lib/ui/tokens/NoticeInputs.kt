package dev.droidtop.lib.ui.tokens

// droidtop runtime-common's NoticeInputs.kt, moved here unchanged but for the palette's name (Droidtop/tracker#867).

import dev.droidtop.notices.NoticeColors
import dev.droidtop.notices.NoticeInputs

/**
 * What the notice colours (this library's `notices`, docs/SPEC.md 7k "Notices") are derived from: the
 * theme's own surfaces and roles.
 */
fun ThemePalette.noticeInputs(): NoticeInputs = NoticeInputs(
    ground = ground,
    overlaySurface = overlaySurface,
    card = card,
    onSurface = onSurface,
    onSurfaceMuted = onSurfaceMuted,
    accent = accent,
    danger = danger,
)

/** The notice colours of this theme. */
fun ThemePalette.noticeColors(highContrast: Boolean = false): NoticeColors = NoticeColors.derive(noticeInputs(), highContrast)
