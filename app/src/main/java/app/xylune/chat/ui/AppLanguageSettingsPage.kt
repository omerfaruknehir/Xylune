package app.xylune.chat.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text as MaterialText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.xylune.chat.R
import app.xylune.chat.settings.AppLanguage
import app.xylune.chat.settings.currentAppLanguage
import app.xylune.chat.settings.setAppLanguage

@Composable
internal fun AppLanguageSettingsPage() = SettingsPage {
    val context = LocalContext.current
    val selected = currentAppLanguage(context)

    Text(
        text = stringResource(R.string.language_description),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 6.dp),
    )

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            LanguageChoice(
                title = stringResource(R.string.language_system),
                selected = selected == AppLanguage.SYSTEM,
                onClick = { setAppLanguage(context, AppLanguage.SYSTEM) },
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            LanguageChoice(
                title = stringResource(R.string.language_english),
                selected = selected == AppLanguage.ENGLISH,
                onClick = { setAppLanguage(context, AppLanguage.ENGLISH) },
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            LanguageChoice(
                title = stringResource(R.string.language_turkish),
                selected = selected == AppLanguage.TURKISH,
                onClick = { setAppLanguage(context, AppLanguage.TURKISH) },
            )
        }
    }

    Spacer(Modifier.padding(bottom = 24.dp))
}

@Composable
private fun LanguageChoice(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(title, fontWeight = FontWeight.SemiBold) },
        leadingContent = {
            RadioButton(
                selected = selected,
                onClick = null,
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
    )
}
