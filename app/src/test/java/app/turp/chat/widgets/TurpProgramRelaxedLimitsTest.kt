package app.turp.chat.widgets

import org.junit.Assert.assertTrue
import org.junit.Test

class TurpProgramRelaxedLimitsTest {
    @Test
    fun widgetInputNodeIsAcceptedAsDisplayControl() {
        val source = """{
          "schema":"turp-widget/1",
          "id":"input_widget",
          "title":"Input",
          "state":{"name":"Turp"},
          "ui":{"type":"input","value":"name","label":"Name","action":"open"},
          "actions":{"open":[{"op":"open_app","route":"memory"}]}
        }""".trimIndent()
        assertTrue(TurpProgramParser.parse(source, TurpProgramSurface.WIDGET).isSuccess)
    }

    @Test
    fun widgetCanContainMoreThanLegacySixListRows() {
        val items = (1..20).joinToString(",") { "{\"label\":\"Row $it\",\"value\":\"$it\"}" }
        val source = """{
          "schema":"turp-widget/1",
          "id":"long_list",
          "title":"List",
          "state":{},
          "ui":{"type":"list","items":[$items]}
        }""".trimIndent()
        val parsed = TurpProgramParser.parse(source, TurpProgramSurface.WIDGET).getOrThrow()
        assertTrue(parsed.ui.items.size == 20)
    }
}
