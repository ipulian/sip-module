package com.ipusoft.siplibrary.ifaceimpl;

import com.ipusoft.context.AppContext;
import com.ipusoft.context.LiveDataBus;
import com.ipusoft.context.LiveDataConstant;
import com.ipusoft.context.bean.SipResponse;
import com.ipusoft.context.component.ToastUtils;
import com.ipusoft.context.iface.BaseSipStatusChangedListener;
import com.ipusoft.siplibrary.bean.SipCallStatus;
import com.ipusoft.siplibrary.constant.CallStatusCode;
import com.ipusoft.siplibrary.manager.SipFloatingViewIntentManager;

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
        SipCallStatus status = SipCallStatus.STATUS_0;
        switch (sipResponse.getCode()) {
            case CallStatusCode.CODE_1:
                ToastUtils.dismiss();
                SipFloatingViewIntentManager.startSipCallOutFloatingService(AppContext.getActivityContext());
                status = SipCallStatus.STATUS_1;
                onStartCall();
                break;
            case CallStatusCode.CODE_3:
                status = SipCallStatus.STATUS_3;
                break;
            case CallStatusCode.CODE_4:
                status = SipCallStatus.STATUS_4;
                break;
            case CallStatusCode.CODE_5:
                status = SipCallStatus.STATUS_5;
                break;
            case CallStatusCode.CODE_6:
                status = SipCallStatus.STATUS_6;
                onEndCall();
                break;
            default:
                break;
        }
        LiveDataBus.get().with(LiveDataConstant.UPDATE_SIP_CALL_STATUS, SipCallStatus.class).postValue(status);
    }
}