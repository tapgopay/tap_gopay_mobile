package com.example.tapgopay.data

import com.example.tapgopay.remote.MessageResponse
import com.google.gson.Gson
import retrofit2.Response

fun <T> Response<T>.extractErrorMessage(): Map<String, String>? {
    val errorBody: String = this.errorBody()?.string() ?: return null
    val messageResponse: MessageResponse =
        Gson().fromJson(errorBody, MessageResponse::class.java) ?: return null
    return messageResponse.errors
}