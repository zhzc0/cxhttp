package com.zicheng.net.cxhttp.request

import com.zicheng.net.cxhttp.CxHttpHelper
import com.zicheng.net.cxhttp.converter.RequestBodyConverter
import java.io.File


class Request internal constructor(val url: String, val method: String) {

    private var _tag: Any? = null
    private var _headers: MutableMap<String, String>? = null
    private var _params: MutableMap<String, Any>? = null
    private var _body: Body<*>? = null
    val tag: Any?
        get() = _tag
    val headers: Map<String, String>?
        get() = _headers
    val params: Map<String, Any>?
        get() = _params
    internal val body: Body<*>?
        get() = _body
    var mergeParamsToUrl = method == Method.GET.value
    internal var bodyConverter: RequestBodyConverter = CxHttpHelper.converter
    internal var onProgress: ((Long, Long) -> Unit)? = null

    fun tag(tag: Any?) {
        _tag = tag
    }

    fun header(name: String, value: String) {
        if (_headers == null) {
            _headers = HashMap()
        }
        _headers!![name] = value
    }

    fun headers(headers: Map<String, String>) {
        if (_headers == null) {
            _headers = HashMap()
        }
        _headers!!.putAll(headers)
    }

    fun param(key: String, value: Any) {
        if (_params == null) {
            _params = HashMap()
        }
        _params!![key] = value
    }

    fun params(params: Map<String, Any>) {
        if (_params == null) {
            _params = HashMap()
        }
        _params!!.putAll(params)
    }

    fun setBody(body: String, contentType: String = CxHttpHelper.CONTENT_TYPE_JSON){
        _body = StringBody(body, contentType)
    }

    fun <T> setBody(body: T, tClass: Class<T>, contentType: String = CxHttpHelper.CONTENT_TYPE_JSON,
                    bodyConverter: RequestBodyConverter? = null){
        _body = EntityBody(body, tClass, contentType)
        bodyConverter?.let { this.bodyConverter = it }
    }

    fun setBody(body: File, contentType: String = CxHttpHelper.CONTENT_TYPE_OCTET_STREAM){
        _body = FileBody(body, contentType)
    }

    fun setBody(body: ByteArray, contentType: String = CxHttpHelper.CONTENT_TYPE_OCTET_STREAM){
        _body = ByteArrayBody(body, contentType)
    }

    /**
     * 设置RequestBody转换器
     * @param bodyConverter RequestBodyConverter：可自定义RequestBody，默认使用CxHttpHelper.converter
     *
     * */
    fun setBodyConverter(bodyConverter: RequestBodyConverter){
        this.bodyConverter = bodyConverter
    }

    fun setFormBody(block: FormBody.() -> Unit = {}){
        FormBody(mutableListOf(), CxHttpHelper.CONTENT_TYPE_FORM).block()
    }

    fun setMultipartBody(type: String = CxHttpHelper.CONTENT_TYPE_MULTIPART_FORM, block: MultipartBody.() -> Unit){
        MultipartBody(mutableListOf(), type).block()
    }

    /**
     * 上传进度监听
     * @param onProgress (Long, Long) -> Unit：totalLength, currentLength
     *
     * */
    fun setOnProgressListener(onProgress: (Long, Long) -> Unit){
        this.onProgress = onProgress
    }

    fun containsHeader(key: String): Boolean {
        return _headers?.containsKey(key) ?: false
    }

    fun containsParam(key: String): Boolean {
        return _params?.containsKey(key) ?: false
    }

    override fun toString(): String {
        val strBuilder = StringBuilder()
        strBuilder.append("url='").append(url).append('\'')
        if (_tag != null) {
            strBuilder.append(", tags=").append(_tag)
        }
        if (_headers != null) {
            strBuilder.append(", headers=").append(_headers)
        }
        if (_params != null) {
            strBuilder.append(", params=").append(_params)
        }
        return strBuilder.toString()
    }

    internal enum class Method(val value: String) {
        GET("GET"), HEAD("HEAD"),
        POST("POST"), DELETE("DELETE"), PUT("PUT"), PATCH("PATCH");
    }
}