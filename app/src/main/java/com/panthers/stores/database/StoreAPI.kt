package com.panthers.stores.database

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class StoreAPI constructor(context: Context) {
    companion object {
        @Volatile
        private var instance: StoreAPI? = null
        fun getInstance(context: Context) = instance ?: synchronized(this) {
            if (instance != null) instance else StoreAPI(context).also { instance = it }
        }
    }

    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(request: Request<T>) {
        requestQueue.add(request)
    }
}