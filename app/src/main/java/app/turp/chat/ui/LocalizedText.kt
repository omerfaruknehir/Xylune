package app.turp.chat.ui

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text as MaterialText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

/** Localizes Turp-owned plain UI copy while preserving surrounding layout whitespace. */
@Composable
internal fun localizedTurpUiText(text: String): String {
    val language = LocalConfiguration.current.locales[0]?.language
    val leadingWhitespace = text.takeWhile(Char::isWhitespace)
    val trailingWhitespace = text.takeLastWhile(Char::isWhitespace)
    val coreEnd = (text.length - trailingWhitespace.length).coerceAtLeast(leadingWhitespace.length)
    val core = text.substring(leadingWhitespace.length, coreEnd)

    val staticResource = turpUiStringResource(core) ?: turpTurkishCompletionResource(core)
    val localizedCore = if (staticResource != null) {
        stringResource(staticResource)
    } else if (language == "tr") {
        val primary = TurkishUiCopy.translate(core)
        if (primary != core) {
            primary
        } else {
            val secondary = TurkishUiCopyExtra2.translate(core)
            if (secondary != core) {
                secondary
            } else {
                val tertiary = TurkishUiCopyExtra.translate(core)
                if (tertiary != core) {
                    tertiary
                } else {
                    val quaternary = TurkishUiCopyExtra3.translate(core)
                    if (quaternary != core) quaternary else TurkishDynamicUiCopy.translate(core)
                }
            }
        }
    } else {
        core
    }
    return leadingWhitespace + localizedCore + trailingWhitespace
}

/**
 * Localized Material Text facade for Turp-owned UI copy.
 *
 * Direct files in app.turp.chat.ui alias Material3's Text import, so their
 * ordinary String labels pass through this facade. AnnotatedString content is
 * deliberately left untouched because it can contain user or model output.
 */
@Composable
internal fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    MaterialText(
        text = localizedTurpUiText(text),
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style,
    )
}

/** Rich/user/model content is never translated by the UI locale layer. */
@Composable
internal fun Text(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    MaterialText(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style,
    )
}
