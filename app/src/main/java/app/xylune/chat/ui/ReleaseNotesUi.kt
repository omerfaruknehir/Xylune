package app.xylune.chat.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.xylune.chat.update.RepositoryRelease

@Composable
internal fun UpdateAvailableDialog(
    release: RepositoryRelease,
    onDismiss: () -> Unit,
    onOpenUpdate: () -> Unit,
) {
    val strings = releaseNotesStrings()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.updateAvailable(release.versionName)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 460.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    strings.whatsNew,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                ReleaseNotesContent(release.notes, strings.noReleaseNotes)
                release.compatibilityMessage?.takeIf(String::isNotBlank)?.let { message ->
                    Text(
                        message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onOpenUpdate) {
                Text(if (release.directInstallCompatible) strings.download else strings.openRelease)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.later) }
        },
    )
}

@Composable
internal fun InstalledWhatsNewDialog(
    release: RepositoryRelease,
    onDismiss: () -> Unit,
    onOpenRelease: () -> Unit,
) {
    val strings = releaseNotesStrings()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.whatsNewIn(release.versionName)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 460.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ReleaseNotesContent(release.notes, strings.noReleaseNotes)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(strings.done) }
        },
        dismissButton = {
            TextButton(onClick = onOpenRelease) { Text(strings.openRelease) }
        },
    )
}

@Composable
internal fun InstalledWhatsNewUnavailableDialog(
    versionName: String,
    message: String,
    onDismissForNow: () -> Unit,
    onRetry: () -> Unit,
    onOpenRelease: () -> Unit,
) {
    val strings = releaseNotesStrings()
    AlertDialog(
        onDismissRequest = onDismissForNow,
        title = { Text(strings.whatsNewIn(versionName)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(strings.couldNotLoad)
                Text(
                    message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onRetry) { Text(strings.retry) }
        },
        dismissButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onOpenRelease) { Text(strings.openRelease) }
                TextButton(onClick = onDismissForNow) { Text(strings.later) }
            }
        },
    )
}

@Composable
private fun ReleaseNotesContent(markdown: String, emptyText: String) {
    val blocks = parseReleaseNotes(markdown)
    if (blocks.isEmpty()) {
        Text(emptyText, color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        blocks.forEach { block ->
            when (block) {
                is ReleaseNotesBlock.Heading -> Text(
                    block.text,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp),
                )
                is ReleaseNotesBlock.Bullet -> Text("• ${block.text}")
                is ReleaseNotesBlock.Paragraph -> Text(block.text)
            }
        }
    }
}

internal sealed interface ReleaseNotesBlock {
    data class Heading(val text: String) : ReleaseNotesBlock
    data class Bullet(val text: String) : ReleaseNotesBlock
    data class Paragraph(val text: String) : ReleaseNotesBlock
}

internal fun parseReleaseNotes(markdown: String): List<ReleaseNotesBlock> = markdown
    .lineSequence()
    .map(String::trim)
    .filter(String::isNotBlank)
    .mapNotNull { line ->
        when {
            line.startsWith("# Xylune ", ignoreCase = true) -> null
            line.startsWith("### ") -> ReleaseNotesBlock.Heading(cleanReleaseNotesInline(line.removePrefix("### ")))
            line.startsWith("## ") -> ReleaseNotesBlock.Heading(cleanReleaseNotesInline(line.removePrefix("## ")))
            line.startsWith("# ") -> ReleaseNotesBlock.Heading(cleanReleaseNotesInline(line.removePrefix("# ")))
            line.startsWith("- ") -> ReleaseNotesBlock.Bullet(cleanReleaseNotesInline(line.removePrefix("- ")))
            line.startsWith("* ") -> ReleaseNotesBlock.Bullet(cleanReleaseNotesInline(line.removePrefix("* ")))
            else -> ReleaseNotesBlock.Paragraph(cleanReleaseNotesInline(line))
        }
    }
    .filterNot { block ->
        when (block) {
            is ReleaseNotesBlock.Heading -> block.text.isBlank()
            is ReleaseNotesBlock.Bullet -> block.text.isBlank()
            is ReleaseNotesBlock.Paragraph -> block.text.isBlank()
        }
    }
    .toList()

private fun cleanReleaseNotesInline(text: String): String = text
    .replace(Regex("\\*\\*(.+?)\\*\\*"), "$1")
    .replace(Regex("`([^`]+)`"), "$1")
    .replace(Regex("\\[([^]]+)]\\(([^)]+)\\)"), "$1")
    .trim()

private data class ReleaseNotesStrings(
    val whatsNew: String,
    val download: String,
    val openRelease: String,
    val later: String,
    val done: String,
    val retry: String,
    val noReleaseNotes: String,
    val couldNotLoad: String,
    val updateAvailable: (String) -> String,
    val whatsNewIn: (String) -> String,
)

@Composable
private fun releaseNotesStrings(): ReleaseNotesStrings {
    val language = LocalContext.current.resources.configuration.locales[0]?.language
    return if (language.equals("tr", ignoreCase = true)) {
        ReleaseNotesStrings(
            whatsNew = "Neler yeni",
            download = "İndir",
            openRelease = "Sürümü aç",
            later = "Daha sonra",
            done = "Tamam",
            retry = "Yeniden dene",
            noReleaseNotes = "Bu sürüm için sürüm notu bulunamadı.",
            couldNotLoad = "Bu sürümün yenilikleri şu anda yüklenemedi.",
            updateAvailable = { version -> "Xylune $version kullanılabilir" },
            whatsNewIn = { version -> "Xylune $version sürümünde neler yeni" },
        )
    } else {
        ReleaseNotesStrings(
            whatsNew = "What's new",
            download = "Download",
            openRelease = "Open release",
            later = "Later",
            done = "Done",
            retry = "Retry",
            noReleaseNotes = "No release notes were provided for this version.",
            couldNotLoad = "What's new for this version could not be loaded right now.",
            updateAvailable = { version -> "Xylune $version is available" },
            whatsNewIn = { version -> "What's new in Xylune $version" },
        )
    }
}
