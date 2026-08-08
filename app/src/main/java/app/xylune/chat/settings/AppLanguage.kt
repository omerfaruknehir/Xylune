package app.xylune.chat.settings

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

enum class AppLanguage(val languageTag: String) {
    SYSTEM(""),
    ENGLISH("en"),
    TURKISH("tr"),
}

private const val LANGUAGE_PREFERENCES = "xylune_app_language"
private const val KEY_APP_LANGUAGE = "application_language"
private val processDefaultLocale: Locale = Locale.getDefault()

fun currentAppLanguage(context: Context): AppLanguage {
    val tag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.getSystemService(LocaleManager::class.java)
            .applicationLocales
            .toLanguageTags()
            .substringBefore(',')
            .trim()
    } else {
        context.getSharedPreferences(LANGUAGE_PREFERENCES, Context.MODE_PRIVATE)
            .getString(KEY_APP_LANGUAGE, "")
            .orEmpty()
    }
    if (tag.isBlank()) return AppLanguage.SYSTEM
    return when (Locale.forLanguageTag(tag).language.lowercase(Locale.ROOT)) {
        "tr" -> AppLanguage.TURKISH
        "en" -> AppLanguage.ENGLISH
        else -> AppLanguage.SYSTEM
    }
}

fun setAppLanguage(context: Context, language: AppLanguage) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.getSystemService(LocaleManager::class.java).applicationLocales =
            if (language == AppLanguage.SYSTEM) LocaleList.getEmptyLocaleList()
            else LocaleList.forLanguageTags(language.languageTag)
        return
    }

    context.getSharedPreferences(LANGUAGE_PREFERENCES, Context.MODE_PRIVATE)
        .edit()
        .putString(KEY_APP_LANGUAGE, language.languageTag)
        .apply()
    (context as? Activity)?.recreate()
}

/**
 * Android 13+ applies per-app locales itself through LocaleManager. Older
 * Android versions need a localized Activity base context so Compose resource
 * lookup follows the same in-app language setting.
 */
fun localizedAppContext(base: Context): Context {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) return base

    val tag = base.getSharedPreferences(LANGUAGE_PREFERENCES, Context.MODE_PRIVATE)
        .getString(KEY_APP_LANGUAGE, "")
        .orEmpty()
    if (tag.isBlank()) {
        Locale.setDefault(processDefaultLocale)
        return base
    }

    val locale = Locale.forLanguageTag(tag)
    Locale.setDefault(locale)
    val configuration = Configuration(base.resources.configuration).apply {
        setLocale(locale)
        setLocales(LocaleList(locale))
    }
    return base.createConfigurationContext(configuration)
}
