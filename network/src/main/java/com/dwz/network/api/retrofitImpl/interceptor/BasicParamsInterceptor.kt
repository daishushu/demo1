package com.dwz.network.api.retrofitImpl.interceptor


import com.dwz.network.api.utils.MD5
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import java.io.IOException
import java.net.URLDecoder
import java.util.*



abstract class BasicParamsInterceptor : Interceptor {

    protected abstract val params: MutableMap<String, String>?

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val requestBuilder = request.newBuilder()
        var postBodyString = bodyToString(request.body())
        val mParams = params
        if (!postBodyString.isEmpty() && mParams != null) {
            val paramsArr = postBodyString.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (aParamsArr in paramsArr) {
                val para = aParamsArr.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (para.isNotEmpty()) {
                    mParams.put(para[0], URLDecoder.decode(if (para.size == 1) "" else para[1], "UTF-8"))
                }
            }
        }
        val builder = FormBody.Builder()
        if (mParams != null) {
            for (key in mParams.keys) {
                builder.add(key, mParams[key])
            }
        }
        val formBody = builder.build()
        //防刷鉴权
        postBodyString = sortParamString(bodyToString(formBody))
        requestBuilder.addHeader("Content-Link", MD5.GetMD5Code(String.format("%s&%s", URLDecoder.decode(postBodyString, "UTF-8"), "E1.A19fg.M")))
        requestBuilder.addHeader("Cache-Encoding", "gbk")
        request = requestBuilder
                .post(formBody)
                .build()
        return chain.proceed(request)
    }

    /**
     * 按key的顺序重新排列参数

     * @param bodyString
     * *
     * @return
     */
    private fun sortParamString(bodyString: String): String {
        val sb = StringBuilder()
        if (bodyString.isNotEmpty()) {
            val paramsArr = bodyString.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            Arrays.sort(paramsArr)
            for (i in paramsArr.indices) {
                sb.append(paramsArr[i])
                if (paramsArr.isNotEmpty() && i < paramsArr.size - 1) {
                    sb.append("&")
                }
            }
            return sb.toString()
        }
        return bodyString
    }

    private fun bodyToString(request: RequestBody?): String {
        try {
            val buffer = Buffer()
            if (request != null)
                request.writeTo(buffer)
            else
                return ""
            return buffer.readUtf8()
        } catch (e: IOException) {
            return "did not work"
        }

    }
}
