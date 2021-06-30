package com.ipusoft.sip.ifaceimpl;

import com.ipusoft.context.LiveDataBus;
import com.ipusoft.context.constant.LiveDataConstant;
import com.ipusoft.context.bean.SipResponse;
import com.ipusoft.context.component.ToastUtils;
import com.ipusoft.context.iface.BaseSipStatusChangedListener;
import com.ipusoft.context.constant.SipState;
import com.ipusoft.context.utils.ThreadUtils;
import com.ipusoft.sip.constant.CallStatusCode;
import com.ipusoft.sip.manager.SipFloatingViewIntentManager;
import com.ipusoft.sip.manager.SipManager;

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

    public abstract void onEndCall();

    @Override
    public void onSipResponseSuccess(SipResponse sipResponse) {
        SipState status = SipState.STATUS_0;
        switch (sipResponse.getCode()) {
            case CallStatusCode.CODE_1:
                ThreadUtils.runOnUiThread(ToastUtils::dismiss);
                SipFloatingViewIntentManager.startSipCallOutFloatingService();
                SipManager.getInstance().resetLoginCnt();
                status = SipState.STATUS_1;
                onStartCall();
                break;
            case CallStatusCode.CODE_3:
                status = SipState.STATUS_3;
                break;
            case CallStatusCode.CODE_4:
                status = SipState.STATUS_4;
                break;
            case CallStatusCode.CODE_5:
                status = SipState.STATUS_5;
                break;
            case CallStatusCode.CODE_6:
                status = SipState.STATUS_6;
                onEndCall();
                break;
            default:
                break;
        }
        LiveDataBus.get().with(LiveDataConstant.UPDATE_SIP_CALL_STATUS, SipState.class).postValue(status);
    }
}