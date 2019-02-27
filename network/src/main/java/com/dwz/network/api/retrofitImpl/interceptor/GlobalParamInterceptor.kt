package com.dwz.network.api.retrofitImpl.interceptor

import java.util.HashMap

import okhttp3.Interceptor


abstract class GlobalParamInterceptor : Interceptor {
    protected var mParams: MutableMap<String, String> = HashMap()

    var params: MutableMap<String, String>
        get() = mParams
        set(params) {
            this.mParams = params
        }

    fun replace(params: Map<String, String>) {
        for (key in mParams.keys) {
            if (params.containsKey(key))
                params[key]?.let { mParams.put(key, it) }
        }
    }
}
