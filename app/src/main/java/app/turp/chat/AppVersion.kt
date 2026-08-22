package app.turp.chat

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

data class InstalledAppVersion(
    val versionName: String,
    val versionCode: Int,
)

/** Returns the version Android reports as installed for this package. */
fun Context.installedAppVersion(): InstalledAppVersion {
    val packageInfo = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(
                packageName,
                PackageManager.PackageInfoFlags.of(0),
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0)
        }
    }.getOrNull()

    val versionName = packageInfo?.versionName
        ?.trim()
        ?.takeIf(String::isNotEmpty)
        ?: BuildConfig.VERSION_NAME
    val versionCode = when {
        packageInfo == null -> BuildConfig.VERSION_CODE
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ->
            packageInfo.longVersionCode.coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()
        else -> {
            @Suppress("DEPRECATION")
            packageInfo.versionCode
        }
    }
    return InstalledAppVersion(versionName, versionCode)
}
