from pathlib import Path
import re


def replace_once(path: str, old: str, new: str) -> None:
    p = Path(path)
    text = p.read_text()
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{path}: expected exactly one match, found {count}: {old[:160]!r}")
    p.write_text(text.replace(old, new, 1))


# Keep thinking/reasoning semantics attached to the preset when a provider connection
# has a generated instance ID such as provider-openrouter-<uuid>.
thinking = "app/src/main/java/app/xylune/chat/provider/ThinkingCapabilities.kt"
replace_once(
    thinking,
    '        val openRouterMetadata = provider?.id.equals("openrouter", ignoreCase = true) ||\n            model.metadataSource.contains("openrouter", ignoreCase = true)',
    '        val openRouterMetadata = ModelRequestPolicy.matchesPresetId(provider?.id, "openrouter") ||\n            model.metadataSource.contains("openrouter", ignoreCase = true)',
)
replace_once(
    thinking,
    '    if (providerId == "openai" && modelId.contains("gpt-5.1") && !modelId.contains("codex-max")) {',
    '    if (ModelRequestPolicy.matchesPresetId(providerId, "openai") && modelId.contains("gpt-5.1") && !modelId.contains("codex-max")) {',
)

# Behavioral regression coverage for UUID-backed OpenRouter/OpenAI provider instances.
reasoning_test = "app/src/test/java/app/xylune/chat/provider/OpenRouterReasoningMetadataTest.kt"
replace_once(
    reasoning_test,
    '    private fun reasoningModel(mandatory: Boolean) = ModelEntity(',
    '''    @Test
    fun `OpenRouter instance id retains normalized effort fallback`() {
        val instance = provider.copy(id = "provider-openrouter-test-instance")
        val model = reasoningModel(mandatory = false).copy(
            providerId = instance.id,
            reasoningEffortsCsv = "",
            metadataSource = "",
        )
        val efforts = supportedThinkingLevels(instance, model).mapNotNull { it.effort }
        assertEquals(ThinkingEffort.entries.toList(), efforts)
    }

    @Test
    fun `OpenAI instance id retains gpt 5 1 effort policy`() {
        val instance = provider.copy(
            id = "provider-openai-test-instance",
            displayName = "OpenAI secondary",
            baseUrl = "https://api.openai.com/v1",
        )
        val model = reasoningModel(mandatory = false).copy(
            providerId = instance.id,
            modelId = "gpt-5.1",
            displayName = "GPT-5.1",
            reasoningMetadataAvailable = false,
            reasoningEffortsCsv = "",
            metadataSource = "",
        )
        val efforts = supportedThinkingLevels(instance, model).mapNotNull { it.effort }
        assertEquals(listOf(ThinkingEffort.LOW, ThinkingEffort.MEDIUM, ThinkingEffort.HIGH), efforts)
    }

    private fun reasoningModel(mandatory: Boolean) = ModelEntity(''',
)

# The previous generated branding test used Python escapes inside a non-raw triple string,
# so Kotlin received unescaped quote characters. Rewrite it deterministically.
branding_test = Path("app/src/test/java/app/xylune/chat/ui/XyluneBrandingRegressionTest.kt")
branding_test.write_text(r'''package app.xylune.chat.ui

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
        val gradle = File("build.gradle.kts").readText()
        assertTrue(strings.contains("<string name=\"app_name\">Turp</string>"))
        assertTrue(gradle.contains("applicationId = \"app.xylune.chat\""))
    }
}
''')

# Generate a proper one-color adaptive-icon mask. Gradient-only paths from the color icon
# must not become implicit/default fills when Android applies a themed icon tint.
foreground = Path("app/src/main/res/drawable/ic_xylune_foreground.xml").read_text()
mono = re.sub(r'\s*<aapt:attr name="android:fillColor">.*?</aapt:attr>', '', foreground, flags=re.S)
mono = mono.replace('    xmlns:aapt="http://schemas.android.com/aapt"\n', '')
mono = re.sub(r'android:fillColor="[^"]+"', 'android:fillColor="#FFFFFFFF"', mono)
mono = re.sub(r'\s+android:fillAlpha="[^"]+"', '', mono)
mono = re.sub(
    r'<path(?![^>]*android:fillColor)([^>]*?)android:pathData=',
    r'<path android:fillColor="#FFFFFFFF"\1android:pathData=',
    mono,
)
for directory in (Path("app/src/main/res/drawable"), Path("app/src/main/res/drawable-v31")):
    if directory.exists():
        for p in directory.glob("ic_xylune_monochrome*.xml"):
            if "safe" not in p.name:
                p.write_text(mono)

# Minimal, no-text, abstract Turp banner. This intentionally avoids a literal whole-radish
# illustration while carrying the same cream/pink/green identity as the approved mark.
banner = '''<svg xmlns="http://www.w3.org/2000/svg" width="1600" height="520" viewBox="0 0 1600 520" role="img" aria-label="Turp brand banner">
  <defs>
    <linearGradient id="cream" x1="0" y1="0" x2="1600" y2="520" gradientUnits="userSpaceOnUse">
      <stop stop-color="#fff0d7"/><stop offset="0.52" stop-color="#fee8c8"/><stop offset="1" stop-color="#fde1bd"/>
    </linearGradient>
    <radialGradient id="pink" cx="0" cy="0" r="1" gradientTransform="translate(390 360) rotate(-18) scale(420 280)" gradientUnits="userSpaceOnUse">
      <stop stop-color="#ef2e52" stop-opacity=".72"/><stop offset="1" stop-color="#ef2e52" stop-opacity="0"/>
    </radialGradient>
    <radialGradient id="green" cx="0" cy="0" r="1" gradientTransform="translate(1220 115) rotate(18) scale(520 260)" gradientUnits="userSpaceOnUse">
      <stop stop-color="#72b442" stop-opacity=".66"/><stop offset="1" stop-color="#338331" stop-opacity="0"/>
    </radialGradient>
    <filter id="blur"><feGaussianBlur stdDeviation="22"/></filter>
  </defs>
  <rect width="1600" height="520" rx="54" fill="url(#cream)"/>
  <ellipse cx="350" cy="390" rx="430" ry="260" fill="url(#pink)" filter="url(#blur)"/>
  <ellipse cx="1260" cy="90" rx="520" ry="250" fill="url(#green)" filter="url(#blur)"/>
  <path d="M1030 500C1120 395 1215 330 1350 285C1455 250 1540 240 1600 247V520H1030Z" fill="#338331" fill-opacity=".10"/>
  <path d="M0 300C155 275 278 300 390 365C470 411 535 460 592 520H0V300Z" fill="#ef2e52" fill-opacity=".08"/>
</svg>
'''
Path("branding/turp-banner.svg").write_text(banner)
readme = Path("README.md")
readme_text = readme.read_text()
readme_text = readme_text.replace(
    '<img src="branding/turp-radish.svg" alt="Turp radish logo" width="220">',
    '<img src="branding/turp-banner.svg" alt="Turp" width="100%">',
    1,
)
readme.write_text(readme_text)

# Final safety/behavior invariants before Gradle gets a chance to run.
assert 'ModelRequestPolicy.matchesPresetId(provider?.id, "openrouter")' in Path(thinking).read_text()
assert 'ModelRequestPolicy.matchesPresetId(providerId, "openai")' in Path(thinking).read_text()
assert 'provider-openrouter-test-instance' in Path(reasoning_test).read_text()
assert 'android:insetLeft=\\"10dp\\"' in branding_test.read_text()
assert all('android:fillColor="#FFFFFFFF"' in p.read_text() for p in Path("app/src/main/res/drawable").glob("ic_xylune_monochrome*.xml") if "safe" not in p.name)
assert 'branding/turp-banner.svg' in readme.read_text()
assert 'applicationId = "app.xylune.chat"' in Path("app/build.gradle.kts").read_text()
print("Turp final compatibility pass applied successfully")
