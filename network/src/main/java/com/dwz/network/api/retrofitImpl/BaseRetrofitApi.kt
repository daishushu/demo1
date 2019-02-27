package com.dwz.network.api.retrofitImpl

import com.dwz.network.api.base.BaseResult
import com.dwz.network.api.callback.ApiCallback
import com.dwz.network.api.corverter.gson.GsonConverterFactory
import com.dwz.network.api.retrofitImpl.interceptor.BasicParamsInterceptor
import com.dwz.network.api.retrofitImpl.interceptor.HttpLoggingInterceptor
import com.dwz.network.api.DoBasicParams
import com.dwz.network.api.HttpsUtils
import okhttp3.OkHttpClient
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


/**
 * 类名：BaseRetrofitApi
 * 描述：
 * 创建人：朱大森 on 2016/9/11 00:56.
 * 修改人：
 * 修改时间：
 * 修改备注：
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
open class BaseRetrofitApi(baseUrl: String, doBasicParams: DoBasicParams, sslParams: HttpsUtils.SSLParams =  HttpsUtils.getSslSocketFactory(HttpsUtils.UnSafeTrustManager, null, null, null)):AnkoLogger {
    protected var mRetrofit: Retrofit

    init {
        if (okHttpClient == null) {
            val basicParamsInterceptor = object : BasicParamsInterceptor() {
                override val params: MutableMap<String, String>?
                    get() = doBasicParams.basicParams
            }
            val builder = OkHttpClient().newBuilder()
            builder.addInterceptor(basicParamsInterceptor)
            builder.addInterceptor(httpLoggingInterceptor)
            try {
                builder.sslSocketFactory(
                        sslParams.sSLSocketFactory,
                        sslParams.trustManager
                )
                builder.hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier)
            } catch (e: Exception) {
            }

            okHttpClient = builder.build()
        }
        mRetrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient!!)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    /**
     * @param <T>
    </T> */
    open class RetrofitApiCallback<T : BaseResult>(open protected val mApiCallback: ApiCallback<T>) : Callback<T> {

        override fun onResponse(call: Call<T>, response: Response<T>) {
            val code = response.code()
            if (code in 200..299) { //服务器请求返回, 有合理响应
                val bizCode = response.body()!!.code
                if (bizCode == 0) {
                    mApiCallback.onSucceed(response.body()!!)
                } else {
                    var msg = response.body()!!.msg
                    mApiCallback.onFail(bizCode, msg)
                }
            } else {
                onFailure(call, RuntimeException("response error,detail = " + response.raw().toString()))
            }
        }

        override fun onFailure(call: Call<T>, throwable: Throwable) { //网络问题回调
            if (throwable is SocketTimeoutException) {
                mApiCallback.onFail(0x99,"您的网络不给力，请稍后再试")
            } else if (throwable is ConnectException) {
                mApiCallback.onFail(0x99," 您当前的网络不通，请在网络恢复后再试")
            } else if (throwable is UnknownHostException) {
                mApiCallback.onFail(0x99," 您当前的网络不通，请在网络恢复后再试")
            } else if (throwable is RuntimeException) {
                mApiCallback.onFail(0x99,"当前服务异常，请稍后再试")
            } else {
                mApiCallback.onFail(0x99,"当前服务异常，请稍后再试")
            }
        }
    }
    /**
     * @param <T>
    </T> */
    open class RetrofitApiLamdba<T : BaseResult>(val success:T?.()->Unit,val error:String?.()->Unit) : Callback<T> {

        override fun onResponse(call: Call<T>, response: Response<T>) {
            val code = response.code()
            if (code in 200..299) { //服务器请求返回, 有合理响应
                val bizCode = response.body()!!.code
                if (bizCode == 0&&response.body()?.isSuccess == true) {
                        response.body().success()
                } else {
                    val msg = response.body()!!.msg
                    response.body()?.msg.error()
                }
            } else {
                onFailure(call, RuntimeException("response error,detail = " + response.raw().toString()))
            }
        }

        override fun onFailure(call: Call<T>, throwable: Throwable) { //网络问题回调
            when (throwable) {
                is SocketTimeoutException -> "您的网络不给力，请稍后再试".error()
                is ConnectException -> "您当前的网络不通，请在网络恢复后再试".error()
                is UnknownHostException -> "您当前的网络不通，请在网络恢复后再试".error()
                else -> "当前服务异常，请稍后再试".error()
            }
        }
    }
    /**
     * Http日志拦截器

     * @return
     */
    private val httpLoggingInterceptor: HttpLoggingInterceptor
        get() {
            val httpLoggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    error { message }
                }
            })
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            return httpLoggingInterceptor
        }

    companion object {
        private var okHttpClient: OkHttpClient? = null
    }
}
