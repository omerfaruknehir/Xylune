from pathlib import Path
import re


def replace_once(path: str, old: str, new: str) -> None:
    p = Path(path)
    text = p.read_text()
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{path}: expected exactly one match, found {count}: {old[:140]!r}")
    p.write_text(text.replace(old, new, 1))


def replace_regex_once(path: str, pattern: str, replacement: str) -> None:
    p = Path(path)
    text = p.read_text()
    updated, count = re.subn(pattern, replacement, text, count=1, flags=re.S)
    if count != 1:
        raise SystemExit(f"{path}: expected one regex match, found {count}: {pattern!r}")
    p.write_text(updated)


# ---------------------------------------------------------------------------
# Provider presets are templates, while every saved connection is an instance.
# Preserve preset-specific protocol behavior for UUID-backed instances.
# ---------------------------------------------------------------------------
settings = "app/src/main/java/app/xylune/chat/ui/SettingsScreen.kt"
replace_once(
    settings,
    "if (addingProvider) AddProviderDialog(\n        templates =",
    "if (addingProvider) AddProviderDialog(\n        initialTemplateId = addingProviderTemplateId,\n        templates =",
)

policy = "app/src/main/java/app/xylune/chat/provider/ModelRequestPolicy.kt"
replace_once(
    policy,
    '    private val qwen3OpenSourceHybridModels = setOf(',
    '''    /**
     * Saved API-key connections get unique IDs so the same preset can be used more than once.
     * This helper keeps provider-specific protocol decisions attached to the preset identity
     * without forcing the persisted row to reuse the preset's singleton ID.
     */
    fun matchesPresetId(providerId: String?, presetId: String): Boolean {
        val id = providerId.orEmpty()
        return id.equals(presetId, ignoreCase = true) ||
            id.startsWith("provider-$presetId-", ignoreCase = true)
    }

    fun matchesPreset(provider: ProviderEntity, presetId: String): Boolean =
        matchesPresetId(provider.id, presetId)

    private val qwen3OpenSourceHybridModels = setOf(''',
)
replace_once(
    policy,
    '(provider.id == "openrouter" || isOpenRouterBaseUrl(provider.baseUrl))',
    '(matchesPreset(provider, "openrouter") || isOpenRouterBaseUrl(provider.baseUrl))',
)
replace_once(
    policy,
    '(provider.id.equals("qwen-cloud", ignoreCase = true) || isQwenCloudBaseUrl(provider.baseUrl))',
    '(matchesPreset(provider, "qwen-cloud") || isQwenCloudBaseUrl(provider.baseUrl))',
)
replace_once(
    policy,
    '(provider.id == "openai" || isOfficialOpenAiBaseUrl(provider.baseUrl))',
    '(matchesPreset(provider, "openai") || isOfficialOpenAiBaseUrl(provider.baseUrl))',
)
replace_once(
    policy,
    '            provider.id !in automaticOpenAiCompatiblePresetIds',
    '            automaticOpenAiCompatiblePresetIds.none { presetId -> matchesPreset(provider, presetId) }',
)

compatible = "app/src/main/java/app/xylune/chat/provider/OpenAiCompatibleProvider.kt"
replace_once(
    compatible,
    '        val isDeepSeek = request.provider.id == "deepseek"',
    '        val isDeepSeek = ModelRequestPolicy.matchesPreset(request.provider, "deepseek")',
)
replace_once(
    compatible,
    '            if (request.provider.id in setOf("openai", "deepseek", "openrouter", "xai", "qwen-cloud") || isOpenRouter || isAlibaba) {',
    '            if (listOf("openai", "deepseek", "openrouter", "xai", "qwen-cloud").any { ModelRequestPolicy.matchesPreset(request.provider, it) } || isOpenRouter || isAlibaba) {',
)

responses = "app/src/main/java/app/xylune/chat/provider/ResponsesApiTransport.kt"
replace_once(
    responses,
    '            providerId == "deepseek" || baseUrl.contains("api.deepseek.com") -> {',
    '            ModelRequestPolicy.matchesPreset(request.provider, "deepseek") || baseUrl.contains("api.deepseek.com") -> {',
)
replace_once(
    responses,
    '            providerId in setOf("openai", "openrouter", "xai") -> NativeWebSearchMode.RESPONSES',
    '            listOf("openai", "openrouter", "xai").any { ModelRequestPolicy.matchesPreset(request.provider, it) } -> NativeWebSearchMode.RESPONSES',
)
replace_once(
    responses,
    '            providerId == "deepseek" || baseUrl.contains("api.deepseek.com") -> "DeepSeek native search"',
    '            ModelRequestPolicy.matchesPreset(request.provider, "deepseek") || baseUrl.contains("api.deepseek.com") -> "DeepSeek native search"',
)
replace_once(
    responses,
    '            providerId == "openai" || baseUrl.contains("api.openai.com") -> "OpenAI native search"',
    '            ModelRequestPolicy.matchesPreset(request.provider, "openai") || baseUrl.contains("api.openai.com") -> "OpenAI native search"',
)
replace_once(
    responses,
    '            providerId == "openrouter" || baseUrl.contains("openrouter.ai") -> "OpenRouter native search"',
    '            ModelRequestPolicy.matchesPreset(request.provider, "openrouter") || baseUrl.contains("openrouter.ai") -> "OpenRouter native search"',
)
replace_once(
    responses,
    '            providerId == "xai" || baseUrl.contains("api.x.ai") -> "xAI native search"',
    '            ModelRequestPolicy.matchesPreset(request.provider, "xai") || baseUrl.contains("api.x.ai") -> "xAI native search"',
)
replace_once(
    responses,
    '        if (request.provider.id.equals("openrouter", ignoreCase = true) ||',
    '        if (ModelRequestPolicy.matchesPreset(request.provider, "openrouter") ||',
)

alibaba = "app/src/main/java/app/xylune/chat/provider/AlibabaImageRoutingProvider.kt"
replace_once(
    alibaba,
    '            val neutralized = if (request.provider.id.equals("qwen-cloud", ignoreCase = true)) {',
    '            val neutralized = if (ModelRequestPolicy.matchesPreset(request.provider, "qwen-cloud")) {',
)

discovery = "app/src/main/java/app/xylune/chat/provider/ModelDiscoveryService.kt"
replace_once(
    discovery,
    '            if (providerId.equals("qwen-cloud", ignoreCase = true) || ModelRequestPolicy.isQwenCloudBaseUrl(baseUrl)) {',
    '            if (ModelRequestPolicy.matchesPresetId(providerId, "qwen-cloud") || ModelRequestPolicy.isQwenCloudBaseUrl(baseUrl)) {',
)

provider_test = Path("app/src/test/java/app/xylune/chat/provider/ProviderInstancePresetTest.kt")
provider_test.write_text('''package app.xylune.chat.provider

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProviderInstancePresetTest {
    @Test
    fun `unique connection ids retain preset semantics`() {
        listOf("openai", "deepseek", "openrouter", "groq", "mistral", "xai", "qwen-cloud", "ollama").forEach { preset ->
            assertTrue(ModelRequestPolicy.matchesPresetId(preset, preset))
            assertTrue(ModelRequestPolicy.matchesPresetId("provider-$preset-1234", preset))
        }
        assertFalse(ModelRequestPolicy.matchesPresetId("provider-gemini-1234", "deepseek"))
        assertFalse(ModelRequestPolicy.matchesPresetId("custom-openai-compatible", "openai"))
    }

    @Test
    fun `provider preset selection is reusable and reaches the sheet`() {
        val source = File("src/main/java/app/xylune/chat/ui/SettingsScreen.kt").readText()
        assertTrue(source.contains("initialTemplateId = addingProviderTemplateId"))
        assertTrue(source.contains("templates = DefaultCatalog.providers.filter"))
        assertTrue(source.contains("provider-${'$'}{templateId ?: draft.kind.name.lowercase()}-${'$'}{UUID.randomUUID()}"))
    }
}
''')

# ---------------------------------------------------------------------------
# Rebrand user-facing test contracts while protecting stable repo/protocol IDs.
# The earlier source pass already uses standalone-word replacement; mirror it in tests.
# ---------------------------------------------------------------------------
standalone_brand = re.compile(r"\bXylune\b")
string_re = re.compile(r'"""[\s\S]*?"""|"(?:\\.|[^"\\])*"')
protected_fragments = (
    "omerfaruknehir/Xylune",
    "omerfaruknehir.github.io/Xylune",
    "github.com/omerfaruknehir/Xylune",
    "app.xylune.chat",
    "XyluneProgramRuntime",
    "XyluneApplication",
    "XyluneDatabase",
    "XyluneArchiveManager",
    "XyluneAlertDialog",
    "XyluneDropdownMenu",
    "XylunePopupBackHandler",
    "XyluneApp",
)
for p in Path("app/src/test/java").rglob("*.kt"):
    source = p.read_text()
    def rebrand_test_literal(match):
        literal = match.group(0)
        if "Xylune" not in literal or any(fragment in literal for fragment in protected_fragments):
            return literal
        return standalone_brand.sub("Turp", literal)
    updated = string_re.sub(rebrand_test_literal, source)
    if updated != source:
        p.write_text(updated)

# Repository slug, GitHub Pages base path, app ID and old archive protocol are compatibility IDs.
# Restore them if a broad user-visible pass happened to touch a surrounding document literal.
for root in (Path("app/src/main"), Path("app/src/test"), Path("docs"), Path(".")):
    paths = [root] if root.is_file() else root.rglob("*")
    for p in paths:
        if not p.is_file() or p.suffix.lower() not in {".kt", ".xml", ".md", ".html", ".js", ".yml", ".yaml", ".json"}:
            continue
        try:
            text = p.read_text()
        except UnicodeDecodeError:
            continue
        fixed = text.replace("omerfaruknehir/Turp", "omerfaruknehir/Xylune")
        fixed = fixed.replace("omerfaruknehir.github.io/Turp", "omerfaruknehir.github.io/Xylune")
        if fixed != text:
            p.write_text(fixed)

config = Path("docs/_config.yml")
config_text = config.read_text()
config_text = re.sub(r"(?m)^title:\s*.*$", "title: Turp", config_text)
config_text = re.sub(r"(?m)^baseurl:\s*.*$", "baseurl: /Xylune", config_text)
config_text = re.sub(r"(?m)^repository:\s*.*$", "repository: omerfaruknehir/Xylune", config_text)
config.write_text(config_text)

# ---------------------------------------------------------------------------
# Android visual rebrand. Keep old resource identifiers so upgrades, aliases and
# palette preferences continue to resolve, but replace their artwork with the radish.
# ---------------------------------------------------------------------------
radish_vector = '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:aapt="http://schemas.android.com/aapt"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="1536"
    android:viewportHeight="1536">
    <path android:pathData="M774,649 C784,604 779,559 748,520 C715,480 662,455 643,408 C621,354 629,289 650,242 C664,210 684,206 708,216 C754,236 805,280 840,337 C866,379 874,430 856,489 C842,540 818,595 795,648 Z">
        <aapt:attr name="android:fillColor"><gradient android:type="linear" android:startX="720" android:startY="220" android:endX="765" android:endY="640" android:startColor="#FF78BF43" android:endColor="#FF3F8E31" /></aapt:attr>
    </path>
    <path android:fillColor="#FF3A8331" android:fillAlpha="0.93" android:pathData="M784,641 C787,586 772,518 754,458 C737,398 718,336 698,272 C714,318 735,378 754,436 C774,495 798,560 790,642 Z" />
    <path android:pathData="M827,674 C874,624 927,570 1000,519 C1060,477 1112,454 1155,466 C1210,481 1260,515 1282,548 C1304,579 1286,617 1260,649 C1215,705 1168,732 1115,726 C1078,722 1049,701 1015,684 C973,663 935,656 900,670 C870,682 846,690 827,674 Z">
        <aapt:attr name="android:fillColor"><gradient android:type="linear" android:startX="850" android:startY="670" android:endX="1285" android:endY="585" android:startColor="#FF338331" android:endColor="#FF72B442" /></aapt:attr>
    </path>
    <path android:fillColor="#FF24782D" android:pathData="M831,670 C887,615 960,561 1040,518 C1088,492 1131,463 1160,465 C1201,476 1247,505 1272,535 C1203,548 1125,562 1050,576 C970,592 894,620 831,670 Z" />
    <path android:pathData="M817,661 C801,597 802,523 805,447 C809,372 840,315 890,264 C936,216 982,178 1017,176 C1057,174 1090,198 1111,237 C1134,281 1141,342 1137,393 C1133,444 1115,489 1085,518 C1054,547 1012,563 969,576 C912,594 858,616 817,661 Z">
        <aapt:attr name="android:fillColor"><gradient android:type="linear" android:startX="1030" android:startY="180" android:endX="820" android:endY="660" android:startColor="#FF80C442" android:endColor="#FF5CA735" /></aapt:attr>
    </path>
    <path android:fillColor="#FF367F31" android:pathData="M817,661 C819,611 835,574 856,534 C881,488 907,437 932,387 C957,337 981,287 1007,235 C1017,214 1027,194 1038,179 C1003,178 966,198 923,235 C870,280 826,333 812,385 C797,442 804,526 809,585 C813,619 814,642 817,661 Z" />
    <path android:fillColor="#FFE51F47" android:pathData="M771,638 C786,643 821,665 858,695 L835,746 C797,720 763,700 734,682 Z" />
    <path android:pathData="M734,681 C686,657 633,648 586,654 C519,663 463,699 428,747 C399,786 389,828 400,875 C406,903 421,936 440,970 C458,1004 469,1035 469,1070 C470,1122 449,1173 414,1219 C390,1250 365,1278 378,1289 C386,1296 414,1267 449,1239 C491,1205 532,1181 574,1169 C618,1157 669,1167 718,1161 C771,1154 820,1131 858,1091 C895,1053 918,1005 923,953 C930,891 916,836 884,791 C846,738 790,705 734,681 Z">
        <aapt:attr name="android:fillColor"><gradient android:type="linear" android:startX="500" android:startY="680" android:endX="760" android:endY="1120" android:startColor="#FFFF385D" android:endColor="#FFDB2449" /></aapt:attr>
    </path>
    <path android:pathData="M440,989 C471,1005 500,1024 531,1048 C570,1078 601,1114 631,1158 C600,1159 570,1166 541,1178 C499,1195 462,1223 428,1253 C402,1276 382,1295 376,1290 C369,1284 393,1253 415,1225 C451,1179 470,1128 468,1072 C468,1037 457,1006 440,989 Z">
        <aapt:attr name="android:fillColor"><gradient android:type="linear" android:startX="440" android:startY="1000" android:endX="610" android:endY="1180" android:startColor="#FFEA4668" android:endColor="#FFFFFFFF" /></aapt:attr>
    </path>
    <path android:fillColor="#FFC91A40" android:fillAlpha="0.82" android:pathData="M516,981 C539,925 572,872 613,831 C651,793 694,777 739,773 C784,769 832,777 870,799 C901,817 919,846 923,891 C929,951 916,1005 884,1049 C853,1092 810,1124 765,1143 C725,1160 680,1165 631,1163 C598,1113 567,1073 531,1043 C497,1015 472,998 440,984 C464,994 489,994 516,981 Z" />
</vector>
'''
monochrome_vector = re.sub(r'<aapt:attr name="android:fillColor">.*?</aapt:attr>', '', radish_vector, flags=re.S)
monochrome_vector = re.sub(r'android:fillColor="#[0-9A-Fa-f]+"', 'android:fillColor="#FFFFFFFF"', monochrome_vector)
monochrome_vector = re.sub(r'android:fillAlpha="[^"]+"', '', monochrome_vector)

for directory in (Path("app/src/main/res/drawable"), Path("app/src/main/res/drawable-v31")):
    if not directory.exists():
        continue
    for pattern in ("ic_xylune_foreground*.xml", "ic_xylune_mark*.xml"):
        for p in directory.glob(pattern):
            p.write_text(radish_vector)
    for p in directory.glob("ic_xylune_monochrome*.xml"):
        if "safe" not in p.name:
            p.write_text(monochrome_vector)

cream_background = '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:aapt="http://schemas.android.com/aapt"
    android:width="108dp" android:height="108dp"
    android:viewportWidth="108" android:viewportHeight="108">
    <path android:pathData="M0,0h108v108h-108z">
        <aapt:attr name="android:fillColor"><gradient android:type="radial" android:centerX="56" android:centerY="50" android:gradientRadius="82" android:startColor="#FFFFF0D7" android:endColor="#FFFDE1BD" /></aapt:attr>
    </path>
</vector>
'''
Path("app/src/main/res/drawable/ic_xylune_background.xml").write_text(cream_background)

# Remove the accidental black export matte from the recovered exact SVG source.
brand_svg = Path("branding/turp-radish.svg")
brand = brand_svg.read_text()
brand = brand.replace('  <rect width="1536" height="1536" fill="#000"/>\n', '')
brand_svg.write_text(brand)

branding_test = Path("app/src/test/java/app/xylune/chat/ui/XyluneBrandingRegressionTest.kt")
branding_test.write_text('''package app.xylune.chat.ui

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class XyluneBrandingRegressionTest {
    private fun source(path: String) = File(path).readText()

    @Test
    fun `launcher and in-app marks use Turp radish geometry`() {
        val foreground = source("src/main/res/drawable/ic_xylune_foreground.xml")
        val mark = source("src/main/res/drawable/ic_xylune_mark.xml")
        val monochrome = source("src/main/res/drawable/ic_xylune_monochrome.xml")
        assertTrue(foreground.contains("M734,681"))
        assertTrue(foreground.contains("#FFFF385D"))
        assertTrue(foreground.contains("#FF78BF43"))
        assertTrue(mark.contains("M440,989"))
        assertTrue(monochrome.contains("#FFFFFFFF"))
        assertFalse(foreground.contains("M33.549193,80.863216"))
    }

    @Test
    fun `launcher safe-zone wrappers remain compatible`() {
        val safe = source("src/main/res/drawable/ic_launcher_foreground_safe.xml")
        assertTrue(safe.contains("@drawable/ic_xylune_foreground"))
        assertTrue(safe.contains("android:insetLeft=\"10dp\""))
        assertTrue(safe.contains("android:insetRight=\"10dp\""))
    }

    @Test
    fun `display brand changes without changing package identity`() {
        val strings = source("src/main/res/values/strings.xml")
        val gradle = File("../app/build.gradle.kts").takeIf(File::isFile)?.readText()
            ?: source("build.gradle.kts")
        assertTrue(strings.contains("<string name=\"app_name\">Turp</string>"))
        assertTrue(gradle.contains("applicationId = \"app.xylune.chat\""))
    }
}
''')

# ---------------------------------------------------------------------------
# Website: static/dynamic favicon and logo are radish-based; hero becomes abstract
# brand color fields rather than an old Xylune banner or a literal radish photo.
# ---------------------------------------------------------------------------
# Use the exact recovered icon source for static site logo/favicon.
static_svg = brand_svg.read_text().replace('width="1536" height="1536"', 'width="512" height="512"')
static_svg = static_svg.replace('<svg xmlns="http://www.w3.org/2000/svg"', '<svg xmlns="http://www.w3.org/2000/svg" role="img" aria-label="Turp"')
Path("docs/favicon.svg").write_text(static_svg)
Path("docs/assets/images/xylune-logo.svg").write_text(static_svg)

site = Path("docs/assets/js/site.js")
site_text = site.read_text()
# Re-map the default dynamic icon palette to Turp colors while keeping the stable
# `xylune` scheme key for existing URLs/localStorage.
site_text = site_text.replace("backgroundStart: '#083a2c',\n      backgroundEnd: '#0c684f',\n      markStart: '#86dfb8',\n      markEnd: '#ddfbea',\n      leaf: '#f4c761',\n      secondStroke: '#f1fff7',",
'''backgroundStart: '#fff0d7',
      backgroundEnd: '#fde1bd',
      markStart: '#78bf43',
      markEnd: '#28722e',
      leaf: '#ef2e52',
      secondStroke: '#f5a0b0',''', 1)

new_dynamic = r'''  function dynamicLogoDataUrl(schemePreference) {
    if (!dynamicIconEnabled) return null;

    const paletteName = iconPaletteFor(schemePreference);
    const palette = appIconPalettes[paletteName];
    const svg = `<svg width="512" height="512" viewBox="0 0 1536 1536" xmlns="http://www.w3.org/2000/svg">
  <defs>
    <linearGradient id="bg" x1="220" y1="120" x2="1340" y2="1420" gradientUnits="userSpaceOnUse">
      <stop stop-color="${palette.backgroundStart}"/>
      <stop offset="1" stop-color="${palette.backgroundEnd}"/>
    </linearGradient>
    <linearGradient id="leaf" x1="720" y1="220" x2="860" y2="680" gradientUnits="userSpaceOnUse">
      <stop stop-color="${palette.markStart}"/>
      <stop offset="1" stop-color="${palette.markEnd}"/>
    </linearGradient>
  </defs>
  <rect width="1536" height="1536" rx="350" fill="url(#bg)"/>
  <path d="M774 649C784 604 779 559 748 520C715 480 662 455 643 408C621 354 629 289 650 242C664 210 684 206 708 216C754 236 805 280 840 337C866 379 874 430 856 489C842 540 818 595 795 648Z" fill="url(#leaf)"/>
  <path d="M827 674C874 624 927 570 1000 519C1060 477 1112 454 1155 466C1210 481 1260 515 1282 548C1304 579 1286 617 1260 649C1215 705 1168 732 1115 726C1078 722 1049 701 1015 684C973 663 935 656 900 670C870 682 846 690 827 674Z" fill="url(#leaf)"/>
  <path d="M817 661C801 597 802 523 805 447C809 372 840 315 890 264C936 216 982 178 1017 176C1057 174 1090 198 1111 237C1134 281 1141 342 1137 393C1133 444 1115 489 1085 518C1054 547 1012 563 969 576C912 594 858 616 817 661Z" fill="url(#leaf)"/>
  <path d="M734 681C686 657 633 648 586 654C519 663 463 699 428 747C399 786 389 828 400 875C406 903 421 936 440 970C458 1004 469 1035 469 1070C470 1122 449 1173 414 1219C390 1250 365 1278 378 1289C386 1296 414 1267 449 1239C491 1205 532 1181 574 1169C618 1157 669 1167 718 1161C771 1154 820 1131 858 1091C895 1053 918 1005 923 953C930 891 916 836 884 791C846 738 790 705 734 681Z" fill="${palette.leaf}"/>
  <path d="M440 989C471 1005 500 1024 531 1048C570 1078 601 1114 631 1158C600 1159 570 1166 541 1178C499 1195 462 1223 428 1253C402 1276 382 1295 376 1290C369 1284 393 1253 415 1225C451 1179 470 1128 468 1072C468 1037 457 1006 440 989Z" fill="${palette.secondStroke}"/>
</svg>`;
    return `data:image/svg+xml,${encodeURIComponent(svg)}`;
  }
'''
site_text, replaced = re.subn(r"  function dynamicLogoDataUrl\(schemePreference\) \{.*?\n  \}\n\n  function syncBrandLogo", new_dynamic + "\n  function syncBrandLogo", site_text, count=1, flags=re.S)
if replaced != 1:
    raise SystemExit(f"site.js dynamic logo replacement count={replaced}")
site.write_text(site_text)

for home_path in (Path("docs/index.html"), Path("docs/tr/index.html")):
    home = home_path.read_text()
    home, count = re.subn(
        r'<img\s+class="home-hero__backdrop"[\s\S]*?>',
        '<div class="home-hero__backdrop home-hero__backdrop--turp" aria-hidden="true"></div>',
        home,
        count=1,
    )
    if count != 1:
        raise SystemExit(f"{home_path}: old hero backdrop not found")
    home_path.write_text(home)

banner_css = Path("docs/assets/css/banner.css")
css = banner_css.read_text()
if ".home-hero__backdrop--turp" not in css:
    css += '''

/* Turp rebrand: abstract radish palette, deliberately not a literal produce photo. */
.home-hero__backdrop--turp {
  background:
    radial-gradient(circle at 29% 65%, color-mix(in srgb, #ef2e52 82%, transparent) 0 16%, transparent 39%),
    radial-gradient(circle at 67% 25%, color-mix(in srgb, #72b442 72%, transparent) 0 15%, transparent 42%),
    radial-gradient(circle at 80% 63%, color-mix(in srgb, #338331 48%, transparent) 0 12%, transparent 38%),
    linear-gradient(135deg, #fff0d7 0%, #fee8c8 52%, #fde1bd 100%);
  filter: saturate(.9) contrast(.96);
  transform: scale(1.03);
}
'''
banner_css.write_text(css)

# Update website regression contract to check Turp art while retaining internal compatibility keys.
website_test = Path("app/src/test/java/app/xylune/chat/ui/LegalWebsiteIntegrationTest.kt")
wt = website_test.read_text()
old_palette_block = '''        assertTrue(site.contains("backgroundStart: '#083a2c'"))
        assertTrue(site.contains("backgroundEnd: '#0c684f'"))
        assertTrue(site.contains("markStart: '#86dfb8'"))
        assertTrue(site.contains("markEnd: '#ddfbea'"))
        assertTrue(site.contains("leaf: '#f4c761'"))
        assertTrue(site.contains("secondStroke: '#f1fff7'"))'''
new_palette_block = '''        assertTrue(site.contains("backgroundStart: '#fff0d7'"))
        assertTrue(site.contains("backgroundEnd: '#fde1bd'"))
        assertTrue(site.contains("markStart: '#78bf43'"))
        assertTrue(site.contains("markEnd: '#28722e'"))
        assertTrue(site.contains("leaf: '#ef2e52'"))
        assertTrue(site.contains("secondStroke: '#f5a0b0'"))'''
if old_palette_block not in wt:
    raise SystemExit("website test: default palette assertion block not found")
wt = wt.replace(old_palette_block, new_palette_block, 1)
wt = wt.replace('assertTrue(site.contains("fill=\\"${\'$\'}{palette.leaf}\\""))', 'assertTrue(site.contains("M734 681"))\n        assertTrue(site.contains("fill=\\"${\'$\'}{palette.leaf}\\""))\n        assertTrue(site.contains("fill=\\"${\'$\'}{palette.secondStroke}\\""))', 1)
wt = wt.replace('assertTrue(home.contains("branding/xylune-banner.png"))', 'assertTrue(home.contains("home-hero__backdrop--turp"))\n        assertTrue(!home.contains("branding/xylune-banner.png"))', 1)
website_test.write_text(wt)

# ---------------------------------------------------------------------------
# Final invariants.
# ---------------------------------------------------------------------------
assert 'applicationId = "app.xylune.chat"' in Path("app/build.gradle.kts").read_text()
assert '<string name="app_name">Turp</string>' in Path("app/src/main/res/values/strings.xml").read_text()
assert 'baseurl: /Xylune' in config.read_text()
assert 'repository: omerfaruknehir/Xylune' in config.read_text()
assert 'M734,681' in Path("app/src/main/res/drawable/ic_xylune_foreground.xml").read_text()
assert 'home-hero__backdrop--turp' in Path("docs/index.html").read_text()
assert 'M734 681' in Path("docs/assets/js/site.js").read_text()
print("Turp follow-up finalizer applied successfully")
