package com.zztx.browse.data.network

import com.zztx.browse.data.entity.Bookmark
import com.zztx.browse.data.entity.History
import com.zztx.browse.data.entity.Password
import com.zztx.browse.data.entity.User
import com.zztx.browse.data.entity.LoginRequest
import com.zztx.browse.data.entity.RegisterRequest
import com.zztx.browse.data.entity.AuthResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/refresh")
    suspend fun refreshToken(@Header("Authorization") token: String): AuthResponse

    @GET("user")
    suspend fun getUserInfo(@Header("Authorization") token: String): User

    @GET("bookmarks")
    suspend fun getBookmarks(@Header("Authorization") token: String): List<Bookmark>

    @POST("bookmarks")
    suspend fun createBookmark(
        @Header("Authorization") token: String,
        @Body bookmark: Bookmark
    ): Bookmark

    @PUT("bookmarks/{id}")
    suspend fun updateBookmark(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body bookmark: Bookmark
    ): Bookmark

    @DELETE("bookmarks/{id}")
    suspend fun deleteBookmark(
        @Header("Authorization") token: String,
        @Path("id") id: String
    )

    @GET("history")
    suspend fun getHistory(@Header("Authorization") token: String): List<History>

    @POST("history")
    suspend fun syncHistory(
        @Header("Authorization") token: String,
        @Body history: List<History>
    ): List<History>

    @GET("passwords")
    suspend fun getPasswords(@Header("Authorization") token: String): List<Password>

    @POST("passwords")
    suspend fun createPassword(
        @Header("Authorization") token: String,
        @Body password: Password
    ): Password

    @PUT("passwords/{id}")
    suspend fun updatePassword(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body password: Password
    ): Password

    @DELETE("passwords/{id}")
    suspend fun deletePassword(
        @Header("Authorization") token: String,
        @Path("id") id: String
    )
}