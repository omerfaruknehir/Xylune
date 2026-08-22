package app.turp.chat

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TurpSmokeTest {
    @Test
    fun applicationStartsAndEncryptedDatabaseOpens() {
        val application = ApplicationProvider.getApplicationContext<TurpApplication>()
        assertNotNull(application.container.database.openHelper.writableDatabase)
    }
}
