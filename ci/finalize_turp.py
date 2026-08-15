from pathlib import Path
import re


def replace_once(path: str, old: str, new: str) -> None:
    p = Path(path)
    text = p.read_text()
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{path}: expected exactly one match, found {count}: {old[:120]!r}")
    p.write_text(text.replace(old, new, 1))


# --- Reusable provider presets / multiple API connections ---
settings = "app/src/main/java/app/xylune/chat/ui/SettingsScreen.kt"
replace_once(
    settings,
    "        ).mapNotNull { (id, label) -> providers.firstOrNull { it.id == id }?.let { it to label } }",
    "        ).mapNotNull { (id, label) -> DefaultCatalog.providers.firstOrNull { it.id == id }?.let { it to label } }",
)
replace_once(
    settings,
    "        templates = providers.filter { provider -> provider.kind != ProviderKind.OPENAI_OAUTH && provider !in registeredProviders },",
    "        templates = DefaultCatalog.providers.filter { provider -> provider.kind != ProviderKind.OPENAI_OAUTH },",
)
replace_once(
    settings,
    '            val id = draft.templateProviderId ?: "provider-${UUID.randomUUID()}"\n            val template = providers.firstOrNull { it.id == draft.templateProviderId }',
    '            val templateId = draft.templateProviderId\n            val id = "provider-${templateId ?: draft.kind.name.lowercase()}-${UUID.randomUUID()}"\n            val template = DefaultCatalog.providers.firstOrNull { it.id == templateId }',
)
replace_once(
    settings,
    "            )).copy(\n                displayName = draft.name,",
    "            )).copy(\n                id = id,\n                displayName = draft.name,",
)
replace_once(
    settings,
    "                val bundled = DefaultCatalog.models.firstOrNull { it.providerId == id && it.modelId == candidate.id }",
    "                val bundled = DefaultCatalog.models.firstOrNull { it.providerId == (templateId ?: id) && it.modelId == candidate.id }",
)
replace_once(
    settings,
    "                val model = base.copy(\n                    displayName = candidate.displayName,",
    "                val model = base.copy(\n                    providerId = id,\n                    displayName = candidate.displayName,",
)

# --- Optional API-key backup ---
store = "app/src/main/java/app/xylune/chat/transfer/AppSettingsArchiveStore.kt"
replace_once(
    store,
    "import app.xylune.chat.provider.ProviderEndpointPolicy\n",
    "import app.xylune.chat.provider.ProviderEndpointPolicy\nimport app.xylune.chat.security.SecureStore\n",
)
replace_once(
    store,
    "    val apiKeyRequired: Boolean,\n)",
    "    val apiKeyRequired: Boolean,\n    val apiKey: String? = null,\n)",
)
replace_once(
    store,
    "    private val preferences: AppPreferences,\n    private val database: XyluneDatabase,\n) {\n    suspend fun snapshot(): PortableAppSettings {",
    "    private val preferences: AppPreferences,\n    private val database: XyluneDatabase,\n    private val secureStore: SecureStore,\n) {\n    suspend fun snapshot(includeApiKeys: Boolean = false): PortableAppSettings {",
)
replace_once(
    store,
    "                    registered = provider.registered,\n                    apiKeyRequired = provider.apiKeyRequired,\n                )",
    "                    registered = provider.registered,\n                    apiKeyRequired = provider.apiKeyRequired,\n                    apiKey = if (includeApiKeys && provider.kind != ProviderKind.OPENAI_OAUTH) {\n                        secureStore.apiKey(provider.id).takeIf(String::isNotBlank)\n                    } else null,\n                )",
)
replace_once(
    store,
    "            val baseUrl = runCatching { ProviderEndpointPolicy.validate(portable.baseUrl) }.getOrNull() ?: return@forEach\n            val canRemainRegistered = portable.registered && !portable.apiKeyRequired && kind != ProviderKind.OPENAI_OAUTH",
    "            val baseUrl = runCatching { ProviderEndpointPolicy.validate(portable.baseUrl) }.getOrNull() ?: return@forEach\n            val restoredApiKey = portable.apiKey.orEmpty().take(MAX_API_KEY_CHARS)\n            val canRemainRegistered = portable.registered &&\n                (!portable.apiKeyRequired || restoredApiKey.isNotBlank()) &&\n                kind != ProviderKind.OPENAI_OAUTH",
)
replace_once(
    store,
    "            )\n            restoredProviderIds += portable.id\n        }\n        value.models.groupBy",
    "            )\n            if (kind != ProviderKind.OPENAI_OAUTH && restoredApiKey.isNotBlank()) {\n                secureStore.setApiKey(portable.id, restoredApiKey)\n            }\n            restoredProviderIds += portable.id\n        }\n        value.models.groupBy",
)
replace_once(
    store,
    "        const val MAX_TRUST_LIST_CHARS = 256_000\n",
    "        const val MAX_TRUST_LIST_CHARS = 256_000\n        const val MAX_API_KEY_CHARS = 8_192\n",
)

application = "app/src/main/java/app/xylune/chat/XyluneApplication.kt"
replace_once(
    application,
    "    val appSettingsArchives = AppSettingsArchiveStore(application, appPreferences, database)",
    "    val appSettingsArchives = AppSettingsArchiveStore(application, appPreferences, database, secureStore)",
)

archive = "app/src/main/java/app/xylune/chat/transfer/XyluneArchiveManager.kt"
replace_once(
    archive,
    "    val includeLinuxEnvironments: Boolean = false,\n    val includeAppSettings: Boolean = false,",
    "    val includeLinuxEnvironments: Boolean = false,\n    val includeAppSettings: Boolean = false,\n    val includeApiKeys: Boolean = false,",
)
replace_once(
    archive,
    "    ) {\n        val bundles = conversationIds.mapNotNull { id -> snapshotConversation(id, options) }\n        val preparedLinux = if (kind == ArchiveKind.BACKUP && options.includeLinuxEnvironments) {",
    "    ) {\n        require(!options.includeApiKeys || kind == ArchiveKind.BACKUP) { \"API keys can only be included in full backups\" }\n        require(!options.includeApiKeys || options.includeAppSettings) { \"Enable app settings before including API keys\" }\n        require(!options.includeApiKeys || password.isNotEmpty()) { \"API keys require a password-encrypted backup\" }\n        val bundles = conversationIds.mapNotNull { id -> snapshotConversation(id, options) }\n        val preparedLinux = if (kind == ArchiveKind.BACKUP && options.includeLinuxEnvironments) {",
)
replace_once(
    archive,
    "            appSettings.snapshot()\n",
    "            appSettings.snapshot(includeApiKeys = options.includeApiKeys)\n",
)

transfer = "app/src/main/java/app/xylune/chat/ui/TransferUi.kt"
replace_once(
    transfer,
    "    var includeAppSettings by remember { mutableStateOf(true) }\n",
    "    var includeAppSettings by remember { mutableStateOf(true) }\n    var includeApiKeys by remember { mutableStateOf(false) }\n",
)
replace_once(
    transfer,
    "        includeLinuxEnvironments = includeLinuxEnvironments,\n        includeAppSettings = includeAppSettings,\n    )\n    val backupLauncher",
    "        includeLinuxEnvironments = includeLinuxEnvironments,\n        includeAppSettings = includeAppSettings,\n        includeApiKeys = includeApiKeys,\n    )\n    val backupReady = passwordsMatch && (!includeApiKeys || password.isNotEmpty())\n    val backupLauncher",
)
replace_once(
    transfer,
    '                    "Chats, branches, app configuration, organization, metadata, and optional attachments. API keys and OAuth sessions are deliberately excluded.",',
    '                    "Chats, branches, app configuration, organization, metadata, and optional attachments. API keys are optional and require password encryption; OAuth sessions are always excluded.",',
)
replace_once(
    transfer,
    '            TransferSwitch("Include app settings and configuration", includeAppSettings) { includeAppSettings = it }',
    '            TransferSwitch("Include app settings and configuration", includeAppSettings) { enabled ->\n                includeAppSettings = enabled\n                if (!enabled) includeApiKeys = false\n            }',
)
old_help = '''            if (includeAppSettings) {
                Text(
                    "Includes theme, UI behavior, new-chat defaults, provider endpoints/models, projects, prompt profiles, and automation settings. Credentials, OAuth sessions, provider authorization headers, cloud grants, drafts, and navigation state stay excluded.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }'''
new_help = '''            if (includeAppSettings) {
                Text(
                    "Includes theme, UI behavior, new-chat defaults, provider endpoints/models, projects, prompt profiles, and automation settings. API keys are included only when separately enabled. OAuth sessions, provider authorization headers, cloud grants, drafts, and navigation state stay excluded.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TransferSwitch("Include API keys", includeApiKeys) { includeApiKeys = it }
                if (includeApiKeys) {
                    Text(
                        "API keys are sensitive. Turp requires a non-empty backup password before this backup can be saved or uploaded. OAuth sessions are never exported.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }'''
replace_once(transfer, old_help, new_help)
replace_once(
    transfer,
    "        options = backupOptions,\n        password = password,\n        enabled = !busy && passwordsMatch,",
    "        options = backupOptions,\n        password = password,\n        enabled = !busy && backupReady,",
)
replace_once(
    transfer,
    "        enabled = !busy && passwordsMatch,\n        modifier = Modifier.fillMaxWidth(),\n    ) {\n        if (busy)",
    "        enabled = !busy && backupReady,\n        modifier = Modifier.fillMaxWidth(),\n    ) {\n        if (busy)",
)
replace_once(
    transfer,
    '                    IncludedRow("App settings and configuration", value.appSettingsIncluded)\n                    IncludedRow("Installed Linux environments", value.options.includeLinuxEnvironments)',
    '                    IncludedRow("App settings and configuration", value.appSettingsIncluded)\n                    IncludedRow("API keys", value.options.includeApiKeys)\n                    IncludedRow("Installed Linux environments", value.options.includeLinuxEnvironments)',
)
replace_once(
    transfer,
    '            "Imports are non-destructive: every chat is created as a separate copy. Existing chats are never overwritten. Provider credentials are not imported, so reconnect the required provider before continuing an imported chat.",',
    '            "Imports are non-destructive: every chat is created as a separate copy. Existing chats are never overwritten. API keys are restored only from backups that explicitly included them; OAuth sessions are never imported.",',
)

# Update the source contract that used to forbid API keys entirely.
feature_test = "app/src/test/java/app/xylune/chat/ui/SetupRestoreSettingsFeatureTest.kt"
text = Path(feature_test).read_text()
start = text.index("    @Test\n    fun portableBackupCarriesNonSecretSettingsAndOrganization()")
end = text.index("\n    @Test\n    fun restoredSettingsPauseSetupButKeepItResumable()", start)
replacement = '''    @Test
    fun portableBackupCanCarryApiKeysOnlyWhenExplicitlyEncrypted() {
        val archive = source("src/main/java/app/xylune/chat/transfer/XyluneArchiveManager.kt")
        val settings = source("src/main/java/app/xylune/chat/transfer/AppSettingsArchiveStore.kt")
        val ui = source("src/main/java/app/xylune/chat/ui/TransferUi.kt")
        assertTrue(archive.contains("includeAppSettings: Boolean = false"))
        assertTrue(archive.contains("includeApiKeys: Boolean = false"))
        assertTrue(archive.contains("appSettings.snapshot(includeApiKeys = options.includeApiKeys)"))
        assertTrue(archive.contains("API keys require a password-encrypted backup"))
        assertTrue(settings.contains("val apiKey: String? = null"))
        assertTrue(settings.contains("SecureStore"))
        assertTrue(settings.contains("secureStore.setApiKey"))
        assertFalse(settings.contains("accessToken"))
        assertTrue(ui.contains("Include API keys"))
        assertTrue(ui.contains("!includeApiKeys || password.isNotEmpty()"))
    }
'''
Path(feature_test).write_text(text[:start] + replacement + text[end:])

# --- User-visible rebrand: keep code/package/schema identifiers stable. ---
# Only replace the standalone product word. This deliberately avoids touching symbols such as
# XyluneProgramRuntime even when they appear inside an interpolated Kotlin string.
string_re = re.compile(r'"""[\s\S]*?"""|"(?:\\.|[^"\\])*"')
standalone_brand = re.compile(r"\bXylune\b")
protected = (
    "github.com/omerfaruknehir/Xylune",
    "omerfaruknehir.github.io/Xylune",
    "app.xylune.chat",
    "XyluneApplication",
    "XyluneDatabase",
)
for p in Path("app/src/main/java").rglob("*.kt"):
    source = p.read_text()
    def repl(match):
        literal = match.group(0)
        if "Xylune" not in literal or any(token in literal for token in protected):
            return literal
        return standalone_brand.sub("Turp", literal)
    updated = string_re.sub(repl, source)
    if updated != source:
        p.write_text(updated)

for p in Path("app/src/main/res").glob("values*/strings.xml"):
    lines = []
    for line in p.read_text().splitlines(keepends=True):
        if "Xylune" in line and "http://" not in line and "https://" not in line and ">" in line and "<" in line:
            first = line.find(">") + 1
            last = line.rfind("<")
            if 0 < first <= last:
                line = line[:first] + standalone_brand.sub("Turp", line[first:last]) + line[last:]
        lines.append(line)
    p.write_text("".join(lines))

# Current docs/site copy: protect URLs and inline-code legacy identifiers, then replace only the
# standalone product word. Historical release notes remain untouched.
doc_files = [
    Path("README.md"), Path("PRIVACY.md"), Path("TERMS.md"), Path("DATA_DELETION.md"),
    Path("BUILDING.md"), Path("ARCHITECTURE.md"), Path("HISTORY_IMPORT.md"), Path("WIDGETS.md"),
    Path("THIRD_PARTY_NOTICES.md"),
]
doc_files += [
    p for p in Path("docs").rglob("*")
    if p.is_file() and p.suffix.lower() in {".md", ".html", ".yml", ".yaml"} and "releases" not in p.parts
]
url_re = re.compile(r"https?://[^\s)>\"']+")
code_re = re.compile(r"`[^`]*`")
for p in doc_files:
    if not p.exists():
        continue
    source = p.read_text()
    stash = []
    def protect_doc(match):
        stash.append(match.group(0))
        return f"@@TURP_PROTECTED_{len(stash)-1}@@"
    masked = url_re.sub(protect_doc, source)
    masked = code_re.sub(protect_doc, masked)
    masked = standalone_brand.sub("Turp", masked)
    for i, value in enumerate(stash):
        masked = masked.replace(f"@@TURP_PROTECTED_{i}@@", value)
    p.write_text(masked)

readme = Path("README.md")
text = readme.read_text()
text = text.replace(
    '<img src="branding/xylune-banner.png" alt="Turp — open-source BYOK AI chat app for Android." width="100%">',
    '<img src="branding/turp-radish.svg" alt="Turp radish logo" width="220">',
    1,
)
text = text.replace('<strong>Turp</strong> (pronounced <strong>“Zy-loon”</strong>)', '<strong>Turp</strong>')
readme.write_text(text)

component = Path("licenses/components/xylune.json")
if component.exists():
    source = component.read_text()
    component.write_text(re.sub(r'("name"\s*:\s*")Xylune("\s*)', r'\1Turp\2', source, count=1))

# Compatibility guards: these legacy identifiers must survive the rebrand.
gradle = Path("app/build.gradle.kts").read_text()
if 'applicationId = "app.xylune.chat"' not in gradle:
    raise SystemExit("compatibility guard: app.xylune.chat applicationId changed or missing")
if 'const val XYLUNE_BACKUP_MIME = "application/vnd.xylune.backup"' not in Path(archive).read_text():
    raise SystemExit("compatibility guard: legacy backup MIME changed")
if '<string name="app_name">Turp</string>' not in Path("app/src/main/res/values/strings.xml").read_text():
    raise SystemExit("rebrand guard: app_name is not Turp")

print("Turp finalizer transformations applied successfully")
