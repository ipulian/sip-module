package com.ipusoft.siplibrary.datastore;

import com.ipusoft.context.utils.GsonUtils;
import com.ipusoft.context.utils.StringUtils;
import com.ipusoft.siplibrary.bean.SeatInfo;
import com.ipusoft.siplibrary.constant.StorageConstant;

/**
 * author : GWFan
 * time   : 5/14/21 11:50 PM
 * desc   :
 */

public class SipDataStore {
    /**
     * 坐席信息
     *
     * @param seatInfo
     */
    public static void setSeatInfo(SeatInfo seatInfo) {
        String str = "";
        if (seatInfo != null) {
            str = GsonUtils.toJson(seatInfo);
        }
        SIPModuleMMKV.set(StorageConstant.SEAT_INFO, str);
    }

    public static SeatInfo getSeatInfo() {
        String string = SIPModuleMMKV.getString(StorageConstant.SEAT_INFO);
        SeatInfo seatInfo = null;
        try {
            if (StringUtils.isNotEmpty(string)) {
                seatInfo = GsonUtils.fromJson(string, SeatInfo.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seatInfo;
    }
}
