package com.dwz.network.api


interface ApiHelper<out A> : DoBasicParams {
    val api: A
}
