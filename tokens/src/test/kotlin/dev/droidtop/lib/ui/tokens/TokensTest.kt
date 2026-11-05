package dev.droidtop.lib.ui.tokens

import dev.droidtop.notices.ColorMath
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TokensTest {
    @Test
    fun `the default palette is droidtop's own dark one, not a theme's`() {
        assertFalse(ThemePalette.DEFAULT.isLight)
        assertFalse(ThemePalette.DEFAULT.declared)
    }

    @Test
    fun `a palette survives being stored and read back`() {
        val text = Json.encodeToString(ThemePalette.serializer(), ThemePalette.DEFAULT)
        assertEquals(ThemePalette.DEFAULT, Json.decodeFromString(ThemePalette.serializer(), text))
    }

    @Test
    fun `the focus ring stands out by at least its floor over white, black and its own accent`() {
        for (accent in listOf(ThemePalette.DEFAULT.accent, 0xFFFFFFFF.toInt(), 0xFF000000.toInt(), 0xFFFFD700.toInt())) {
            for (background in listOf(ColorMath.WHITE, ColorMath.BLACK, accent)) {
                assertTrue(FocusRingInks.visibility(accent, background) >= FocusRingInks.FLOOR)
            }
        }
    }

    @Test
    fun `the inner edge is black on a light accent and white on a dark one`() {
        assertEquals(ColorMath.BLACK, FocusRingInks.inner(0xFFFFD700.toInt()))
        assertEquals(ColorMath.WHITE, FocusRingInks.inner(0xFF11151C.toInt()))
    }

    @Test
    fun `a palette's notice colours come from its own roles`() {
        val inputs = ThemePalette.DEFAULT.noticeInputs()
        assertEquals(ThemePalette.DEFAULT.accent, inputs.accent)
        assertEquals(ThemePalette.DEFAULT.danger, inputs.danger)
        ThemePalette.DEFAULT.noticeColors()
        ThemePalette.DEFAULT.noticeColors(highContrast = true)
    }
}
