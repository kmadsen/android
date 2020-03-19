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


class LocalhostFilesApi {

    interface LocalhostFiles {
        @GET("json_files")
        fun drives(): Call<LocalhostFilesResponse>

        @GET("json_files/{filename}")
        fun data(@Path("filename") filename: String): Call<String>

        @GET("json_files/{filename}")
        fun dataJson(@Path("filename") filename: String): Call<JsonFileDataResponse>
    }

    data class LocalhostFilesResponse(
        val json_files: List<String>
    )

    data class JsonFileDataResponse(
        val version: String
    )

    suspend fun requestHistory(ipAddress: String): List<String> = suspendCoroutine { cont ->
        val retrofit = Retrofit.Builder()
            .baseUrl("http://$ipAddress:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val historyDrives = retrofit.create(LocalhostFiles::class.java)
        historyDrives.drives().enqueue(object : Callback<LocalhostFilesResponse> {
            override fun onFailure(call: Call<LocalhostFilesResponse>, t: Throwable) {
                L.e(t, "requestData onFailure")
                cont.resume(emptyList())
            }

            override fun onResponse(call: Call<LocalhostFilesResponse>, response: Response<LocalhostFilesResponse>) {
                L.i("requestData onResponse")
                val drives = if (response.isSuccessful) {
                    response.body()?.json_files ?: emptyList()
                } else {
                    emptyList()
                }
                cont.resume(drives)
            }
        })
    }

    suspend fun requestData(ipAddress: String, filename: String): String = suspendCoroutine { cont ->
        val retrofit = Retrofit.Builder()
            .baseUrl("http://$ipAddress:8000")
            .addConverterFactory(ToStringConverterFactory())
            .build()

        val historyDrives = retrofit.create(LocalhostFiles::class.java)
        historyDrives.data(filename).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                L.e(t, "requestData onFailure")
                cont.resume("")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                L.i("requestData onResponse")
                val data = if (response.isSuccessful) {
                    response.body() ?: ""
                } else {
                    ""
                }
                cont.resume(data)
            }
        })
    }

    suspend fun requestDataJson(ipAddress: String, filename: String): JsonFileDataResponse? = suspendCoroutine { cont ->
        val retrofit = Retrofit.Builder()
            .baseUrl("http://$ipAddress:8000")
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