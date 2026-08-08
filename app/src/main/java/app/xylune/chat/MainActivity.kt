package app.xylune.chat

import android.app.PendingIntent
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import app.xylune.chat.settings.ColorPalette
import app.xylune.chat.settings.LauncherIconManager
import app.xylune.chat.settings.localizedAppContext
import app.xylune.chat.transfer.XYLUNE_BACKUP_EXTENSION
import app.xylune.chat.transfer.XYLUNE_BACKUP_MIME
import app.xylune.chat.transfer.XYLUNE_CHAT_EXTENSION
import app.xylune.chat.transfer.XYLUNE_CHAT_MIME
import app.xylune.chat.ui.AppLanguageMenuButton
import app.xylune.chat.ui.ChatViewModel
import app.xylune.chat.ui.LocalXyluneIconPalette
import app.xylune.chat.ui.Screen
import app.xylune.chat.ui.XyluneAlertDialog
import app.xylune.chat.ui.XyluneApp
import app.xylune.chat.ui.theme.XyluneTheme
import app.xylune.chat.ui.theme.resolvedXyluneColorScheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var launcherRestartInFlight = false
    private val viewModel: ChatViewModel by viewModels {
        ChatViewModel.factory((application as XyluneApplication).container)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(localizedAppContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = (application as XyluneApplication).container.appPreferences
        window.setBackgroundDrawable(
            ColorDrawable(
                resolvedXyluneColorScheme(
                    context = this,
                    palette = preferences.palette.value,
                    themeMode = preferences.themeMode.value,
                    amoled = preferences.amoled.value,
                ).background.toArgb(),
            ),
        )
        enableEdgeToEdge()
        handleIntent(intent)
        observeLauncherRestarts()
        setContent {
            val amoled by viewModel.amoled.collectAsState()
            val palette by viewModel.palette.collectAsState()
            val themeMode by viewModel.themeMode.collectAsState()
            val matchLauncherIconToPalette by viewModel.matchLauncherIconToPalette.collectAsState()
            val screen by viewModel.screen.collectAsState()
            XyluneTheme(amoled = amoled, palette = palette, themeMode = themeMode) {
                CompositionLocalProvider(
                    LocalXyluneIconPalette provides if (matchLauncherIconToPalette) palette else ColorPalette.XYLUNE,
                ) {
                    val appName = stringResource(R.string.app_name)
                    Box(Modifier.fillMaxSize()) {
                        XyluneApp(viewModel, this@MainActivity)
                        if (screen == Screen.SETTINGS) {
                            AppLanguageMenuButton(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .statusBarsPadding()
                                    .padding(top = 4.dp, end = 4.dp),
                            )
                        }
                    }
                    val container = (application as XyluneApplication).container
                    var crashReport by remember { mutableStateOf(container.crashReporter.read()) }
                    val renderSafeMode by viewModel.renderSafeMode.collectAsState()
                    crashReport?.let { report ->
                        val context = LocalContext.current
                        XyluneAlertDialog(
                            onDismissRequest = { container.crashReporter.clear(); crashReport = null },
                            title = { Text(stringResource(R.string.crash_recovered_title, appName)) },
                            text = {
                                Column(Modifier.heightIn(max = 420.dp).verticalScroll(rememberScrollState())) {
                                    if (renderSafeMode) {
                                        Text(stringResource(R.string.crash_safe_message, appName) + "\n")
                                        OutlinedButton(onClick = { viewModel.setRenderSafeMode(false) }) {
                                            Text(stringResource(R.string.try_full_rendering_again))
                                        }
                                        Text("\n")
                                    }
                                    Text(stringResource(R.string.crash_copy_instruction) + "\n\n$report")
                                }
                            },
                            dismissButton = {
                                OutlinedButton(onClick = { container.crashReporter.clear(); crashReport = null }) {
                                    Text(stringResource(R.string.dismiss))
                                }
                            },
                            confirmButton = {
                                Button(onClick = {
                                    context.getSystemService(ClipboardManager::class.java)
                                        .setPrimaryClip(
                                            ClipData.newPlainText(
                                                context.getString(R.string.crash_report_clip_label, appName),
                                                report,
                                            ),
                                        )
                                }) {
                                    Text(stringResource(R.string.copy_report))
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (application as XyluneApplication).container.openAiOAuth.onBrowserReturned()
    }

    override fun onStop() {
        if (!isChangingConfigurations) {
            lifecycleScope.launch { viewModel.flushPersistentState() }
        }
        super.onStop()
    }

    private fun observeLauncherRestarts() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.launcherRestartRequests.collect { desiredAlias ->
                    if (launcherRestartInFlight) return@collect
                    launcherRestartInFlight = true
                    viewModel.flushPersistentState()
                    val relaunch = PendingIntent.getActivity(
                        this@MainActivity,
                        20_020,
                        Intent(Intent.ACTION_MAIN).apply {
                            component = ComponentName(packageName, desiredAlias)
                            addCategory(Intent.CATEGORY_LAUNCHER)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        },
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    )
                    if (LauncherIconManager.requestStatefulRestart(
                            context = applicationContext,
                            desiredClassName = desiredAlias,
                            relaunchIntent = relaunch,
                        )
                    ) {
                        finishAffinity()
                        @Suppress("DEPRECATION")
                        overridePendingTransition(0, 0)
                    } else {
                        launcherRestartInFlight = false
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            intent.data?.let {
                if (viewModel.handleCloudOAuthRedirect(it)) return
                viewModel.receivePortableArchive(it)
                return
            }
        }
        val uris: List<Uri> = when (intent.action) {
            Intent.ACTION_SEND -> listOfNotNull(intent.parcelableExtra<Uri>(Intent.EXTRA_STREAM))
            Intent.ACTION_SEND_MULTIPLE -> intent.parcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
            else -> emptyList()
        }
        if (uris.size == 1 && isPortableArchiveIntent(intent, uris.single())) {
            viewModel.receivePortableArchive(uris.single())
            return
        }
        viewModel.receiveIntent(intent.getStringExtra(EXTRA_CONVERSATION_ID), uris)
    }

    private fun isPortableArchiveIntent(intent: Intent, uri: Uri): Boolean {
        if (intent.type == XYLUNE_CHAT_MIME || intent.type == XYLUNE_BACKUP_MIME) return true
        val name = uri.lastPathSegment?.lowercase().orEmpty()
        return name.endsWith(XYLUNE_CHAT_EXTENSION) || name.endsWith(XYLUNE_BACKUP_EXTENSION)
    }

    @Suppress("DEPRECATION")
    private inline fun <reified T : Parcelable> Intent.parcelableExtra(key: String): T? =
        if (Build.VERSION.SDK_INT >= 33) getParcelableExtra(key, T::class.java) else getParcelableExtra(key)

    @Suppress("DEPRECATION")
    private inline fun <reified T : Parcelable> Intent.parcelableArrayListExtra(key: String): List<T> =
        if (Build.VERSION.SDK_INT >= 33) getParcelableArrayListExtra(key, T::class.java).orEmpty() else getParcelableArrayListExtra<T>(key).orEmpty()

    companion object { const val EXTRA_CONVERSATION_ID = "conversation_id" }
}
