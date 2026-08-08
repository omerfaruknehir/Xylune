package app.xylune.chat.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text as MaterialText
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.xylune.chat.R
import app.xylune.chat.settings.AppLanguage
import app.xylune.chat.settings.currentAppLanguage
import app.xylune.chat.settings.setAppLanguage

@Composable
internal fun AppLanguageMenuButton(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var open by remember { mutableStateOf(false) }

    IconButton(
        onClick = { open = true },
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Outlined.Language,
            contentDescription = stringResource(R.string.language),
        )
    }

    if (!open) return

    val selected = currentAppLanguage(context)
    XyluneAlertDialog(
        onDismissRequest = { open = false },
        title = { Text(stringResource(R.string.language_dialog_title)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.language_description),
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                LanguageChoice(
                    title = stringResource(R.string.language_system),
                    selected = selected == AppLanguage.SYSTEM,
                    onClick = {
                        open = false
                        setAppLanguage(context, AppLanguage.SYSTEM)
                    },
                )
                LanguageChoice(
                    title = stringResource(R.string.language_english),
                    selected = selected == AppLanguage.ENGLISH,
                    onClick = {
                        open = false
                        setAppLanguage(context, AppLanguage.ENGLISH)
                    },
                )
                LanguageChoice(
                    title = stringResource(R.string.language_turkish),
                    selected = selected == AppLanguage.TURKISH,
                    onClick = {
                        open = false
                        setAppLanguage(context, AppLanguage.TURKISH)
                    },
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { open = false }) {
                Text(stringResource(R.string.close))
            }
        },
    )
}

@Composable
private fun LanguageChoice(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(title, fontWeight = FontWeight.Medium) },
        leadingContent = {
            RadioButton(
                selected = selected,
                onClick = null,
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    )
}
