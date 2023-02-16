package com.ipusoft.sip.ifaceimpl;

import android.util.Log;

import com.ipusoft.context.LiveDataBus;
import com.ipusoft.context.bean.SipResponse;
import com.ipusoft.context.component.ToastUtils;
import com.ipusoft.context.constant.LiveDataConstant;
import com.ipusoft.context.constant.SipState;
import com.ipusoft.context.iface.BaseSipStatusChangedListener;
import com.ipusoft.sip.constant.CallStatusCode;
import com.ipusoft.sip.constant.LoginCode;
import com.ipusoft.sip.constant.SipType;
import com.ipusoft.sip.manager.SipFloatingViewIntentManager;
import com.ipusoft.sip.manager.SipManager;
import com.ipusoft.utils.StringUtils;
import com.ipusoft.utils.ThreadUtils;

/**
 * author : GWFan
 * time   : 1/14/21 3:36 PM
 * desc   : SIP 状态变化的 实现类
 */

public abstract class OnSipStatusChangedListener implements BaseSipStatusChangedListener {
    private static final String TAG = "OnSipStatusChangedLis";

    @Override
    public abstract void onSipResponseError(SipResponse sipResponse);

    public abstract void onStartCall();

    public abstract void onEndCall(SipResponse sipResponse);

    /**
     * SIP登陆状态，0未登陆（掉线，登陆异常），1已登陆（正常在线状态）
     */
    public abstract void onSipLoginStatusChanged(int status);

    @Override
    public void onSipResponseSuccess(SipResponse sipResponse) {
        SipState status = SipState.STATUS_0;
        Log.d(TAG, "onSipResponseSuccess: --------------->" + sipResponse.getCode());
        if (StringUtils.equals(SipType.HTTP.getType(), sipResponse.getType())) {
            if (sipResponse.getCode() == LoginCode.CODE_M99) {
                onSipLoginStatusChanged(0);
            }
        } else if (StringUtils.equals(SipType.LOGIN.getType(), sipResponse.getType())) {
            switch (sipResponse.getCode()) {
                case LoginCode.CODE_1:
                    onSipLoginStatusChanged(1);
                    break;
                case LoginCode.CODE_M1:
                case LoginCode.CODE_M99:
                    onSipLoginStatusChanged(0);
                    break;
            }
        } else {
            switch (sipResponse.getCode()) {
                case CallStatusCode.CODE_1:
                    ThreadUtils.runOnUiThread(ToastUtils::dismiss);
                    Log.d(TAG, "onSipResponseSuccess: ----------<");
                    SipManager.getInstance().resetLoginCnt();
                    status = SipState.STATUS_1;
                    SipFloatingViewIntentManager.startSipCallOutFloatingService();
                    onStartCall();
                    break;
                case CallStatusCode.CODE_2:
                    status = SipState.STATUS_2;
                    //  SipFloatingViewIntentManager.startSipCallOutFloatingService();
                    break;
                case CallStatusCode.CODE_3:
                    status = SipState.STATUS_3;
                    SipFloatingViewIntentManager.startSipCallOutFloatingService();
                    break;
                case CallStatusCode.CODE_4:
                    status = SipState.STATUS_4;
                    //  SipFloatingViewIntentManager.startSipCallOutFloatingService();
                    break;
                case CallStatusCode.CODE_5:
                    status = SipState.STATUS_5;
                    //  SipFloatingViewIntentManager.startSipCallOutFloatingService();
                    break;
                case CallStatusCode.CODE_6:
                    status = SipState.STATUS_6;
                    // SipFloatingViewIntentManager.startSipCallOutFloatingService();
                    onEndCall(sipResponse);
                    break;
                default://其他状态（号码拦截，黑名单等），直接挂断
                    status = SipState.STATUS_6;
                    onEndCall(sipResponse);
                    break;
            }

            LiveDataBus.get().with(LiveDataConstant.UPDATE_SIP_CALL_STATUS, SipState.class).postValue(status);
        }
    }
}