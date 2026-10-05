package com.example.pokemax

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

object TranslationService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    private val cache = ConcurrentHashMap<String, String>()

    suspend fun translateToPortuguese(text: String?): String = withContext(Dispatchers.IO) {
        if (text.isNullOrBlank()) return@withContext "Sem descrição disponível."

        val cleanText = text.replace("\n", " ").replace("\u000c", " ").trim()
        val cached = cache[cleanText]
        if (cached != null) return@withContext cached

        try {
            val encodedQuery = URLEncoder.encode(cleanText, "UTF-8")
            val url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=pt-BR&dt=t&q=$encodedQuery"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0")
                .build()

            val response = client.newCall(request).execute()
            val bodyString = response.body?.string()

            if (response.isSuccessful && !bodyString.isNullOrBlank()) {
                val jsonArray = JSONArray(bodyString)
                val sentencesArray = jsonArray.optJSONArray(0)
                if (sentencesArray != null) {
                    val translatedSb = StringBuilder()
                    for (i in 0 until sentencesArray.length()) {
                        val sentence = sentencesArray.optJSONArray(i)
                        if (sentence != null && sentence.length() > 0) {
                            translatedSb.append(sentence.getString(0))
                        }
                    }
                    val result = translatedSb.toString().trim()
                    if (result.isNotEmpty()) {
                        cache[cleanText] = result
                        return@withContext result
                    }
                }
            }
        } catch (_: Exception) {
            // Fallback
        }

        cache[cleanText] = cleanText
        return@withContext cleanText
    }
}
