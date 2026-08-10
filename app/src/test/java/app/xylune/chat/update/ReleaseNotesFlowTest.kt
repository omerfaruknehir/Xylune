package app.xylune.chat.update

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReleaseNotesFlowTest {
    @Test
    fun freshInstallWithoutHistoryDoesNotShowWhatsNew() {
        assertFalse(
            shouldShowInstalledReleaseNotes(
                lastSeenVersionCode = null,
                currentVersionCode = 218,
                wasUpdatedInstall = false,
            ),
        )
    }

    @Test
    fun existingInstallUpgradedBeforeFeatureWasIntroducedShowsWhatsNew() {
        assertTrue(
            shouldShowInstalledReleaseNotes(
                lastSeenVersionCode = null,
                currentVersionCode = 218,
                wasUpdatedInstall = true,
            ),
        )
    }

    @Test
    fun newerInstalledVersionShowsExactlyOnce() {
        assertTrue(
            shouldShowInstalledReleaseNotes(
                lastSeenVersionCode = 217,
                currentVersionCode = 218,
                wasUpdatedInstall = true,
            ),
        )
        assertFalse(
            shouldShowInstalledReleaseNotes(
                lastSeenVersionCode = 218,
                currentVersionCode = 218,
                wasUpdatedInstall = true,
            ),
        )
    }

    @Test
    fun debugAndDowngradeBuildsDoNotShowUpgradeNotes() {
        assertFalse(
            shouldShowInstalledReleaseNotes(
                lastSeenVersionCode = 220,
                currentVersionCode = 218,
                wasUpdatedInstall = true,
            ),
        )
        assertFalse(
            shouldShowInstalledReleaseNotes(
                lastSeenVersionCode = 217,
                currentVersionCode = 218,
                wasUpdatedInstall = true,
                debugBuild = true,
            ),
        )
    }
}
