package com.ipusoft.sip.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ipusoft.context.AppContext;
import com.ipusoft.context.component.WrapLinearLayout;
import com.ipusoft.utils.DateTimeUtils;
import com.ipusoft.utils.ResourceUtils;
import com.ipusoft.utils.SizeUtils;
import com.ipusoft.utils.StringUtils;
import com.ipusoft.sip.R;
import com.ipusoft.sip.bean.SipCallOutInfoBean;
import com.ipusoft.context.constant.SipState;
import com.ipusoft.sip.view.SipPhoneFloatingView;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * author : GWFan
 * time   : 5/31/21 6:25 PM
 * desc   :
 */

public class SipPhoneFloatingViewAdapter {
    private SipCallOutInfoBean bean;
    private SipPhoneFloatingView mView;
    private LinearLayout llLabel;
    private TextView tvStatus;

    public void updateData(SipCallOutInfoBean sipCallOutInfoBean) {
        this.bean = sipCallOutInfoBean;
        updateView(mView);
    }

    public void updateSipStatus(SipState sipCallStatus) {
        if (tvStatus != null) {
            tvStatus.setText(sipCallStatus.getStr());
        }
    }

    public void updateSipCallDuration(int duration) {
        if (tvStatus != null) {
            tvStatus.setText(DateTimeUtils.formatVideoDuration2(duration));
        }
    }

    public void updateView(SipPhoneFloatingView mWindowView) {
        this.mView = mWindowView;

        if (mWindowView != null) {
            llLabel = mView.findViewById(R.id.ll_label);

            initCommonInfoView(bean);

            initDetailsView(bean);
        }
    }

    /**
     * 初始化通用数据的View
     *
     * @param bean
     */
    private void initCommonInfoView(SipCallOutInfoBean bean) {
        if (bean != null) {
            TextView tvPhone = mView.findViewById(R.id.tv_phone);
            TextView tvPhoneArea = mView.findViewById(R.id.tv_phone_area);
            tvStatus = mView.findViewById(R.id.tv_status);

            if (StringUtils.isNotEmpty(bean.getPhoneArea())) {
                tvPhoneArea.setVisibility(View.VISIBLE);
                tvPhoneArea.setText(bean.getPhoneArea());
            } else {
                tvPhoneArea.setVisibility(View.GONE);
            }

            tvStatus.setText(bean.getSipStatus());

            String cPhone = bean.getPhone();
            if (StringUtils.isEmpty(cPhone)) {
                cPhone = "未知号码";
            } else {
                if (cPhone.length() > 7) {
                    cPhone = cPhone.substring(0, 3) + " " + cPhone.substring(3, 7) + " " + cPhone.substring(7);
                }
            }
            tvPhone.setText(cPhone);
        }
    }

    private void initDetailsView(SipCallOutInfoBean bean) {
        if (bean != null) {
            TextView tvName = mView.findViewById(R.id.tv_name);
            WrapLinearLayout wllLabel = mView.findViewById(R.id.wll_label);
            LinearLayout llItemRoot = mView.findViewById(R.id.ll_item_root);
            String str = bean.getName();
            if (StringUtils.isNotEmpty(str)) {
                if (StringUtils.isNotEmpty(bean.getSex())) {
                    str += " · " + bean.getSex();
                }
                if (StringUtils.isNotEmpty(bean.getStage())) {
                    str += " · " + bean.getStage();
                }
                tvName.setText(str);
            }

            HashMap<String, String> itemMap = new LinkedHashMap<>();
            if (StringUtils.isNotEmpty(bean.getSource())) {
                itemMap.put("来源：", bean.getSource());
            }
            if (StringUtils.isNotEmpty(bean.getSort())) {
                itemMap.put("分类：", bean.getSort());
            }
            initItemView(itemMap, llItemRoot);
            /*
             * 标签
             */
            initLabel(wllLabel, bean.getLabel());
        } else {
            llLabel.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化标签
     *
     * @param wllLabel
     * @param label
     */
    protected void initLabel(WrapLinearLayout wllLabel, String label) {
        if (StringUtils.isNotEmpty(label)) {
            llLabel.setVisibility(View.VISIBLE);
            View labelView;
            TextView tvLabel;
            for (String str : label.split(",")) {
                labelView = LayoutInflater.from(AppContext.getAppContext()).inflate(R.layout.cell_label, null);
                LinearLayout llLabelRoot = labelView.findViewById(R.id.ll_label_root);
                llLabelRoot.setBackground(ResourceUtils.getDrawable(R.drawable.sip_bg_label_cell2));
                tvLabel = labelView.findViewById(R.id.tv_label);
                tvLabel.setText(str);
                tvLabel.setTextColor(Color.parseColor("#E6FFFFFF"));
                wllLabel.addView(labelView);
            }
        } else {
            llLabel.setVisibility(View.GONE);
        }
    }

    protected void initItemView(Map<String, String> itemMap, LinearLayout llItemRoot) {
        View itemView;
        llItemRoot.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
        layoutParams.topMargin = SizeUtils.dp2px(8);
        for (Map.Entry<String, String> entry : itemMap.entrySet()) {
            itemView = LayoutInflater.from(AppContext.getAppContext()).inflate(R.layout.item_out_call_window2, null);
            itemView.setLayoutParams(layoutParams);
            TextView tvKey = itemView.findViewById(R.id.tv_key);
            TextView tvValue = itemView.findViewById(R.id.tv_value);
            tvKey.setText(entry.getKey());
            tvValue.setText(entry.getValue());
            llItemRoot.addView(itemView);
        }
    }
}
