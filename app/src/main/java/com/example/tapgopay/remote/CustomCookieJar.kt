package com.example.tapgopay.remote

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.tapgopay.MainActivity
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class CustomCookieJar() : CookieJar {
    private val context: Context = MainActivity.instance.applicationContext
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE)

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookiesSet: Set<String>? = sharedPreferences.getStringSet(url.host, emptySet())
        return cookiesSet?.mapNotNull { Cookie.parse(url, it) } ?: listOf()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val cookiesSet = cookies.map { it.toString() }.toSet()
        sharedPreferences.edit {
            putStringSet(url.host, cookiesSet)
        }
    }

}