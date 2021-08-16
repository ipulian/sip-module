package com.ipusoft.sip.service;

import android.content.Intent;
import android.util.Log;

import com.ipusoft.context.AppContext;
import com.ipusoft.context.BaseLifeCycleService;
import com.ipusoft.context.LiveDataBus;
import com.ipusoft.context.component.ToastUtils;
import com.ipusoft.context.constant.LiveDataConstant;
import com.ipusoft.context.constant.SipState;
import com.ipusoft.utils.GsonUtils;
import com.ipusoft.utils.StringUtils;
import com.ipusoft.utils.ThreadUtils;
import com.ipusoft.sip.ITimerTask;
import com.ipusoft.sip.adapter.SipPhoneFloatingViewAdapter;
import com.ipusoft.sip.bean.SipCallOutInfoBean;
import com.ipusoft.sip.constant.CallStatusCode;
import com.ipusoft.sip.iface.OnSipCallOutWindowClickListener;
import com.ipusoft.sip.manager.MediaPlayerManager;
import com.ipusoft.sip.manager.SipPhoneManager;
import com.ipusoft.sip.view.SipPhoneFloatingView;

import java.util.TimerTask;

/**
 * author : GWFan
 * time   : 5/31/21 6:14 PM
 * desc   :
 */
public class SipPhoneFloatingService extends BaseLifeCycleService implements OnSipCallOutWindowClickListener {
    private static final String TAG = "SipCallOutFloatingServi";
    /**
     * SIP外呼视图
     */
    private SipPhoneFloatingView mFloatingView;

    private SipPhoneFloatingViewAdapter sipAdapter;

    private int i = 0;
    private ITimerTask task;

    @Override
    protected void onICreate() {
        mFloatingView = new SipPhoneFloatingView(this);
        mFloatingView.setOnClickListener(this);
        sipAdapter = new SipPhoneFloatingViewAdapter();
        mFloatingView.setAdapter(sipAdapter);
    }

    @Override
    protected void bindLiveData() {

        LiveDataBus.get().with(LiveDataConstant.WINDOW_SHOW_SIP_CALL, SipCallOutInfoBean.class)
                .observe(this, sipBean -> {
                    //Log.d(TAG, "onIStartCommand: ----------->2");
                    sipAdapter.updateData(sipBean);
                    mFloatingView.show(sipBean.getSipPhoneType());
                });

        LiveDataBus.get().with(LiveDataConstant.UPDATE_SIP_CALL_STATUS, SipState.class)
                .observe(this, sipCallStatus -> {
                    ToastUtils.dismiss();
                    if (CallStatusCode.CODE_1 == sipCallStatus.getStatus()) {
                        MediaPlayerManager.playCallOutRing(AppContext.getAppContext());
                    } else if (CallStatusCode.CODE_2 == sipCallStatus.getStatus()) {
                        MediaPlayerManager.playCallOutRing(AppContext.getAppContext());
                    } else if (CallStatusCode.CODE_3 == sipCallStatus.getStatus()) {
                        MediaPlayerManager.stopAndReleasePlay();
                    } else if (CallStatusCode.CODE_4 == sipCallStatus.getStatus()) {
                        /*
                         * 部分线路，没有CallStatusCode.CODE_3
                         */
                        MediaPlayerManager.stopAndReleasePlay();
                    } else if (CallStatusCode.CODE_5 == sipCallStatus.getStatus()) {
                        try {
                            i = 0;
                            task = new ITimerTask(1000, 1000, new TimerTask() {
                                @Override
                                public void run() {
                                    ThreadUtils.runOnUiThread(() -> sipAdapter.updateSipCallDuration(++i));
                                }
                            });
                            task.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (CallStatusCode.CODE_6 == sipCallStatus.getStatus()) {
                        try {
                            if (task != null) {
                                task.stop();
                            }
                            MediaPlayerManager.playHungUpRing(AppContext.getAppContext(),
                                    mp -> mFloatingView.dismiss());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (CallStatusCode.CODE_5 != sipCallStatus.getStatus()) {
                        sipAdapter.updateSipStatus(sipCallStatus);
                    }
                    Log.d(TAG, "onIStartCommand: ----------->3--->" + sipCallStatus);
                });
    }

    @Override
    protected void onIStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String json = intent.getStringExtra(LiveDataConstant.WINDOW_SHOW_SIP_CALL);
            if (StringUtils.isNotEmpty(json)) {
                SipCallOutInfoBean sipCallOutInfoBean = GsonUtils.fromJson(json, SipCallOutInfoBean.class);
                Log.d(TAG, "onIStartCommand: ----------->1");
                sipAdapter.updateData(sipCallOutInfoBean);
                mFloatingView.show(sipCallOutInfoBean.getSipPhoneType());
            }
        }
    }

    @Override
    protected void onIDestroy() {
        if (mFloatingView != null) {
            mFloatingView.dismiss();
            mFloatingView = null;
        }
    }

    @Override
    public void onDismiss() {
        if (mFloatingView != null) {
            mFloatingView.dismiss();
        }
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

    @Override
    public void answerCall() {
        SipPhoneManager.answerCall();
    }
}
