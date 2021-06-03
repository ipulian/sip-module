package com.ipusoft.siplibrary.manager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ipusoft.context.LiveDataBus;
import com.ipusoft.context.LiveDataConstant;
import com.ipusoft.context.ServiceManager;
import com.ipusoft.context.utils.GsonUtils;
import com.ipusoft.siplibrary.SipCacheApp;
import com.ipusoft.siplibrary.bean.SipCallOutInfoBean;
import com.ipusoft.siplibrary.service.SipCallOutFloatingService;

/**
 * author : GWFan
 * time   : 6/1/21 4:16 PM
 * desc   :
 */

public class SipFloatingViewIntentManager {
    private static final String TAG = "SipFloatingViewIntentMa";

    /**
     * 启动SIP呼叫的弹屏
     *
     * @param mContext
     */
    public static void startSipCallOutFloatingService(Context mContext) {
        SipCallOutInfoBean sipCallOutInfoBean = SipCacheApp.getSipCallOutInfoBean();
        Log.d(TAG, "startSipCallOutFloatingService: ------>" + GsonUtils.toJson(sipCallOutInfoBean));
        if (ServiceManager.isServiceRunning(SipCallOutFloatingService.class)) {
            LiveDataBus.get().with(LiveDataConstant.WINDOW_SHOW_SIP_CALL, SipCallOutInfoBean.class)
                    .postValue(sipCallOutInfoBean);
        } else {
            Intent intent = new Intent(mContext, SipCallOutFloatingService.class);
            intent.setAction("com.ipusoft.siplibrary.service.SipCallOutFloatingService");
            intent.putExtra(SipCallOutFloatingService.SIP_WINDOW_CUSTOMER_DATA, GsonUtils.toJson(sipCallOutInfoBean));
            mContext.startService(intent);
        }
    }
}
