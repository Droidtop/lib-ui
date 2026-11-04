package dev.droidtop.lib.ui.contract

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * One answer from a backend (contract/README.md): ok, unavailable, error or pending.
 * [slot] names the operation and [request], on a settled answer, the pending request it
 * settles. [fields] are the operation's own fields, kept as they came.
 */
sealed interface Answer {
    val slot: String?
    val request: String?
    val fields: JsonObject

    data class Ok(
        override val fields: JsonObject = JsonObject(emptyMap()),
        override val slot: String? = null,
        override val request: String? = null,
    ) : Answer

    /** No backend for this here; [message] is the reason in words a screen can show. */
    data class Unavailable(
        val message: String,
        val title: String? = null,
        override val slot: String? = null,
        override val request: String? = null,
        override val fields: JsonObject = JsonObject(emptyMap()),
    ) : Answer

    /** It was tried and failed; [message] is the reason, [code] a stable word for programs. */
    data class Error(
        val message: String,
        val code: String? = null,
        override val slot: String? = null,
        override val request: String? = null,
        override val fields: JsonObject = JsonObject(emptyMap()),
    ) : Answer

    /** The answer comes later, settled by an answer carrying the same [request]. */
    data class Pending(
        override val request: String,
        override val slot: String? = null,
        override val fields: JsonObject = JsonObject(emptyMap()),
    ) : Answer

    /** The answer as it goes on the wire. */
    fun encode(): JsonObject = buildJsonObject {
        fields.forEach { (k, v) -> put(k, v) }
        when (val a = this@Answer) {
            is Ok -> put("status", "ok")
            is Unavailable -> {
                put("status", "unavailable"); put("message", a.message); a.title?.let { put("title", it) }
            }
            is Error -> {
                put("status", "error"); put("message", a.message); a.code?.let { put("code", it) }
            }
            is Pending -> put("status", "pending")
        }
        slot?.let { put("slot", it) }
        request?.let { put("request", it) }
    }

    companion object {
        const val NO_ANSWER = "There was no answer."
        const val MALFORMED = "The answer could not be read."
        private val OWN = setOf("status", "slot", "message", "title", "code", "request")

        /** Reads an answer from its JSON text; text that is not JSON is [MALFORMED]. */
        fun decode(text: String?): Answer {
            if (text == null) return decode(null as JsonElement?)
            val element = runCatching { Json.parseToJsonElement(text) }.getOrNull() ?: return malformed()
            return decode(element)
        }

        /**
         * The one reading of what a backend sent. Anything that is not a well-formed answer is an
         * [Error] (contract rule 1); a bare list is the legacy [Ok] with the list under `value`.
         */
        fun decode(value: JsonElement?): Answer {
            if (value == null || value is JsonNull) return Error(NO_ANSWER, code = "no_answer")
            if (value is JsonArray) return Ok(JsonObject(mapOf("value" to value)))
            if (value !is JsonObject) return malformed()
            val own = HashMap<String, String>()
            for (name in OWN) {
                val v = value[name] ?: continue
                if (v !is JsonPrimitive || !v.isString) return malformed()
                own[name] = v.content
            }
            val fields = JsonObject(value.filterKeys { it !in OWN })
            val slot = own["slot"]
            val request = own["request"]
            return when (own["status"]) {
                "ok" -> Ok(fields, slot, request)
                "unavailable" -> own["message"]?.takeIf { it.isNotEmpty() }
                    ?.let { Unavailable(it, own["title"], slot, request, fields) } ?: malformed()
                "error" -> own["message"]?.takeIf { it.isNotEmpty() }
                    ?.let { Error(it, own["code"], slot, request, fields) } ?: malformed()
                "pending" -> request?.takeIf { it.isNotEmpty() }?.let { Pending(it, slot, fields) } ?: malformed()
                else -> malformed()
            }
        }

        private fun malformed() = Error(MALFORMED, code = "malformed")

        /** Runs one operation; whatever it throws becomes an [Error] naming it, and [log] is told once. */
        inline fun guard(slot: String, log: (String, Throwable) -> Unit = { _, _ -> }, work: () -> Answer): Answer =
            try {
                work()
            } catch (e: Exception) {
                log(slot, e)
                Error(e.message?.takeIf { it.isNotBlank() } ?: e.javaClass.simpleName, slot = slot)
            }
    }
}
