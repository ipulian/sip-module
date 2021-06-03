package com.ipusoft.siplibrary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ipusoft.context.AppContext;
import com.ipusoft.context.utils.VibrateUtils;
import com.ipusoft.siplibrary.R;
import com.ipusoft.siplibrary.constant.DialKey;
import com.ipusoft.siplibrary.iface.OnSipWindowDialPanClickListener;
import com.ipusoft.siplibrary.manager.MediaPlayerManager;
import com.ipusoft.siplibrary.manager.SipManager;

import java.util.List;
import java.util.Map;

/**
 * author : GWFan
 * time   : 6/1/21 3:29 PM
 * desc   : SIP外呼拨号盘
 */

public class SipWindowDialPanView extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "SipWindowDialPanView";
    private OnSipWindowDialPanClickListener listener;

    public SipWindowDialPanView(Context context) {
        super(context);
        initView(context);
    }

    public SipWindowDialPanView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void setOnSipDialPanClickListener(OnSipWindowDialPanClickListener listener) {
        this.listener = listener;
    }

    private void initView(Context context) {
        View mView = LayoutInflater.from(context).inflate(R.layout.sip_dial_pan_root, this);
        LinearLayout llPanRoot = mView.findViewById(R.id.ll_pan_root);
        llPanRoot.removeAllViews();
        LinearLayout llBtnGroup, llBtn;
        View btnDial;
        TextView tvText, tvSubText;
        List<Map<String, String>> btnList = DialKey.getKeys(2);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
        for (Map<String, String> map : btnList) {
            View panLinearLayout = LayoutInflater.from(context).inflate(R.layout.button_group_dial, null);
            llBtnGroup = panLinearLayout.findViewById(R.id.ll_button_group);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                btnDial = LayoutInflater.from(context).inflate(R.layout.button_dial2, null);
                llBtn = btnDial.findViewById(R.id.ll_btn);
                tvText = btnDial.findViewById(R.id.tv_text);
                tvSubText = btnDial.findViewById(R.id.tv_sub_text);
                tvText.setText(entry.getKey());
                tvSubText.setText(entry.getValue());
                llBtn.setTag(entry.getKey());
                llBtn.setOnClickListener(this);
                llBtnGroup.addView(btnDial, layoutParams);
            }
            llPanRoot.addView(panLinearLayout);
        }
    }

    @Override
    public void onClick(View v) {
        VibrateUtils.vibrate(30);
        String tag = (String) v.getTag();
        MediaPlayerManager.playPressTipVoice(AppContext.getAppContext(), DialKey.getTipVoiceByKey(tag));
        SipManager.getInstance().dialDTMF(tag);
        if (listener != null) {
            listener.onSipDialPanClick(tag);
        }
    }
}
