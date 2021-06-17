package com.ipusoft.sip.constant;

import androidx.annotation.RawRes;

import com.ipusoft.context.utils.StringUtils;
import com.ipusoft.sip.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * author : GWFan
 * time   : 3/18/21 11:28 AM
 * desc   : 封装拨号按键
 */

public enum DialKey {
    KEY_1("1", "", R.raw.di0123),
    KEY_2("2", "ABC", R.raw.di0123),
    KEY_3("3", "DEF", R.raw.di0123),
    KEY_4("4", "GHI", R.raw.di456),
    KEY_5("5", "JKL", R.raw.di456),
    KEY_6("6", "MNO", R.raw.di456),
    KEY_7("7", "PQRS", R.raw.di789),
    KEY_8("8", "TUV", R.raw.di789),
    KEY_9("9", "WXYZ", R.raw.di789),
    KEY_0("0", "+", R.raw.di0123),
    KEY_ASTERISK("*", "", R.raw.di0123),
    KEY_POUND_SIGN("#", "", R.raw.di0123),
    KEY_DIAL("拨号", " ", 0),
    KEY_BACK("返回", " ", 0);


    private final String key;
    private final String value;
    private final int tipVoiceId;

    DialKey(String key, String value, @RawRes int tipVoiceId) {
        this.key = key;
        this.value = value;
        this.tipVoiceId = tipVoiceId;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public int getTipVoiceId() {
        return tipVoiceId;
    }

    /**
     * 根据按键类型返回提示音
     *
     * @param key
     * @return
     */
    public static int getTipVoiceByKey(String key) {
        int resId = 0;
        for (DialKey item : DialKey.values()) {
            if (StringUtils.equals(key, item.key)) {
                resId = item.getTipVoiceId();
            }
        }
        return resId;
    }

    public static List<Map<String, String>> getKeys(int type) {
        List<Map<String, String>> btnList = new ArrayList<>();
        Map<String, String> btnMap = new LinkedHashMap<>();
        for (DialKey item : DialKey.values()) {
            btnMap.put(item.getKey(), item.getValue());
            if (btnMap.size() == 3) {
                btnList.add(btnMap);
                btnMap = new LinkedHashMap<>();
            }
            if (btnList.size() == 3) {
                btnMap = new LinkedHashMap<>();
                break;
            }
        }
        if (type == 1) {
            btnMap.put(DialKey.KEY_0.getKey(), DialKey.KEY_0.getValue());
            btnMap.put(DialKey.KEY_DIAL.getKey(), DialKey.KEY_DIAL.getValue());
            btnMap.put(DialKey.KEY_BACK.getKey(), DialKey.KEY_BACK.getValue());
        } else if (type == 2) {
            btnMap.put(DialKey.KEY_ASTERISK.getKey(), DialKey.KEY_ASTERISK.getValue());
            btnMap.put(DialKey.KEY_0.getKey(), DialKey.KEY_0.getValue());
            btnMap.put(DialKey.KEY_POUND_SIGN.getKey(), DialKey.KEY_POUND_SIGN.getValue());
        }
        btnList.add(btnMap);
        return btnList;
    }
}
