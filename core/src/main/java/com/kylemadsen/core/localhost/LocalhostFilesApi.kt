package com.kylemadsen.core.localhost

import com.google.gson.GsonBuilder
import com.kylemadsen.core.logger.L
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class FilePath(
    val title: String,
    val path: String
)

data class JsonFileDataResponse(
    val version: String
)

class LocalhostFilesApi {

    interface LocalhostFiles {
        @GET("index.json")
        fun drives(): Call<List<FilePath>>

        @GET("json-files/{filename}")
        fun data(@Path("filename") filename: String): Call<String>

        @GET("json-files/{filename}")
        fun dataJson(@Path("filename") filename: String): Call<JsonFileDataResponse>
    }

    suspend fun requestHistory(): List<FilePath> = suspendCoroutine { cont ->
        val retrofit = Retrofit.Builder()
            .baseUrl("https://kmadsen.github.io/android/localhost-fileshare/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val historyDrives = retrofit.create(LocalhostFiles::class.java)
        historyDrives.drives().enqueue(object : Callback<List<FilePath>> {
            override fun onFailure(call: Call<List<FilePath>>, t: Throwable) {
                L.e(t, "requestHistory onFailure")
                cont.resume(emptyList())
            }

            override fun onResponse(call: Call<List<FilePath>>, response: Response<List<FilePath>>) {
                L.i("requestHistory onResponse")
                val drives = if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    emptyList()
                }
                cont.resume(drives)
            }
        })
    }

    suspend fun requestDataJson(filename: String): JsonFileDataResponse? = suspendCoroutine { cont ->
        val retrofit = Retrofit.Builder()
            .baseUrl("https://kmadsen.github.io/android/localhost-fileshare/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()

        val historyDrives = retrofit.create(LocalhostFiles::class.java)
        historyDrives.dataJson(filename).enqueue(object : Callback<JsonFileDataResponse> {
            override fun onFailure(call: Call<JsonFileDataResponse>, t: Throwable) {
                L.e(t, "requestData onFailure")
                cont.resume(null)
            }

            override fun onResponse(call: Call<JsonFileDataResponse>, response: Response<JsonFileDataResponse>) {
                L.i("requestData onResponse")
                val data = if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
                cont.resume(data)
            }
        })
    }
}
