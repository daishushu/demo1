package com.dwz.network.api.base


open class BaseResult {
    var code = -1
    var msg: String = ""
    val isSuccess: Boolean = code == 0
}
