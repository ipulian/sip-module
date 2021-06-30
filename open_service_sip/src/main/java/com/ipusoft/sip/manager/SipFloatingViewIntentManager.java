package com.ipusoft.sip.manager;

import android.content.Intent;

import com.ipusoft.context.AppContext;
import com.ipusoft.context.LiveDataBus;
import com.ipusoft.context.ServiceManager;
import com.ipusoft.context.constant.LiveDataConstant;
import com.ipusoft.context.utils.GsonUtils;
import com.ipusoft.sip.SipCacheApp;
import com.ipusoft.sip.bean.SipCallOutInfoBean;
import com.ipusoft.sip.service.SipCallOutFloatingService;

/**
 * author : GWFan
 * time   : 6/1/21 4:16 PM
 * desc   :
 */

public class SipFloatingViewIntentManager {
    private static final String TAG = "SipFloatingViewIntentMa";

    /**
     * 启动SIP呼叫的弹屏
     */
    public static void startSipCallOutFloatingService() {
        SipCallOutInfoBean sipCallOutInfoBean = SipCacheApp.getSipCallOutInfoBean();
        if (ServiceManager.isServiceRunning(SipCallOutFloatingService.class)) {
            LiveDataBus.get().with(LiveDataConstant.WINDOW_SHOW_SIP_CALL, SipCallOutInfoBean.class)
                    .postValue(sipCallOutInfoBean);
        } else {
            Intent intent = new Intent(AppContext.getAppContext(), SipCallOutFloatingService.class);
            intent.setAction("com.ipusoft.siplibrary.service.SipCallOutFloatingService");
            intent.putExtra(LiveDataConstant.WINDOW_SHOW_SIP_CALL, GsonUtils.toJson(sipCallOutInfoBean));
            AppContext.getAppContext().startService(intent);
        }
    }
}
