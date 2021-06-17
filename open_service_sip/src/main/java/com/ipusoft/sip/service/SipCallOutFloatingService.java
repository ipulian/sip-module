package com.ipusoft.sip.service;

import android.content.Intent;

import com.ipusoft.context.AppContext;
import com.ipusoft.context.BaseLifeCycleService;
import com.ipusoft.context.LiveDataBus;
import com.ipusoft.context.constant.LiveDataConstant;
import com.ipusoft.context.utils.GsonUtils;
import com.ipusoft.context.utils.StringUtils;
import com.ipusoft.sip.ITimerTask;
import com.ipusoft.sip.adapter.SipCallOutFloatingViewAdapter;
import com.ipusoft.sip.bean.SipCallOutInfoBean;
import com.ipusoft.context.constant.SipState;
import com.ipusoft.sip.constant.CallStatusCode;
import com.ipusoft.sip.iface.OnSipCallOutWindowClickListener;
import com.ipusoft.sip.manager.MediaPlayerManager;
import com.ipusoft.sip.manager.SipPhoneManager;
import com.ipusoft.sip.view.SipCallOutFloatingView;

import java.util.TimerTask;

/**
 * author : GWFan
 * time   : 5/31/21 6:14 PM
 * desc   :
 */
public class SipCallOutFloatingService extends BaseLifeCycleService implements
        OnSipCallOutWindowClickListener {
    private static final String TAG = "SipCallOutFloatingServi";
    private SipCallOutFloatingView mFloatingView;
    private SipCallOutFloatingViewAdapter sipAdapter;
    public static final String SIP_WINDOW_CUSTOMER_DATA = "sip_window_customer_data";
    private int i = 0;
    private ITimerTask task;

    @Override
    protected void onICreate() {
        mFloatingView = new SipCallOutFloatingView(this);
        mFloatingView.setOnClickListener(this);
        sipAdapter = new SipCallOutFloatingViewAdapter();
        mFloatingView.setAdapter(sipAdapter);
    }

    @Override
    protected void bindLiveData() {

        LiveDataBus.get().with(LiveDataConstant.WINDOW_SHOW_SIP_CALL, SipCallOutInfoBean.class)
                .observe(this, sipBean -> {
                    sipAdapter.updateData(sipBean);
                    mFloatingView.show();
                });

        LiveDataBus.get().with(LiveDataConstant.UPDATE_SIP_CALL_STATUS, SipState.class)
                .observe(this, sipCallStatus -> {
                    if (CallStatusCode.CODE_1 == sipCallStatus.getStatus()) {
                        MediaPlayerManager.playCallOutRing(AppContext.getAppContext());
                    } else if (CallStatusCode.CODE_3 == sipCallStatus.getStatus()
                            || CallStatusCode.CODE_4 == sipCallStatus.getStatus()) {
                        MediaPlayerManager.stopAndReleasePlay();
                    } else if (CallStatusCode.CODE_5 == sipCallStatus.getStatus()) {
                        try {
                            i = 0;
                            task = new ITimerTask(1000, new TimerTask() {
                                @Override
                                public void run() {
                                    sipAdapter.updateSipCallDuration(++i);
                                }
                            });
                            task.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (CallStatusCode.CODE_6 == sipCallStatus.getStatus()) {
                        if (task != null) {
                            task.stop();
                        }
                        MediaPlayerManager.playHungUpRing(AppContext.getAppContext(),
                                mp -> mFloatingView.dismiss());
                    }
                    sipAdapter.updateSipStatus(sipCallStatus);
                });
    }

    @Override
    protected void onIStartCommand(Intent intent, int flags, int startId) {
        String json = intent.getStringExtra(SIP_WINDOW_CUSTOMER_DATA);
        if (StringUtils.isNotEmpty(json)) {
            SipCallOutInfoBean sipCallOutInfoBean = GsonUtils.fromJson(json, SipCallOutInfoBean.class);
            sipAdapter.updateData(sipCallOutInfoBean);
        }
        mFloatingView.show();
    }

    @Override
    protected void onIDestroy() {
        mFloatingView.dismiss();
        mFloatingView = null;
    }

    @Override
    public void onDismiss() {
        mFloatingView.dismiss();
    }

    @Override
    public void hungUp() {
        SipPhoneManager.dropPhone();
        if (task != null) {
            task.stop();
        }
    }

    @Override
    public void dialDTMF(String number) {
        SipPhoneManager.dialDTMF(number);
    }
}