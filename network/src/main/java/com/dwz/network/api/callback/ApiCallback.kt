package com.dwz.network.api.callback


interface ApiCallback<in T> {

    /**
     * 请求成功，且业务状态正确

     * @param result 返回数据泛型对象
     */
    fun onSucceed(result: T)

    /**
     * 请求成功，业务状态不正确

     * @param code
     * *
     * @param msg
     */
    fun onFail(code: Int, msg: String)

}

