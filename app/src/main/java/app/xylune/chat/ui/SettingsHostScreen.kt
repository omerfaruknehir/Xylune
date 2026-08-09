package app.xylune.chat.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text as MaterialText
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.xylune.chat.R
import app.xylune.chat.settings.AppLanguage
import app.xylune.chat.settings.currentAppLanguage
import app.xylune.chat.settings.setAppLanguage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first

/**
 * Adds app language to the normal Settings home without replacing the existing
 * SettingsScreen navigation stack. The original SettingsScreen stays composed
 * underneath this home surface so its existing page transitions remain live.
 */
@Composable
internal fun SettingsHostScreen(
    viewModel: ChatViewModel,
    openDrawer: (() -> Unit)?,
) {
    val settingsRoute by viewModel.settingsRoute.collectAsState()
    var languageOpen by rememberSaveable { mutableStateOf(false) }
    var homeSurfaceVisible by rememberSaveable { mutableStateOf(settingsRoute == SettingsRoute.HOME) }

    LaunchedEffect(settingsRoute) {
        if (settingsRoute == SettingsRoute.HOME) {
            // Button-driven Settings Back starts a 280 ms transition after the
            // route changes. Reveal the augmented home after that transition so
            // it never cuts the established animation short.
            delay(300)
            homeSurfaceVisible = true
        } else {
            languageOpen = false
            // Give the already-composed SettingsScreen one frame of its forward
            // transition before uncovering it. This prevents a hard page swap.
            delay(90)
            homeSurfaceVisible = false
        }
    }

    Box(Modifier.fillMaxSize()) {
        SettingsScreen(viewModel, openDrawer)
        if (homeSurfaceVisible) {
            PredictiveNavigationHost(
                targetState = languageOpen,
                backTarget = if (languageOpen) false else null,
                onBack = { languageOpen = it },
                depth = { if (it) 1 else 0 },
                modifier = Modifier.fillMaxSize(),
                label = "SettingsLanguageNavigation",
            ) { showLanguage ->
                if (showLanguage) {
                    LanguageSettingsPage(onBack = { languageOpen = false })
                } else {
                    SettingsHomeWithLanguage(
                        viewModel = viewModel,
                        openDrawer = openDrawer,
                        onOpenLanguage = { languageOpen = true },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsHomeWithLanguage(
    viewModel: ChatViewModel,
    openDrawer: (() -> Unit)?,
    onOpenLanguage: () -> Unit,
) {
    val providers by viewModel.providers.collectAsState()
    val credentialRevision by viewModel.credentialRevision.collectAsState()
    val setupDismissed by viewModel.setupDismissed.collectAsState()
    val setupStepIndex by viewModel.setupStepIndex.collectAsState()
    val chromeBlurStrength by viewModel.chromeBlurStrength.collectAsState()
    val chromeEdgeSoftness by viewModel.chromeEdgeSoftness.collectAsState()
    val chromeOverlayOpacity by viewModel.chromeOverlayOpacity.collectAsState()
    val providerCount = remember(providers, credentialRevision) {
        viewModel.registeredProviders(providers).size
    }
    val setupDeferred = setupDismissed && setupStepIndex < 2
    val context = LocalContext.current
    val selectedLanguage = currentAppLanguage(context)
    val selectedLanguageLabel = when (selectedLanguage) {
        AppLanguage.SYSTEM -> stringResource(R.string.language_system)
        AppLanguage.ENGLISH -> stringResource(R.string.language_english)
        AppLanguage.TURKISH -> stringResource(R.string.language_turkish)
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val blurState = rememberXyluneBackdropBlurState()
    val scrollState = rememberScrollState(initial = viewModel.settingsScrollOffset(SettingsRoute.HOME))

    LaunchedEffect(scrollState, viewModel) {
        snapshotFlow { scrollState.value }
            .distinctUntilChanged()
            .collect { viewModel.saveSettingsScrollOffset(SettingsRoute.HOME, it) }
    }
    LaunchedEffect(scrollState, scrollBehavior.state) {
        val limit = snapshotFlow { scrollBehavior.state.heightOffsetLimit }.first { it < 0f }
        scrollBehavior.state.heightOffset = settingsTopBarHeightOffset(scrollState.value, limit)
        scrollBehavior.state.contentOffset = -scrollState.value.coerceAtLeast(0).toFloat()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            CollapsingTranslucentTopBar(
                title = SettingsRoute.HOME.title,
                scrollBehavior = scrollBehavior,
                blurState = blurState,
                blurStrength = chromeBlurStrength,
                edgeSoftness = chromeEdgeSoftness,
                overlayOpacity = chromeOverlayOpacity,
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (openDrawer != null) openDrawer()
                            else viewModel.screen.value = Screen.CHAT
                        },
                    ) {
                        Icon(
                            imageVector = if (openDrawer != null) Icons.Outlined.Menu else Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = if (openDrawer != null) "Menu" else "Back",
                        )
                    }
                },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().xyluneBackdropSource(blurState)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = padding.calculateTopPadding() + 20.dp,
                        bottom = padding.calculateBottomPadding() + 20.dp,
                    ),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                if (setupDeferred) {
                    HostSettingsGroup("Setup") {
                        HostSettingsDestination(
                            icon = Icons.Outlined.CheckCircle,
                            title = "Finish setup",
                            subtitle = "Continue from step ${setupStepIndex + 1} of 3",
                            onClick = { viewModel.startSetup(setupStepIndex) },
                        )
                    }
                }
                HostSettingsGroup("Setup & connections") {
                    HostSettingsDestination(
                        icon = Icons.Outlined.Cloud,
                        title = "Providers & models",
                        subtitle = if (providerCount == 0) {
                            "Add your first API provider"
                        } else {
                            "$providerCount provider${if (providerCount == 1) "" else "s"} configured"
                        },
                        onClick = { viewModel.openSettingsRoute(SettingsRoute.PROVIDERS) },
                    )
                    HostSettingsDestination(
                        icon = Icons.Outlined.Cloud,
                        title = "Backup & transfer",
                        subtitle = "Cloud backups, local archives, and restore",
                        onClick = { viewModel.openSettingsRoute(SettingsRoute.BACKUP) },
                    )
                }
                HostSettingsGroup("Chat behavior") {
                    HostSettingsDestination(
                        icon = Icons.Outlined.SmartToy,
                        title = "New chat defaults",
                        subtitle = "Model, thinking, context, and output limits",
                        onClick = { viewModel.openSettingsRoute(SettingsRoute.DEFAULTS) },
                    )
                    HostSettingsDestination(
                        icon = Icons.Outlined.Tune,
                        title = "Response style",
                        subtitle = "Emoji use and global answer presentation",
                        onClick = { viewModel.openSettingsRoute(SettingsRoute.RESPONSE_STYLE) },
                    )
                    HostSettingsDestination(
                        icon = Icons.Outlined.Edit,
                        title = "Custom instructions",
                        subtitle = "Reusable tone and workflow profiles",
                        onClick = { viewModel.openSettingsRoute(SettingsRoute.SYSTEM_PROMPTS) },
                    )
                }
                HostSettingsGroup("Intelligence") {
                    HostSettingsDestination(
                        icon = Icons.Outlined.Psychology,
                        title = "Memory",
                        subtitle = "Cross-chat facts and preferences stored locally",
                        onClick = { viewModel.openSettingsRoute(SettingsRoute.MEMORY) },
                    )
                    HostSettingsDestination(
                        icon = Icons.Outlined.AutoAwesome,
                        title = "Background tasks",
                        subtitle = "Chat naming and context compression models",
                        onClick = { viewModel.openSettingsRoute(SettingsRoute.AUTOMATION) },
                    )
                }
                HostSettingsGroup("Tools & safety") {
                    HostSettingsDestination(
                        icon = Icons.Outlined.Search,
                        title = "Search & web",
                        subtitle = "Native routing, search engines, credentials, and page fetching",
                        onClick = { viewModel.openSettingsRoute(SettingsRoute.SEARCH) },
                    )
                    HostSettingsDestination(
                        icon = Icons.Outlined.Code,
                        title = "Local execution",
                        subtitle = "Python, Linux, packages, and approval policy",
                        onClick = { viewModel.openSettingsRoute(SettingsRoute.LOCAL_EXECUTION) },
                    )
                    HostSettingsDestination(
                        icon = Icons.Outlined.PrivacyTip,
                        title = "Privacy & safety",
                        subtitle = "Generated UI safety and local-data behavior",
                        onClick = { viewModel.openSettingsRoute(SettingsRoute.PRIVACY) },
                    )
                }
                HostSettingsGroup("Personalization") {
                    HostSettingsDestination(
                        icon = Icons.Outlined.Language,
                        title = stringResource(R.string.language_dialog_title),
                        subtitle = selectedLanguageLabel,
                        onClick = onOpenLanguage,
                    )
                    HostSettingsDestination(
                        icon = Icons.Outlined.Palette,
                        title = "Appearance",
                        subtitle = "Theme, palette, launcher icon, and AMOLED black",
                        onClick = { viewModel.openSettingsRoute(SettingsRoute.APPEARANCE) },
                    )
                }
                HostSettingsGroup("About") {
                    HostSettingsDestination(
                        icon = Icons.Outlined.Info,
                        title = "About Xylune",
                        subtitle = "Version, architecture, and privacy model",
                        onClick = { viewModel.openSettingsRoute(SettingsRoute.ABOUT) },
                    )
                }
                Spacer(Modifier.padding(bottom = 24.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSettingsPage(onBack: () -> Unit) {
    val context = LocalContext.current
    val selected = currentAppLanguage(context)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val blurState = rememberXyluneBackdropBlurState()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            CollapsingTranslucentTopBar(
                title = stringResource(R.string.language_dialog_title),
                scrollBehavior = scrollBehavior,
                blurState = blurState,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().xyluneBackdropSource(blurState)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = padding.calculateTopPadding() + 20.dp,
                        bottom = padding.calculateBottomPadding() + 20.dp,
                    ),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
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
        }
    }
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

@Composable
private fun HostSettingsGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 6.dp),
    )
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(content = content)
    }
}

@Composable
private fun HostSettingsDestination(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    val haptics = rememberXyluneHaptics()
    ListItem(
        headlineContent = { Text(title, fontWeight = FontWeight.SemiBold) },
        supportingContent = { Text(subtitle) },
        leadingContent = { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) },
        trailingContent = {
            Icon(
                Icons.Outlined.ChevronRight,
                null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        modifier = Modifier.clickable {
            haptics.selection()
            onClick()
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
    )
}
