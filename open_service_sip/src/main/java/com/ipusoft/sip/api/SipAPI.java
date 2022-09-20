package com.ipusoft.sip.api;

import com.ipusoft.context.bean.base.HttpResponse;
import com.ipusoft.http.HttpConstant;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * @author : GWFan
 * time   : 2022/7/16 10:57
 * desc   :
 */

public interface SipAPI {
    /**
     * Sip心跳
     *
     * @param headers
     * @return
     */
    @POST("/sip/js/call/ping")
    @Headers({HttpConstant.OPEN_URL})
    Observable<HttpResponse> sipPing(@HeaderMap Map<String, String> headers, @Body RequestBody requestBody);
}
