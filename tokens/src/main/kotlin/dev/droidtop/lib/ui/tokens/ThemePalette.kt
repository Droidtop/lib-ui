package dev.droidtop.lib.ui.tokens

import dev.droidtop.notices.ColorMath

// droidtop runtime-common's GamingThemeColors (GamingThemeMapping.kt), moved here unchanged but for its name
// (Droidtop/tracker#867: the theme types go to the tokens). droidtop keeps GamingThemeColors as a typealias, and
// its ES-DE theme engine (GamingThemeMapping) stays droidtop's: it is one source of a palette, and Pythia's theme
// is another. The SPEC and tracker references are droidtop's.

/**
 * Every colour role the Gaming chrome draws, as ARGB ints, plus the theme's
 * own typefaces and background texture. `:shell-gamepad`'s design tokens
 * (`MenuTokens`, the Material scheme) are this and nothing else, so one
 * theme change moves every surface droidtop draws itself.
 *
 * [DEFAULT] is droidtop's own palette, the exact values the chrome used
 * before any theme reached it. [declared] is false for it.
 *
 * Serializable so [ThemeColorStore] can keep a resolved palette across process starts (docs/SPEC.md 7k, "Colours
 * outside Gaming").
 */
@kotlinx.serialization.Serializable
data class ThemePalette(
    val ground: Int,
    val overlaySurface: Int,
    val card: Int,
    val cardFocused: Int,
    val cardInset: Int,
    val hintBar: Int,
    val onSurface: Int,
    val onSurfaceMuted: Int,
    val value: Int,
    val placeholder: Int,
    val sectionLabel: Int,
    val onSurfaceDisabled: Int,
    val accent: Int,
    val onAccent: Int,
    val danger: Int,
    val affirmative: Int,
    val favourite: Int,
    val rowFill: Int,
    val rowFillSelected: Int,
    val cardOutline: Int,
    val hintPillOutline: Int,
    val selected: Int,
    val onSelected: Int,
    val scrim: Int,
    val dangerPlate: Int,
    val launch: Int,
    val launchFocused: Int,
    val launchDisabled: Int,
    val onLaunchMuted: Int,
    /** The theme's UI typeface (its help-row font), an existing file, or null. */
    val bodyFontPath: String? = null,
    /** The theme's display typeface (its carousel or title font), or null. */
    val displayFontPath: String? = null,
    /** A tiled background image the theme lays under its ground, or null. */
    val textureImagePath: String? = null,
    val declared: Boolean = true,
) {
    /** True when the ground is light, so text is dark and the Material scheme is a light one. */
    val isLight: Boolean get() = ColorMath.luminance(ground) > 0.4

    companion object {
        /**
         * droidtop's own palette, from the approved visual language (owner, 2026-10-24, Droidtop/tracker#644,
         * coordination/design/proposals/2026-10-24-menus-and-visual-language.md 4.1 and 4.2): a blue-grey ground, never
         * pure black, and three surfaces one luminance step apart (cards, sheets, menus and dialogs); text that is not
         * pure white; one azure accent, used for focus, the primary button and what is chosen. It is the fallback: an
         * ES-DE theme's own colours win wherever it declares them ([GamingThemeMapping]).
         */
        val DEFAULT = ThemePalette(
            ground = 0xFF11151C.toInt(),
            overlaySurface = 0xFF1E2430.toInt(),
            card = 0xFF181D26.toInt(),
            cardFocused = 0xFF262D3A.toInt(),
            cardInset = 0xFF141920.toInt(),
            hintBar = 0xFF151A21.toInt(),
            onSurface = 0xFFF2F5F9.toInt(),
            onSurfaceMuted = 0xFFA9B4C4.toInt(),
            value = 0xFFC9D1DC.toInt(),
            placeholder = 0xFF95A0AF.toInt(),
            sectionLabel = 0xFFA9B4C4.toInt(),
            // 40 percent of the text, as the visual language has it.
            onSurfaceDisabled = 0x66F2F5F9,
            accent = 0xFF3BB5FF.toInt(),
            onAccent = 0xFF0B1220.toInt(),
            danger = 0xFFFF5A5F.toInt(),
            affirmative = 0xFF4CD28A.toInt(),
            favourite = 0xFFFFD700.toInt(),
            rowFill = 0x0DFFFFFF,
            // A focused row: the accent at 22 percent behind it (4.5, "Focus").
            rowFillSelected = 0x383BB5FF,
            cardOutline = 0x1FFFFFFF,
            hintPillOutline = 0x33FFFFFF,
            selected = 0xFFF2F5F9.toInt(),
            onSelected = 0xFF11151C.toInt(),
            scrim = 0xCC000000.toInt(),
            // The ground with the danger mixed in at 22 percent, at 80 percent over the page.
            dangerPlate = 0xCC45242B.toInt(),
            // The primary button: the ground with the accent at 40 percent, its label in the text colour (7.3:1).
            launch = 0xFF225577.toInt(),
            launchFocused = 0xFF3B6887.toInt(),
            launchDisabled = 0xFF141920.toInt(),
            onLaunchMuted = 0xFFD7E3EE.toInt(),
            declared = false,
        )
    }
}
