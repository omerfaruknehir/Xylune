package app.turp.chat.generation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ResearchStateEnforcerTest {
    @Test fun acceptsTaskSpecificModelState() {
        val text = """
            <turp-research-state>
            {"status":"Planning sources for the battery comparison","reportState":"planning","progress":0.05,"steps":[{"id":"specs","title":"Collect official specifications","state":"active"}]}
            </turp-research-state>
        """.trimIndent()
        assertNotNull(ResearchStateEnforcer.firstValidBlock(text))
        assertFalse(ResearchStateEnforcer.hasTerminalBlock(text))
    }

    @Test fun rejectsEmptyOrPlaceholderRoadmaps() {
        val empty = "<turp-research-state>{\"status\":\"Waiting\",\"reportState\":\"planning\",\"progress\":0,\"steps\":[]}</turp-research-state>"
        assertFalse(ResearchStateEnforcer.hasValidBlock(empty))
    }

    @Test fun recognizesModelReportedCompletion() {
        val complete = "<turp-research-state>{\"status\":\"Report completed from verified evidence\",\"reportState\":\"complete\",\"progress\":1,\"steps\":[{\"id\":\"report\",\"title\":\"Write cited report\",\"state\":\"complete\"}]}</turp-research-state>"
        assertTrue(ResearchStateEnforcer.hasTerminalBlock(complete))
    }
}
