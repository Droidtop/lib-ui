package dev.droidtop.lib.ui.contract

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class AnswerTest {
    private val cases: JsonArray =
        Json.parseToJsonElement(File(System.getProperty("contract.fixtures")).readText()).jsonObject["cases"]!!.jsonArray

    /** The shared fixtures' reading of an answer: status, own fields, everything else under `fields`. */
    private fun reading(answer: Answer): JsonObject {
        val wire = answer.encode()
        return buildJsonObject {
            for (name in listOf("status", "slot", "message", "title", "code", "request")) {
                (wire[name] as? JsonPrimitive)?.let { put(name, it) }
            }
            put("fields", answer.fields)
        }
    }

    @Test
    fun everyFixtureDecodesAsWritten() {
        for (case in cases) {
            val c = case.jsonObject
            assertEquals(c["name"]!!.jsonPrimitive.content, c["decoded"], reading(Answer.decode(c["input"])))
        }
    }

    @Test
    fun encodingAnAnswerReadsBackTheSame() {
        for (case in cases) {
            val decoded = Answer.decode(case.jsonObject["input"])
            assertEquals(decoded, Answer.decode(decoded.encode()))
        }
    }

    @Test
    fun textThatIsNotJsonIsMalformed() {
        val answer = Answer.decode("{not json")
        assertTrue(answer is Answer.Error && answer.code == "malformed")
    }

    @Test
    fun aThrowBecomesAnErrorAnswer() {
        var logged: String? = null
        val answer = Answer.guard("boom", { slot, _ -> logged = slot }) { error("no disk") }
        assertEquals(Answer.Error("no disk", slot = "boom"), answer)
        assertEquals("boom", logged)
    }
}
