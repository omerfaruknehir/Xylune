package app.xylune.chat.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Compatibility shim for the old Settings overlay.
 *
 * Language selection now lives in the normal Settings hierarchy. MainActivity
 * still calls this symbol on older source branches, so keep it deliberately
 * empty until that legacy call is removed in a later cleanup.
 */
@Deprecated("Language is now a normal Settings destination")
@Composable
internal fun AppLanguageMenuButton(
    @Suppress("UNUSED_PARAMETER") modifier: Modifier = Modifier,
) = Unit
