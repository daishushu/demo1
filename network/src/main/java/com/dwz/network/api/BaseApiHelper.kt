package com.dwz.network.api

import java.util.*


abstract class BaseApiHelper<out A> : ApiHelper<A> {

    override val basicParams: MutableMap<String, String>
        get() {
            return HashMap()
        }
}
