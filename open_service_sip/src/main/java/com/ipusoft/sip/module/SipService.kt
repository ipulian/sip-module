package com.ipusoft.sip.module

import com.ipusoft.context.bean.base.HttpResponse
import com.ipusoft.http.manager.RetrofitManager
import com.ipusoft.sip.api.SipAPI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * author : GWFan
 * time   : 9/14/20 2:54 PM
 * desc   :
 */

class SipService {
    companion object {

        /**
         * sip心跳
         */
        fun sipPing(
            headers: Map<String, String>,
            params: String,
            observer: Observer<HttpResponse>
        ) {
            RetrofitManager.getInstance().retrofit.create(SipAPI::class.java)
                .sipPing(headers, RetrofitManager.getInstance().getRequestBody(params))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
        }
    }
}