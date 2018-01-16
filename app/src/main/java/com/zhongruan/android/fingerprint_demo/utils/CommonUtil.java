package com.zhongruan.android.fingerprint_demo.utils;

import android.support.v4.view.MotionEventCompat;
import android.util.Log;

import com.zhongruan.android.fingerprint_demo.BuildConfig;
import com.zhongruan.android.fingerprint_demo.idcardengine.IDCardData;

import java.text.SimpleDateFormat;
import java.util.Arrays;

import cn.com.aratek.idcard.IDCard;
import cn.com.aratek.idcard.IDCardReader;
import cn.com.aratek.util.Result;

public class CommonUtil {
    public static final int SPLIT_INTEGER = 512;
    private static final String TAG = "ABLCommonUtil";

    public static IDCardData getIDCardData(IDCard card) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        IDCardData data = new IDCardData();
        data.setXm(card.getName());
        data.setXb(card.getSex().toString());
        data.setMz(card.getNationality().toString());
        data.setBirth(df.format(card.getBirthday()));
        data.setAddress(card.getAddress());
        data.setSfzh(card.getNumber());
        data.setQfjg(card.getAuthority());
        if (card.getValidFrom() == null) {
            data.setYxksrq(BuildConfig.FLAVOR);
        } else {
            data.setYxksrq(df.format(card.getValidFrom()));
        }
        if (card.getValidTo() == null) {
            data.setYxjsrq("长期");
        } else {
            data.setYxjsrq(df.format(card.getValidTo()));
        }
        if (card.getPhoto() != null) {
            data.setMap(card.getPhoto());
        }
        if (card.isSupportFingerprint()) {
            int len = card.getFingerprint().length;
            if (len == 1024) {
                data.setSfzRightzw(Arrays.copyOfRange(card.getFingerprint(), 0, SPLIT_INTEGER));
                data.setSfzLeftzw(Arrays.copyOfRange(card.getFingerprint(), SPLIT_INTEGER, len));
            } else {
                data.setSfzLeftzw(card.getFingerprint());
            }
        } else {
            LogUtil.i(TAG, "身份证无指纹");
            data.setSfzRightzw(null);
            data.setSfzLeftzw(null);
        }
        Result res = IDCardReader.getInstance().readID();
        if (res.error == 0) {
            try {
                data.setCardNo(parseRFCardNum(long2bytes(((Long) res.data).longValue())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            LogUtil.i(TAG, "ic卡扫描失败");
        }
        return data;
    }


    public static String parseRFCardNum(byte[] dataBuffer) {
        String str = null;
        if (dataBuffer != null) {
            try {
                StringBuffer sb = new StringBuffer();
                for (byte b : dataBuffer) {
                    String hex = Integer.toHexString(b & MotionEventCompat.ACTION_MASK);
                    if (hex.length() == 1) {
                        hex = '0' + hex;
                    }
                    sb.append(hex.toUpperCase());
                }
                str = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    public static byte[] long2bytes(long in) {
        byte[] out = new byte[8];
        for (int i = 0; i < 8; i++) {
            out[i] = (byte) ((int) (in >> ((7 - i) * 8)));
        }
        return out;
    }
}
