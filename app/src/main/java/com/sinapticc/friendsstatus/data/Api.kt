package com.sinapticc.friendsstatus.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

// ------------------------------------------------------------------ wire format (see server/src/index.ts)

@Serializable data class RegisterRes(val id: String, val token: String, val pairCode: String)
@Serializable data class StatusDto(val key: String, val text: String, val hue: Int = 0, val acc: String = "none", val at: Long, val expiresAt: Long? = null)
@Serializable data class MeDto(
    val id: String, val nick: String, val avatar: Int, val look: String? = null, val pairCode: String,
    val ghost: Boolean, val pausedUntil: Long? = null, val precision: String, val status: StatusDto? = null,
)
@Serializable data class MemberDto(val id: String, val nick: String, val avatar: Int, val role: String, val joinedAt: Long)
@Serializable data class GroupDto(
    val id: String, val kind: String, val name: String, val icon: String, val color: Int, val createdAt: Long,
    val role: String, val share: String, val muted: Boolean, val code: String? = null, val codeTtl: String,
    val codeExpiresAt: Long? = null, val members: List<MemberDto>,
)
@Serializable data class HistoryDto(val key: String, val text: String, val at: Long)
@Serializable data class FriendDto(
    val id: String, val nick: String, val avatar: Int, val look: String? = null, val groups: List<String>,
    val status: StatusDto? = null, val distanceKm: Double? = null, val history: List<HistoryDto>,
)
@Serializable data class ReactionDto(val kind: String, val at: Long, val nick: String)
@Serializable data class FeedDto(val serverTime: Long, val me: MeDto, val groups: List<GroupDto>, val friends: List<FriendDto>, val reactions: List<ReactionDto> = emptyList())
@Serializable data class InvitePreview(val kind: String, val name: String, val count: Int, val names: List<String>)
@Serializable data class JoinRes(val id: String, val kind: String, val name: String)
@Serializable data class CreateGroupRes(val id: String, val code: String)
@Serializable data class CodeRes(val code: String)
@Serializable private data class Ok(val ok: Boolean = true)

class ApiException(val status: Int, val code: String) : Exception("$status $code")

val ApiJson = Json { ignoreUnknownKeys = true; explicitNulls = false }

/** Builds a JSON object from plain values; `null` is sent as JSON null so fields can be cleared. */
fun jsonOf(vararg fields: Pair<String, Any?>): JsonObject = JsonObject(fields.associate { (k, v) -> k to toJson(v) })

private fun toJson(v: Any?): JsonElement = when (v) {
    null -> JsonNull
    is JsonElement -> v
    is String -> JsonPrimitive(v)
    is Number -> JsonPrimitive(v)
    is Boolean -> JsonPrimitive(v)
    is List<*> -> JsonArray(v.map { toJson(it) })
    else -> error("unsupported ${v::class}")
}

/** Client for the Cloudflare Worker. All calls run on the IO dispatcher. */
class Api(private val baseUrl: String, private val token: () -> String?) {
    private val http = OkHttpClient.Builder().callTimeout(20, TimeUnit.SECONDS).build()
    private val jsonType = "application/json; charset=utf-8".toMediaType()

    private suspend fun <T> call(method: String, path: String, body: JsonObject?, out: KSerializer<T>): T = withContext(Dispatchers.IO) {
        val req = Request.Builder().url(baseUrl.trimEnd('/') + path)
            .method(method, body?.toString()?.toRequestBody(jsonType) ?: if (method == "GET" || method == "DELETE") null else "{}".toRequestBody(jsonType))
        token()?.let { req.header("Authorization", "Bearer $it") }
        http.newCall(req.build()).execute().use { res ->
            val text = res.body?.string().orEmpty()
            if (!res.isSuccessful) {
                val code = runCatching { ApiJson.parseToJsonElement(text).jsonObject["error"]?.jsonPrimitive?.content }.getOrNull()
                throw ApiException(res.code, code ?: "http_${res.code}")
            }
            ApiJson.decodeFromString(out, text)
        }
    }

    suspend fun register(nick: String, avatar: Int, look: String?) =
        call("POST", "/v1/register", jsonOf("nick" to nick, "avatar" to avatar, "look" to look), RegisterRes.serializer())

    suspend fun feed(since: Long) = call("GET", "/v1/feed?since=$since", null, FeedDto.serializer())

    suspend fun patchMe(vararg fields: Pair<String, Any?>) { call("PATCH", "/v1/me", jsonOf(*fields), Ok.serializer()) }

    suspend fun deleteMe() { call("DELETE", "/v1/me", null, Ok.serializer()) }

    suspend fun clearHistory() { call("DELETE", "/v1/history", null, Ok.serializer()) }

    suspend fun postStatus(
        key: String, text: String, hue: Int, acc: String, visibility: String, visGroups: List<String>,
        expiresInMin: Int?, lat: Double?, lng: Double?,
    ) {
        call(
            "POST", "/v1/status",
            jsonOf(
                "key" to key, "text" to text, "hue" to hue, "acc" to acc, "visibility" to visibility,
                "visGroups" to visGroups, "expiresInMin" to expiresInMin, "lat" to lat, "lng" to lng,
            ),
            Ok.serializer(),
        )
    }

    suspend fun react(to: String, kind: String) { call("POST", "/v1/react", jsonOf("to" to to, "kind" to kind), Ok.serializer()) }

    suspend fun preview(code: String) = call("GET", "/v1/invite/${java.net.URLEncoder.encode(code, "UTF-8")}", null, InvitePreview.serializer())

    suspend fun join(code: String) = call("POST", "/v1/join", jsonOf("code" to code), JoinRes.serializer())

    suspend fun createGroup(name: String, icon: String) = call("POST", "/v1/groups", jsonOf("name" to name, "icon" to icon), CreateGroupRes.serializer())

    suspend fun patchGroup(id: String, vararg fields: Pair<String, Any?>) { call("PATCH", "/v1/groups/$id", jsonOf(*fields), Ok.serializer()) }

    suspend fun patchMembership(id: String, vararg fields: Pair<String, Any?>) { call("PATCH", "/v1/groups/$id/me", jsonOf(*fields), Ok.serializer()) }

    suspend fun resetCode(id: String) = call("POST", "/v1/groups/$id/code", null, CodeRes.serializer())

    suspend fun leave(id: String) { call("POST", "/v1/groups/$id/leave", null, Ok.serializer()) }

    suspend fun setRole(id: String, userId: String, admin: Boolean) { call("POST", "/v1/groups/$id/members/$userId/role", jsonOf("admin" to admin), Ok.serializer()) }

    suspend fun removeMember(id: String, userId: String) { call("DELETE", "/v1/groups/$id/members/$userId", null, Ok.serializer()) }
}
