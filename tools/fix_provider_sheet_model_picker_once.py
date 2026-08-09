from pathlib import Path


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected exactly one match, found {count}")
    return text.replace(old, new, 1)


settings_path = Path("app/src/main/java/app/xylune/chat/ui/SettingsScreen.kt")
settings = settings_path.read_text()

settings = replace_once(
    settings,
    "import androidx.compose.foundation.background\n",
    "import androidx.compose.foundation.ScrollState\nimport androidx.compose.foundation.background\n",
    "ScrollState import",
)

settings = replace_once(
    settings,
    "    var showManualModel by remember { mutableStateOf(false) }\n",
    "    var showManualModel by rememberSaveable { mutableStateOf(false) }\n",
    "save manual model expansion",
)

settings = replace_once(
    settings,
    "    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)\n    val formScrollState = rememberScrollState()\n",
    "    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)\n    val formScrollState = rememberSaveable(saver = ScrollState.Saver) { ScrollState(0) }\n    val modelListScrollState = rememberSaveable(saver = ScrollState.Saver) { ScrollState(0) }\n",
    "save add provider scroll states",
)

settings = replace_once(
    settings,
    "                    Column(Modifier.fillMaxWidth().heightIn(max = 240.dp).verticalScroll(rememberScrollState())) {\n",
    "                    Column(Modifier.fillMaxWidth().heightIn(max = 240.dp).verticalScroll(modelListScrollState)) {\n",
    "stable discovered model scroll",
)

settings = replace_once(
    settings,
    "    var advanced by remember { mutableStateOf(false) }\n",
    "    var advanced by rememberSaveable(provider.id) { mutableStateOf(false) }\n",
    "save provider advanced expansion",
)

settings = replace_once(
    settings,
    "    val selectedModels by selectedModelFlow.collectAsStateWithLifecycle(initialValue = emptyList())\n\n    LaunchedEffect(selected?.id) {\n",
    "    val selectedModels by selectedModelFlow.collectAsStateWithLifecycle(initialValue = emptyList())\n    val editConnectionSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)\n    val editConnectionScrollState = rememberSaveable(selected?.id, saver = ScrollState.Saver) { ScrollState(0) }\n\n    LaunchedEffect(selected?.id) {\n",
    "stable edit connection sheet state",
)

old_edit_sheet = '''    if (editingConnection) {\n        ModalBottomSheet(onDismissRequest = { editingConnection = false }) {\n            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp).verticalScroll(rememberScrollState())) {\n                Text("Edit connection", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)\n                Text(selected?.displayName.orEmpty(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)\n                Spacer(Modifier.size(16.dp))\n                selected?.let { provider ->\n                    ProviderEditor(\n                        provider = provider,\n                        name = providerName,\n                        onName = { providerName = it },\n                        baseUrl = baseUrl,\n                        onBaseUrl = { baseUrl = it },\n                        key = apiKey,\n                        onKey = { apiKey = it },\n                        headers = headers,\n                        onHeaders = { headers = it },\n                        apiKeyRequired = apiKeyRequired,\n                        onApiKeyRequired = { apiKeyRequired = it },\n                    ) {\n                        viewModel.saveProvider(provider.copy(displayName = providerName.trim(), baseUrl = baseUrl.trimEnd('/'), customHeadersJson = headers, apiKeyRequired = apiKeyRequired), apiKey)\n                        editingConnection = false\n                    }\n                }\n                Spacer(Modifier.size(28.dp))\n            }\n        }\n    }\n'''

new_edit_sheet = '''    if (editingConnection) {\n        ModalBottomSheet(\n            onDismissRequest = { editingConnection = false },\n            sheetState = editConnectionSheetState,\n        ) {\n            Column(\n                Modifier\n                    .fillMaxWidth()\n                    .fillMaxHeight(0.94f)\n                    .heightIn(max = 760.dp)\n                    .imePadding(),\n            ) {\n                Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {\n                    Text("Edit connection", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)\n                    Text(selected?.displayName.orEmpty(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)\n                }\n                Column(\n                    Modifier\n                        .weight(1f)\n                        .verticalScroll(editConnectionScrollState)\n                        .padding(horizontal = 20.dp, vertical = 8.dp),\n                ) {\n                    selected?.let { provider ->\n                        ProviderEditor(\n                            provider = provider,\n                            name = providerName,\n                            onName = { providerName = it },\n                            baseUrl = baseUrl,\n                            onBaseUrl = { baseUrl = it },\n                            key = apiKey,\n                            onKey = { apiKey = it },\n                            headers = headers,\n                            onHeaders = { headers = it },\n                            apiKeyRequired = apiKeyRequired,\n                            onApiKeyRequired = { apiKeyRequired = it },\n                        ) {\n                            viewModel.saveProvider(provider.copy(displayName = providerName.trim(), baseUrl = baseUrl.trimEnd('/'), customHeadersJson = headers, apiKeyRequired = apiKeyRequired), apiKey)\n                            editingConnection = false\n                        }\n                    }\n                    Spacer(Modifier.size(28.dp))\n                }\n            }\n        }\n    }\n'''
settings = replace_once(settings, old_edit_sheet, new_edit_sheet, "fixed-height edit connection sheet")
settings_path.write_text(settings)


picker_path = Path("app/src/main/java/app/xylune/chat/ui/ModelPickerSheet.kt")
picker = picker_path.read_text()

picker = picker.replace("import androidx.compose.foundation.layout.fillMaxSize\n", "import androidx.compose.foundation.layout.fillMaxHeight\n")
picker = picker.replace("import androidx.compose.foundation.layout.statusBarsPadding\n", "")
picker = picker.replace("import androidx.compose.ui.window.Dialog\n", "")
picker = picker.replace("import androidx.compose.ui.window.DialogProperties\n", "")
picker = replace_once(
    picker,
    "import androidx.compose.material3.MaterialTheme\n",
    "import androidx.compose.material3.ExperimentalMaterial3Api\nimport androidx.compose.material3.MaterialTheme\nimport androidx.compose.material3.ModalBottomSheet\n",
    "bottom sheet material imports",
)
picker = replace_once(
    picker,
    "import androidx.compose.material3.Surface\n",
    "import androidx.compose.material3.Surface\nimport androidx.compose.material3.rememberModalBottomSheetState\n",
    "bottom sheet state import",
)
picker = replace_once(
    picker,
    "import androidx.compose.runtime.remember\n",
    "import androidx.compose.runtime.remember\nimport androidx.compose.runtime.rememberCoroutineScope\n",
    "coroutine scope import",
)
picker = replace_once(
    picker,
    "import java.util.Locale\n",
    "import java.util.Locale\nimport kotlinx.coroutines.launch\n",
    "coroutine launch import",
)
picker = replace_once(
    picker,
    "@Composable\ninternal fun ModelPickerSheet(\n",
    "@OptIn(ExperimentalMaterial3Api::class)\n@Composable\ninternal fun ModelPickerSheet(\n",
    "model picker opt-in",
)

old_open = '''    Dialog(\n        onDismissRequest = onDismiss,\n        properties = DialogProperties(\n            usePlatformDefaultWidth = false,\n            decorFitsSystemWindows = false,\n        ),\n    ) {\n        Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxSize()) {\n            Column(\n                Modifier\n                    .fillMaxSize()\n                    .statusBarsPadding()\n                    .navigationBarsPadding()\n                    .imePadding()\n                    .padding(horizontal = 16.dp, vertical = 12.dp),\n                verticalArrangement = Arrangement.spacedBy(10.dp),\n            ) {\n'''
new_open = '''    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)\n    val scope = rememberCoroutineScope()\n    var dismissing by remember { mutableStateOf(false) }\n\n    fun dismissSheet() {\n        if (dismissing) return\n        dismissing = true\n        scope.launch {\n            runCatching { sheetState.hide() }\n            onDismiss()\n        }\n    }\n\n    fun selectAndDismiss(providerId: String, modelId: String) {\n        if (dismissing) return\n        dismissing = true\n        scope.launch {\n            runCatching { sheetState.hide() }\n            onSelect(providerId, modelId)\n            onDismiss()\n        }\n    }\n\n    ModalBottomSheet(\n        onDismissRequest = ::dismissSheet,\n        sheetState = sheetState,\n    ) {\n        Column(\n            Modifier\n                .fillMaxWidth()\n                .fillMaxHeight(0.94f)\n                .navigationBarsPadding()\n                .imePadding()\n                .padding(horizontal = 16.dp, vertical = 12.dp),\n            verticalArrangement = Arrangement.spacedBy(10.dp),\n        ) {\n'''
picker = replace_once(picker, old_open, new_open, "replace model picker dialog with bottom sheet")
picker = replace_once(
    picker,
    '                    IconButton(onClick = onDismiss) { Icon(Icons.Outlined.Close, "Close model picker") }\n',
    '                    IconButton(onClick = ::dismissSheet) { Icon(Icons.Outlined.Close, "Close model picker") }\n',
    "animated close button",
)
picker = replace_once(
    picker,
    '''                            modifier = Modifier.clickable {\n                                onSelect(choice.provider.id, choice.model.modelId)\n                                onDismiss()\n                            },\n''',
    '''                            modifier = Modifier.clickable {\n                                selectAndDismiss(choice.provider.id, choice.model.modelId)\n                            },\n''',
    "animated model selection dismissal",
)

marker = "\n}\n\nprivate val ModelPickerChoice.pickerSummary"
marker_index = picker.index(marker)
block_start = picker.index("    ModalBottomSheet(")
block = picker[block_start:marker_index]
old_tail = "            }\n        }\n    }"
if not block.endswith(old_tail):
    raise RuntimeError("model picker closing structure changed unexpectedly")
block = block[: -len(old_tail)] + "        }\n    }"
picker = picker[:block_start] + block + picker[marker_index:]
picker_path.write_text(picker)


sidebar_path = Path("app/src/main/java/app/xylune/chat/ui/ConversationSidebar.kt")
sidebar = sidebar_path.read_text()
sidebar = replace_once(
    sidebar,
    '            Text("On-device history • BYOK", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(12.dp, 4.dp, 12.dp, 0.dp))\n',
    "",
    "remove sidebar BYOK footer",
)
sidebar_path.write_text(sidebar)


turkish_path = Path("app/src/main/java/app/xylune/chat/ui/TurkishUiCopy.kt")
turkish = turkish_path.read_text()
turkish = replace_once(
    turkish,
    '        "On-device history • BYOK" -> "Cihazdaki geçmiş • BYOK"\n',
    "",
    "remove obsolete Turkish BYOK footer copy",
)
turkish_path.write_text(turkish)


test_path = Path("app/src/test/java/app/xylune/chat/ui/ProviderSheetInteractionRegressionTest.kt")
test_path.write_text('''package app.xylune.chat.ui\n\nimport org.junit.Assert.assertFalse\nimport org.junit.Assert.assertTrue\nimport org.junit.Test\nimport java.io.File\n\nclass ProviderSheetInteractionRegressionTest {\n    private fun repositoryFile(path: String): File = sequenceOf(File(path), File("..", path))\n        .firstOrNull(File::isFile)\n        ?: error("Could not locate repository file: $path")\n\n    @Test\n    fun `provider editors preserve scroll state across expanding content`() {\n        val source = repositoryFile("app/src/main/java/app/xylune/chat/ui/SettingsScreen.kt").readText()\n\n        assertTrue(source.contains("rememberSaveable(saver = ScrollState.Saver) { ScrollState(0) }"))\n        assertTrue(source.contains("editConnectionScrollState"))\n        assertTrue(source.contains("rememberSaveable(provider.id) { mutableStateOf(false) }"))\n        assertTrue(source.contains("verticalScroll(modelListScrollState)"))\n        assertTrue(source.contains("sheetState = editConnectionSheetState"))\n        assertTrue(source.contains(".fillMaxHeight(0.94f)"))\n    }\n\n    @Test\n    fun `model picker is a bottom sheet with animated dismissal`() {\n        val source = repositoryFile("app/src/main/java/app/xylune/chat/ui/ModelPickerSheet.kt").readText()\n\n        assertTrue(source.contains("ModalBottomSheet("))\n        assertTrue(source.contains("rememberModalBottomSheetState(skipPartiallyExpanded = true)"))\n        assertTrue(source.contains("sheetState.hide()"))\n        assertTrue(source.contains("selectAndDismiss(choice.provider.id, choice.model.modelId)"))\n        assertFalse(source.contains("Dialog("))\n        assertFalse(source.contains("DialogProperties"))\n    }\n\n    @Test\n    fun `sidebar omits redundant on device byok footer`() {\n        val sidebar = repositoryFile("app/src/main/java/app/xylune/chat/ui/ConversationSidebar.kt").readText()\n        val turkish = repositoryFile("app/src/main/java/app/xylune/chat/ui/TurkishUiCopy.kt").readText()\n\n        assertFalse(sidebar.contains("On-device history • BYOK"))\n        assertFalse(turkish.contains("On-device history • BYOK"))\n    }\n}\n''')

print("Patched provider sheet scrolling, model picker motion, and sidebar footer.")
