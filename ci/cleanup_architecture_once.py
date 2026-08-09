from __future__ import annotations

import hashlib
import html
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8")


def write(path: str, text: str) -> None:
    target = ROOT / path
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text(text, encoding="utf-8")


def replace_once(path: str, old: str, new: str) -> None:
    text = read(path)
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{path}: expected one match, found {count}: {old[:120]!r}")
    write(path, text.replace(old, new, 1))


def replace_all_checked(path: str, old: str, new: str, minimum: int = 1) -> None:
    text = read(path)
    count = text.count(old)
    if count < minimum:
        raise RuntimeError(f"{path}: expected at least {minimum} matches, found {count}: {old[:120]!r}")
    write(path, text.replace(old, new))


def kotlin_unescape(value: str) -> str:
    out: list[str] = []
    i = 0
    while i < len(value):
        ch = value[i]
        if ch != "\\":
            out.append(ch)
            i += 1
            continue
        if i + 1 >= len(value):
            out.append("\\")
            break
        nxt = value[i + 1]
        table = {"n": "\n", "r": "\r", "t": "\t", "b": "\b", "f": "\f", '"': '"', "'": "'", "\\": "\\", "$": "$"}
        if nxt == "u" and i + 5 < len(value):
            raw = value[i + 2 : i + 6]
            if re.fullmatch(r"[0-9A-Fa-f]{4}", raw):
                out.append(chr(int(raw, 16)))
                i += 6
                continue
        out.append(table.get(nxt, nxt))
        i += 2
    return "".join(out)


def kotlin_quote(value: str) -> str:
    return (
        '"'
        + value.replace("\\", "\\\\")
        .replace('"', '\\"')
        .replace("$", "\\$")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
        + '"'
    )


def android_xml_text(value: str) -> str:
    value = value.replace("\\", "\\\\").replace("'", "\\'")
    value = html.escape(value, quote=False)
    if value.startswith("@") or value.startswith("?"):
        value = "\\" + value
    return value


# ---------------------------------------------------------------------------
# 1. Make Settings have exactly one owner and one route hierarchy.
# ---------------------------------------------------------------------------
write(
    "app/src/main/java/app/xylune/chat/ui/SettingsRoute.kt",
    '''package app.xylune.chat.ui

import app.xylune.chat.R

enum class SettingsRoute(
    val titleRes: Int,
) {
    HOME(R.string.settings_title),
    DEFAULTS(R.string.settings_new_chat_defaults),
    RESPONSE_STYLE(R.string.settings_response_style),
    SEARCH(R.string.settings_search_web),
    AUTOMATION(R.string.settings_background_tasks),
    MEMORY(R.string.settings_memory),
    LANGUAGE(R.string.language_dialog_title),
    APPEARANCE(R.string.settings_appearance),
    PRIVACY(R.string.settings_privacy_safety),
    BACKUP(R.string.settings_backup_transfer),
    LOCAL_EXECUTION(R.string.settings_local_execution),
    DEVELOPER(R.string.settings_developer),
    SYSTEM_PROMPTS(R.string.settings_custom_instructions),
    PROVIDERS(R.string.settings_providers_models),
    ABOUT(R.string.settings_about_xylune),
    LICENSES(R.string.settings_licenses_notices),
    ;
}
''',
)

settings = "app/src/main/java/app/xylune/chat/ui/SettingsScreen.kt"
replace_once(settings, "import androidx.compose.material.icons.outlined.Palette\n", "import androidx.compose.material.icons.outlined.Palette\nimport androidx.compose.material.icons.outlined.Language\n")
replace_once(settings, "import app.xylune.chat.settings.ThemeMode\n", "import app.xylune.chat.settings.ThemeMode\nimport app.xylune.chat.settings.AppLanguage\nimport app.xylune.chat.settings.currentAppLanguage\nimport app.xylune.chat.settings.setAppLanguage\n")
replace_once(settings, "                    title = currentRoute.title,\n", "                    title = stringResource(currentRoute.titleRes),\n")
replace_once(
    settings,
    "                        SettingsRoute.MEMORY -> MemorySettingsPage(automation, memories, viewModel)\n                        SettingsRoute.APPEARANCE -> AppearanceSettingsPage(\n",
    "                        SettingsRoute.MEMORY -> MemorySettingsPage(automation, memories, viewModel)\n                        SettingsRoute.LANGUAGE -> LanguageSettingsPage()\n                        SettingsRoute.APPEARANCE -> AppearanceSettingsPage(\n",
)
replace_once(
    settings,
    ") = SettingsPage {\n    if (setupDeferred) {\n",
    ") = SettingsPage {\n    val context = LocalContext.current\n    val selectedLanguageLabel = when (currentAppLanguage(context)) {\n        AppLanguage.SYSTEM -> stringResource(R.string.language_system)\n        AppLanguage.ENGLISH -> stringResource(R.string.language_english)\n        AppLanguage.TURKISH -> stringResource(R.string.language_turkish)\n    }\n    if (setupDeferred) {\n",
)
replace_once(
    settings,
    '''    SettingsGroup("Personalization") {
        SettingsDestination(
            icon = Icons.Outlined.Palette,
''',
    '''    SettingsGroup("Personalization") {
        SettingsDestination(
            icon = Icons.Outlined.Language,
            title = stringResource(R.string.language_dialog_title),
            subtitle = selectedLanguageLabel,
            onClick = { onOpen(SettingsRoute.LANGUAGE) },
        )
        SettingsDestination(
            icon = Icons.Outlined.Palette,
''',
)
language_page = '''
@Composable
private fun LanguageSettingsPage() = SettingsPage {
    val context = LocalContext.current
    val selected = currentAppLanguage(context)
    SectionTitle(
        stringResource(R.string.language_dialog_title),
        stringResource(R.string.language_description),
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
        leadingContent = { RadioButton(selected = selected, onClick = null) },
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = androidx.compose.material3.ListItemDefaults.colors(containerColor = Color.Transparent),
    )
}

'''
replace_once(settings, "@Composable\nprivate fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {\n", language_page + "@Composable\nprivate fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {\n")

host = ROOT / "app/src/main/java/app/xylune/chat/ui/SettingsHostScreen.kt"
if not host.is_file():
    raise RuntimeError("SettingsHostScreen.kt missing before cleanup")
host.unlink()


# ---------------------------------------------------------------------------
# 2. Replace inferred/timed provider-catalog readiness with explicit ownership.
# ---------------------------------------------------------------------------
application = "app/src/main/java/app/xylune/chat/XyluneApplication.kt"
replace_once(application, "import kotlinx.coroutines.flow.first\n", "import kotlinx.coroutines.flow.first\nimport kotlinx.coroutines.flow.MutableStateFlow\nimport kotlinx.coroutines.flow.StateFlow\nimport kotlinx.coroutines.flow.asStateFlow\n")
replace_once(
    application,
    "class XyluneApplication : Application() {\n",
    "enum class CatalogInitializationState { LOADING, READY, FAILED }\n\nclass XyluneApplication : Application() {\n",
)
replace_once(
    application,
    "        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {\n            // Generated/returned assistant images",
    "        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {\n            try {\n            // Generated/returned assistant images",
)
replace_once(
    application,
    '''            container.database.automationSettingsDao().upsert(
                container.database.automationSettingsDao().get() ?: app.xylune.chat.data.AutomationSettingsEntity(),
            )
        }
    }
''',
    '''            container.database.automationSettingsDao().upsert(
                container.database.automationSettingsDao().get() ?: app.xylune.chat.data.AutomationSettingsEntity(),
            )
            container.markCatalogReady()
            } catch (error: Throwable) {
                container.markCatalogFailed()
            }
        }
    }
''',
)
replace_once(
    application,
    "class AppContainer(val application: Application, val crashReporter: CrashReporter) {\n    val appPreferences = AppPreferences(application)\n",
    "class AppContainer(val application: Application, val crashReporter: CrashReporter) {\n    private val _catalogInitializationState = MutableStateFlow(CatalogInitializationState.LOADING)\n    val catalogInitializationState: StateFlow<CatalogInitializationState> = _catalogInitializationState.asStateFlow()\n\n    internal fun markCatalogReady() { _catalogInitializationState.value = CatalogInitializationState.READY }\n    internal fun markCatalogFailed() { _catalogInitializationState.value = CatalogInitializationState.FAILED }\n\n    val appPreferences = AppPreferences(application)\n",
)

viewmodel = "app/src/main/java/app/xylune/chat/ui/ChatViewModel.kt"
replace_once(
    viewmodel,
    '''    val providers = container.repository.observeProviders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    private val _providerCatalogReady = MutableStateFlow(false)
    val providerCatalogReady: StateFlow<Boolean> = _providerCatalogReady
    val providerSetupRequested = savedStateHandle.getMutableStateFlow("provider_setup_requested", false)

    init {
        viewModelScope.launch {
            container.repository.observeProviders().first { it.isNotEmpty() }
            _providerCatalogReady.value = true
        }
    }
''',
    '''    val providers = container.repository.observeProviders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val catalogInitializationState = container.catalogInitializationState
    val providerSetupRequested = savedStateHandle.getMutableStateFlow("provider_setup_requested", false)
''',
)

app = "app/src/main/java/app/xylune/chat/ui/XyluneApp.kt"
replace_once(app, "import app.xylune.chat.settings.DeveloperSettings\n", "import app.xylune.chat.CatalogInitializationState\nimport app.xylune.chat.settings.DeveloperSettings\n")
replace_once(app, "import kotlinx.coroutines.delay\n", "")
replace_once(app, "import androidx.compose.runtime.saveable.rememberSaveable\n", "")
replace_once(app, "import androidx.compose.foundation.layout.fillMaxHeight\n", "")
replace_once(app, "    val providerCatalogReady by viewModel.providerCatalogReady.collectAsState()\n", "    val catalogInitializationState by viewModel.catalogInitializationState.collectAsState()\n")
replace_once(app, "    var providerCatalogGraceExpired by rememberSaveable { mutableStateOf(false) }\n", "")
replace_once(
    app,
    '''    LaunchedEffect(providerCatalogReady) {
        if (providerCatalogReady) {
            providerCatalogGraceExpired = false
        } else {
            delay(8_000)
            providerCatalogGraceExpired = true
        }
    }
    if (shouldBlockForProviderCatalog(providerCatalogReady, providerCatalogGraceExpired)) {
        XyluneStartupScreen()
        return
    }
    val onboardingCatalogUsable = providerCatalogReady || providerCatalogGraceExpired
''',
    '''    if (shouldBlockForProviderCatalog(catalogInitializationState)) {
        XyluneStartupScreen()
        return
    }
    val onboardingCatalogUsable = catalogInitializationState != CatalogInitializationState.LOADING
    val providerCatalogUnavailable = catalogInitializationState == CatalogInitializationState.FAILED
''',
)
replace_once(app, "                providerCatalogDelayed = !providerCatalogReady,\n", "                providerCatalogUnavailable = providerCatalogUnavailable,\n")
replace_once(
    app,
    '''                    Screen.SETTINGS -> Box(Modifier.fillMaxSize()) {
                        SettingsHostScreen(viewModel, compactOpenDrawer)
                        SettingsLeftBackEdgeGuard()
                    }
''',
    "                    Screen.SETTINGS -> SettingsScreen(viewModel, compactOpenDrawer)\n",
)
settings_guard = '''
@Composable
private fun SettingsLeftBackEdgeGuard() {
    // The drawer can still be pulled from the Settings content, but the first
    // 48 dp are owned by Android Back. This node only registers geometry; it
    // consumes no pointer input and therefore cannot block taps or scrolling.
    Box(
        Modifier
            .fillMaxHeight()
            .width(48.dp)
            .horizontalGesturePriority(),
    )
}

'''
replace_once(app, settings_guard, "")

onboarding = "app/src/main/java/app/xylune/chat/ui/OnboardingScreen.kt"
replace_once(onboarding, "import androidx.compose.ui.unit.dp\n", "import androidx.compose.ui.unit.dp\nimport androidx.compose.ui.res.stringResource\nimport app.xylune.chat.CatalogInitializationState\nimport app.xylune.chat.R\n")
replace_once(
    onboarding,
    "internal fun shouldBlockForProviderCatalog(catalogReady: Boolean, graceExpired: Boolean): Boolean =\n    !catalogReady && !graceExpired\n",
    "internal fun shouldBlockForProviderCatalog(state: CatalogInitializationState): Boolean =\n    state == CatalogInitializationState.LOADING\n",
)
replace_all_checked(onboarding, "providerCatalogDelayed", "providerCatalogUnavailable")
replace_once(
    onboarding,
    'Text("The built-in provider catalog is delayed. Setup remains usable and Xylune will keep retrying in the background.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)',
    'Text(stringResource(R.string.provider_catalog_initialization_failed), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)',
)


# ---------------------------------------------------------------------------
# 3. Make the drawer itself respect Android Back edges; remove invisible guards.
# ---------------------------------------------------------------------------
drawer = "app/src/main/java/app/xylune/chat/ui/InteractiveNavigationDrawer.kt"
replace_once(drawer, "import androidx.compose.foundation.layout.fillMaxSize\n", "import androidx.compose.foundation.layout.fillMaxSize\nimport androidx.compose.foundation.layout.WindowInsets\nimport androidx.compose.foundation.layout.systemGestures\n")
replace_once(drawer, "import androidx.compose.ui.platform.LocalFocusManager\n", "import androidx.compose.ui.platform.LocalFocusManager\nimport androidx.compose.ui.platform.LocalLayoutDirection\n")
replace_once(
    drawer,
    "/** One physical drawer offset shared by touch, fling, buttons, scrim and Back. */\n",
    '''internal fun shouldIgnoreClosedDrawerDown(
    x: Float,
    width: Float,
    leftBackEdgePx: Int,
    rightBackEdgePx: Int,
): Boolean = x <= leftBackEdgePx.coerceAtLeast(0) ||
    x >= width - rightBackEdgePx.coerceAtLeast(0)

/** One physical drawer offset shared by touch, fling, buttons, scrim and Back. */
''',
)
replace_once(drawer, "    val focusManager = LocalFocusManager.current\n", "    val focusManager = LocalFocusManager.current\n    val layoutDirection = LocalLayoutDirection.current\n    val leftBackEdgePx = WindowInsets.systemGestures.getLeft(density, layoutDirection)\n    val rightBackEdgePx = WindowInsets.systemGestures.getRight(density, layoutDirection)\n")
replace_once(
    drawer,
    '''                velocityThresholdPx,
                drawerOriginInRoot,
            ) {
''',
    '''                velocityThresholdPx,
                drawerOriginInRoot,
                leftBackEdgePx,
                rightBackEdgePx,
            ) {
''',
)
replace_once(
    drawer,
    '''                    val down = awaitFirstDown(requireUnconsumed = false)
                    val startOffset = state.offsetPx
                    if (
                        startOffset <= .5f &&
                        horizontalPriority.owns(down.position + drawerOriginInRoot)
                    ) {
                        return@awaitEachGesture
                    }
''',
    '''                    val down = awaitFirstDown(requireUnconsumed = false)
                    val startOffset = state.offsetPx
                    if (startOffset <= .5f) {
                        if (shouldIgnoreClosedDrawerDown(
                                x = down.position.x,
                                width = size.width.toFloat(),
                                leftBackEdgePx = leftBackEdgePx,
                                rightBackEdgePx = rightBackEdgePx,
                            ) || horizontalPriority.owns(down.position + drawerOriginInRoot)
                        ) {
                            return@awaitEachGesture
                        }
                    }
''',
)


# ---------------------------------------------------------------------------
# 4. Replace magic UI timers with actual transition/draw completion signals.
# ---------------------------------------------------------------------------
link = "app/src/main/java/app/xylune/chat/ui/LinkPreview.kt"
replace_once(link, "import kotlinx.coroutines.delay\n", "")
replace_once(
    link,
    '''    LaunchedEffect(dismissRequested) {
        if (dismissRequested) {
            visibility.targetState = false
            delay(170)
            onDismiss()
        }
    }
''',
    '''    LaunchedEffect(dismissRequested) {
        if (dismissRequested) visibility.targetState = false
    }
    LaunchedEffect(dismissRequested, visibility.isIdle, visibility.currentState) {
        if (dismissRequested && visibility.isIdle && !visibility.currentState) onDismiss()
    }
''',
)

launcher = "app/src/main/java/app/xylune/chat/LauncherActivity.kt"
replace_once(launcher, "import androidx.core.view.WindowCompat\n", "import androidx.core.view.WindowCompat\nimport androidx.core.view.doOnPreDraw\n")
replace_once(launcher, "        root.postDelayed({\n", "        root.doOnPreDraw {\n")
replace_once(launcher, "        }, 110L)\n", "        }\n")


# ---------------------------------------------------------------------------
# 5. Move all exact Turkish UI copy into Android resources.
#    Dynamic/interpolated compatibility rules remain as a narrow fallback.
# ---------------------------------------------------------------------------
copy_files = [
    "app/src/main/java/app/xylune/chat/ui/TurkishUiCopy.kt",
    "app/src/main/java/app/xylune/chat/ui/TurkishUiCopyExtra2.kt",
    "app/src/main/java/app/xylune/chat/ui/TurkishUiCopyExtra.kt",
    "app/src/main/java/app/xylune/chat/ui/TurkishUiCopyExtra3.kt",
]
entry_pattern = re.compile(r'^\s*"((?:\\.|[^"\\])*)"\s*->\s*"((?:\\.|[^"\\])*)"', re.MULTILINE)
entries: dict[str, str] = {}
raw_keys: dict[str, str] = {}
for path in copy_files:
    source = read(path)
    for match in entry_pattern.finditer(source):
        en_raw, tr_raw = match.groups()
        en = kotlin_unescape(en_raw)
        tr = kotlin_unescape(tr_raw)
        if en not in entries:
            entries[en] = tr
            raw_keys[en] = en_raw
if len(entries) < 300:
    raise RuntimeError(f"Expected a substantial Turkish exact-copy catalog, found only {len(entries)} entries")

used_names: set[str] = set()
resource_names: dict[str, str] = {}
for en in entries:
    slug = re.sub(r"[^a-z0-9]+", "_", en.lower()).strip("_")[:42] or "text"
    digest = hashlib.sha1(en.encode("utf-8")).hexdigest()[:8]
    name = f"ui_copy_{slug}_{digest}"
    if name in used_names:
        raise RuntimeError(f"Resource name collision: {name}")
    used_names.add(name)
    resource_names[en] = name

route_resources = {
    "settings_title": ("Settings", "Ayarlar"),
    "settings_new_chat_defaults": ("New chat defaults", "Yeni sohbet varsayılanları"),
    "settings_response_style": ("Response style", "Yanıt stili"),
    "settings_search_web": ("Search & web", "Arama ve web"),
    "settings_background_tasks": ("Background tasks", "Arka plan görevleri"),
    "settings_memory": ("Memory", "Hafıza"),
    "settings_appearance": ("Appearance", "Görünüm"),
    "settings_privacy_safety": ("Privacy & safety", "Gizlilik ve güvenlik"),
    "settings_backup_transfer": ("Backup & transfer", "Yedekleme ve aktarım"),
    "settings_local_execution": ("Local execution", "Yerel çalıştırma"),
    "settings_developer": ("Developer settings", "Geliştirici ayarları"),
    "settings_custom_instructions": ("Custom instructions", "Özel talimatlar"),
    "settings_providers_models": ("Providers & models", "Sağlayıcılar ve modeller"),
    "settings_about_xylune": ("About Xylune", "Xylune hakkında"),
    "settings_licenses_notices": ("Licenses & notices", "Lisanslar ve bildirimler"),
    "provider_catalog_initialization_failed": (
        "The built-in provider catalog could not be initialized. Setup remains usable; you can add a custom provider manually.",
        "Yerleşik sağlayıcı kataloğu başlatılamadı. Kurulum kullanılabilir durumda; özel bir sağlayıcıyı elle ekleyebilirsiniz.",
    ),
}


def insert_resources(path: str, values: dict[str, str], generated: list[tuple[str, str]]) -> None:
    text = read(path)
    if "<!-- generated-ui-copy:start -->" in text:
        raise RuntimeError(f"{path}: generated UI copy section already exists")
    lines = []
    for name, value in values.items():
        if f'name="{name}"' in text:
            continue
        lines.append(f'    <string name="{name}">{android_xml_text(value)}</string>')
    if lines:
        text = text.replace("</resources>", "\n" + "\n".join(lines) + "\n</resources>")
    generated_lines = ["    <!-- generated-ui-copy:start -->"]
    generated_lines.extend(
        f'    <string name="{name}" formatted="false">{android_xml_text(value)}</string>'
        for name, value in generated
    )
    generated_lines.append("    <!-- generated-ui-copy:end -->")
    text = text.replace("</resources>", "\n" + "\n".join(generated_lines) + "\n</resources>")
    write(path, text)

insert_resources(
    "app/src/main/res/values/strings.xml",
    {name: values[0] for name, values in route_resources.items()},
    [(resource_names[en], en) for en in sorted(entries)],
)
insert_resources(
    "app/src/main/res/values-tr/strings.xml",
    {name: values[1] for name, values in route_resources.items()},
    [(resource_names[en], entries[en]) for en in sorted(entries)],
)

mapping_lines = [
    "package app.xylune.chat.ui",
    "",
    "import app.xylune.chat.R",
    "",
    "/** Resource bridge for legacy String call sites. Static translations live in Android resources. */",
    "internal fun xyluneUiStringResource(text: String): Int? = when (text) {",
]
for en in sorted(entries):
    mapping_lines.append(f"    {kotlin_quote(en)} -> R.string.{resource_names[en]}")
mapping_lines += ["    else -> null", "}", ""]
write("app/src/main/java/app/xylune/chat/ui/UiStringResources.kt", "\n".join(mapping_lines))

localized = "app/src/main/java/app/xylune/chat/ui/LocalizedText.kt"
replace_once(localized, "import androidx.compose.ui.platform.LocalConfiguration\n", "import androidx.compose.ui.platform.LocalConfiguration\nimport androidx.compose.ui.res.stringResource\n")
replace_once(
    localized,
    '''    val localized = if (language == "tr") {
        val primary = TurkishUiCopy.translate(text)
        if (primary != text) {
            primary
        } else {
            val secondary = TurkishUiCopyExtra2.translate(text)
            if (secondary != text) {
                secondary
            } else {
                val tertiary = TurkishUiCopyExtra.translate(text)
                if (tertiary != text) tertiary else TurkishUiCopyExtra3.translate(text)
            }
        }
    } else {
        text
    }
''',
    '''    val staticResource = xyluneUiStringResource(text)
    val localized = if (staticResource != null) {
        stringResource(staticResource)
    } else if (language == "tr") {
        // Only dynamic/interpolated compatibility rules reach this fallback.
        val primary = TurkishUiCopy.translate(text)
        if (primary != text) {
            primary
        } else {
            val secondary = TurkishUiCopyExtra2.translate(text)
            if (secondary != text) {
                secondary
            } else {
                val tertiary = TurkishUiCopyExtra.translate(text)
                if (tertiary != text) tertiary else TurkishUiCopyExtra3.translate(text)
            }
        }
    } else {
        text
    }
''',
)


# ---------------------------------------------------------------------------
# 6. Replace tests which explicitly protected the hacks with behavior/invariants.
# ---------------------------------------------------------------------------
write(
    "app/src/test/java/app/xylune/chat/ui/AppLanguageLocalizationTest.kt",
    '''package app.xylune.chat.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class AppLanguageLocalizationTest {
    private fun repositoryFile(path: String): File = sequenceOf(File(path), File("..", path))
        .firstOrNull(File::isFile)
        ?: error("Could not locate repository file: $path")

    @Test
    fun `Turkish locale is advertised and covers every Android string resource`() {
        val base = repositoryFile("app/src/main/res/values/strings.xml").readText()
        val turkish = repositoryFile("app/src/main/res/values-tr/strings.xml").readText()
        val localeConfig = repositoryFile("app/src/main/res/xml/locales_config.xml").readText()

        fun names(xml: String): Set<String> = Regex("""<string\\s+name=\"([^\"]+)\"""")
            .findAll(xml)
            .map { it.groupValues[1] }
            .toSet()

        assertEquals(names(base), names(turkish))
        assertTrue(localeConfig.contains("android:name=\\\"tr\\\""))
        assertTrue(localeConfig.contains("android:name=\\\"tr-TR\\\""))
        assertTrue(turkish.contains(">Ayarlar<"))
        assertTrue(turkish.contains(">Uygulama dili<"))
    }

    @Test
    fun `language is a real settings route without a delayed overlay host`() {
        val settings = repositoryFile("app/src/main/java/app/xylune/chat/ui/SettingsScreen.kt").readText()
        val routes = repositoryFile("app/src/main/java/app/xylune/chat/ui/SettingsRoute.kt").readText()
        val host = sequenceOf(
            File("app/src/main/java/app/xylune/chat/ui/SettingsHostScreen.kt"),
            File("..", "app/src/main/java/app/xylune/chat/ui/SettingsHostScreen.kt"),
        ).firstOrNull(File::isFile)

        assertTrue(routes.contains("LANGUAGE(R.string.language_dialog_title)"))
        assertTrue(settings.contains("SettingsRoute.LANGUAGE -> LanguageSettingsPage()"))
        assertTrue(settings.contains("onOpen(SettingsRoute.LANGUAGE)"))
        assertTrue(settings.contains("title = stringResource(currentRoute.titleRes)"))
        assertFalse(settings.contains("delay(300)"))
        assertFalse(settings.contains("delay(90)"))
        assertTrue(host == null)
    }

    @Test
    fun `static localized copy resolves through android resources before compatibility formatters`() {
        val localized = repositoryFile("app/src/main/java/app/xylune/chat/ui/LocalizedText.kt").readText()
        val mapping = repositoryFile("app/src/main/java/app/xylune/chat/ui/UiStringResources.kt").readText()
        assertTrue(localized.contains("xyluneUiStringResource(text)"))
        assertTrue(localized.contains("stringResource(staticResource)"))
        assertTrue(mapping.contains("R.string.ui_copy_"))
        assertTrue(mapping.contains("\\\"Settings\\\" -> R.string."))
    }
}
''',
)

nav_test = "app/src/test/java/app/xylune/chat/ui/NavigationBackTest.kt"
replace_once(
    nav_test,
    '''    @Test fun settingsReservesOnlyTheRealLeftBackEdge() {
        val root = java.io.File("src/main/java/app/xylune/chat/ui/XyluneApp.kt").readText()
        assertTrue(root.contains("SettingsLeftBackEdgeGuard"))
        assertTrue(root.contains(".width(48.dp)"))
        assertTrue(root.contains(".horizontalGesturePriority()"))
    }
''',
    '''    @Test fun closedDrawerLeavesAndroidBackEdgesUnclaimed() {
        assertTrue(shouldIgnoreClosedDrawerDown(x = 0f, width = 1080f, leftBackEdgePx = 44, rightBackEdgePx = 52))
        assertTrue(shouldIgnoreClosedDrawerDown(x = 1070f, width = 1080f, leftBackEdgePx = 44, rightBackEdgePx = 52))
        assertFalse(shouldIgnoreClosedDrawerDown(x = 100f, width = 1080f, leftBackEdgePx = 44, rightBackEdgePx = 52))
    }
''',
)

flow_test = "app/src/test/java/app/xylune/chat/ui/OnboardingFlowTest.kt"
replace_once(flow_test, "import org.junit.Test\n", "import org.junit.Test\nimport app.xylune.chat.CatalogInitializationState\n")
replace_once(
    flow_test,
    '''    fun `startup wait has a bounded escape path`() {
        assertTrue(shouldBlockForProviderCatalog(catalogReady = false, graceExpired = false))
        assertFalse(shouldBlockForProviderCatalog(catalogReady = false, graceExpired = true))
        assertFalse(shouldBlockForProviderCatalog(catalogReady = true, graceExpired = false))
    }
''',
    '''    fun `startup wait follows explicit catalog initialization state`() {
        assertTrue(shouldBlockForProviderCatalog(CatalogInitializationState.LOADING))
        assertFalse(shouldBlockForProviderCatalog(CatalogInitializationState.READY))
        assertFalse(shouldBlockForProviderCatalog(CatalogInitializationState.FAILED))
    }
''',
)

write(
    "app/src/test/java/app/xylune/chat/ui/ArchitectureTimingRegressionTest.kt",
    '''package app.xylune.chat.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ArchitectureTimingRegressionTest {
    private fun source(name: String): String = File("src/main/java/app/xylune/chat/$name").readText()

    @Test
    fun `ui navigation and popup lifecycles do not depend on arbitrary sleeps`() {
        assertFalse(source("ui/XyluneApp.kt").contains("delay(8_000"))
        assertFalse(source("ui/LinkPreview.kt").contains("delay(170"))
        assertFalse(source("LauncherActivity.kt").contains("postDelayed"))
        assertTrue(source("ui/LinkPreview.kt").contains("visibility.isIdle"))
        assertTrue(source("LauncherActivity.kt").contains("doOnPreDraw"))
    }

    @Test
    fun `settings has one navigation owner`() {
        assertFalse(File("src/main/java/app/xylune/chat/ui/SettingsHostScreen.kt").exists())
        val settings = source("ui/SettingsScreen.kt")
        assertTrue(settings.contains("SettingsRoute.LANGUAGE"))
        assertFalse(settings.contains("delay(300)"))
        assertFalse(settings.contains("delay(90)"))
    }
}
''',
)


# ---------------------------------------------------------------------------
# 7. Prepare version 0.24.26 and bilingual release notes.
# ---------------------------------------------------------------------------
gradle = "app/build.gradle.kts"
replace_once(gradle, "        versionCode = 214\n        versionName = \"0.24.25\"\n", "        versionCode = 215\n        versionName = \"0.24.26\"\n")

write(
    "docs/releases/RELEASE_NOTES_0.24.26.md",
    '''# Xylune 0.24.26

## Structural UI cleanup

- Removes the duplicated Settings home and the 300 ms / 90 ms timing workaround; App language is now a normal Settings route owned by the existing navigation host.
- Replaces the provider-catalog 8-second escape timer with an explicit loading/ready/failed initialization state owned by application startup.
- Makes the navigation drawer respect Android's actual system Back gesture insets instead of reserving an invisible fixed-width Settings strip.
- Closes link previews when their exit transition actually finishes instead of sleeping for a hard-coded duration.
- Replaces the launcher's fixed 110 ms handoff with a first-draw lifecycle callback while keeping the separate One UI launcher-alias recovery path intact.

## Localization and regression coverage

- Moves exact Turkish interface copy into Android locale resources; the compatibility formatter is now used only for dynamic/interpolated text.
- Localizes Settings page titles through resource IDs, including App language.
- Replaces regression tests that explicitly required the old timing hacks with behavior and architectural-invariant coverage.
''',
)
write(
    "docs/releases/tr/RELEASE_NOTES_0.24.26.md",
    '''# Xylune 0.24.26

## Yapısal arayüz temizliği

- Yinelenen Ayarlar ana sayfasını ve 300 ms / 90 ms zamanlama geçici çözümünü kaldırır; Uygulama dili artık mevcut gezinme yapısının yönettiği normal bir Ayarlar rotasıdır.
- Sağlayıcı kataloğundaki 8 saniyelik kaçış zamanlayıcısını, uygulama başlangıcının sahip olduğu açık yükleniyor/hazır/başarısız durumuyla değiştirir.
- Gezinme çekmecesinin görünmez sabit genişlikli bir Ayarlar şeridi ayırmak yerine Android'in gerçek sistem Geri hareketi alanlarına uymasını sağlar.
- Bağlantı önizlemelerini sabit süre beklemek yerine çıkış geçişi gerçekten tamamlandığında kapatır.
- One UI başlatıcı diğer adı kurtarma yolunu korurken başlatıcının sabit 110 ms aktarımını ilk çizim yaşam döngüsü geri çağrısıyla değiştirir.

## Yerelleştirme ve regresyon kapsamı

- Sabit Türkçe arayüz metinlerini Android yerel ayar kaynaklarına taşır; uyumluluk biçimlendiricisi artık yalnızca dinamik/değişken metinler için kullanılır.
- Uygulama dili dahil Ayarlar sayfa başlıklarını kaynak kimlikleri üzerinden yerelleştirir.
- Eski zamanlama geçici çözümlerini açıkça zorunlu tutan regresyon testlerini davranış ve mimari değişmez kapsamıyla değiştirir.
''',
)
write(
    "app/src/test/java/app/xylune/chat/ReleaseVersionRegressionTest.kt",
    '''package app.xylune.chat

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ReleaseVersionRegressionTest {
    private fun repositoryFile(path: String): File = sequenceOf(File(path), File("..", path))
        .firstOrNull(File::isFile)
        ?: error("Could not locate repository file: $path")

    @Test
    fun `release metadata is scoped to 0_24_26`() {
        val gradle = repositoryFile("app/build.gradle.kts").readText()
        val english = repositoryFile("docs/releases/RELEASE_NOTES_0.24.26.md").readText()
        val turkish = repositoryFile("docs/releases/tr/RELEASE_NOTES_0.24.26.md").readText()

        assertTrue(gradle.contains("versionCode = 215"))
        assertTrue(gradle.contains("versionName = \\\"0.24.26\\\""))
        assertTrue(english.startsWith("# Xylune 0.24.26"))
        assertTrue(turkish.startsWith("# Xylune 0.24.26"))
    }
}
''',
)

# One-shot script must never survive the patch commit.
Path(__file__).unlink()

print(f"Architecture cleanup prepared with {len(entries)} resource-backed exact UI strings.")
