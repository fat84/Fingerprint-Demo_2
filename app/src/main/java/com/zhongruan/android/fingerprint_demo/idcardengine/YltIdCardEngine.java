package com.zhongruan.android.fingerprint_demo.idcardengine;


import com.zhongruan.android.fingerprint_demo.BuildConfig;
import com.zhongruan.android.fingerprint_demo.utils.CommonUtil;
import com.zhongruan.android.fingerprint_demo.utils.LogUtil;

import cn.com.aratek.iccard.ICCardReader;
import cn.com.aratek.idcard.IDCard;
import cn.com.aratek.idcard.IDCardReader;
import cn.com.aratek.util.Result;

public class YltIdCardEngine extends IDCardEngine {
    private static final String TAG = "HNZR";
    private ICCardReader icReader;
    private IDCardReader mReader;

    /**
     * 身份证识别器上电
     *
     * @return
     */
    public int initEngine() {
        if (this.mReader == null) {
            this.mReader = IDCardReader.getInstance();
        }
        int result = 1;
        int powerOn = this.mReader.powerOn();
        this.error = powerOn;
        if (powerOn != 0) {
            LogUtil.i(TAG, "身份证识读器上电失败");
            result = 0;
        }
        powerOn = this.mReader.open();
        this.error = powerOn;
        if (powerOn != 0) {
            LogUtil.i(TAG, "身份证识读器打开失败" + this.error);
            if (this.icReader == null) {
                this.icReader = ICCardReader.getInstance();
            }
            powerOn = this.icReader.powerOn();
            this.error = powerOn;
            if (powerOn != 0) {
                LogUtil.i(TAG, "物理卡号识读器上电失败");
            }
            powerOn = this.icReader.open();
            this.error = powerOn;
            if (powerOn != 0) {
                LogUtil.i(TAG, "物理卡号识读器打开失败");
                result = 0;
            } else {
                result = 1;
                setType(2);
            }
            if (result == 1) {
                return 1;
            }
            return 0;
        }
        LogUtil.i(TAG, "身份证识读器打开成功");
        setType(1);
        return result;
    }

    /**
     * 身份证识别器断电
     *
     * @return
     */
    public int freeEngine() {
        int close;
        if (this.mReader != null) {
            close = this.mReader.close();
            this.error = close;
            if (close != 0) {
                LogUtil.i(TAG, "身份证识读器关闭失败");
                return 0;
            }
            close = this.mReader.powerOff();
            this.error = close;
            if (close != 0) {
                LogUtil.i(TAG, "身份证识读器断电失败");
                return 0;
            }
            this.softEnable = false;
            LogUtil.i(TAG, "身份证识读器关闭成功");
        }
        if (this.icReader != null) {
            close = this.icReader.close();
            this.error = close;
            if (close != 0) {
                LogUtil.i(TAG, "物理卡号识读器关闭失败");
                return 0;
            }
            LogUtil.i(TAG, "物理卡号识读器关闭成功");
            close = this.icReader.powerOff();
            this.error = close;
            if (close != 0) {
                LogUtil.i(TAG, "物理卡号识读器断电失败");
                return 0;
            }
        }
        return 1;
    }

    public boolean isAvailable() {
        return false;
    }


    /**
     * 读取身份证信息
     *
     * @return
     */

    public IDCardData startScanIdCard() {
        Result res;
        if (getType() == 1) {
            res = this.mReader.read();
            if (res.error == 0) {
                LogUtil.i(TAG, "读卡成功");
                return CommonUtil.getIDCardData((IDCard) res.data);
            } else if (res.error == IDCardReader.NO_CARD) {
                LogUtil.i(TAG, "请重新放卡或者确认卡片是否存在！");
            } else {
                LogUtil.i(TAG, "读卡失败！错误码：" + res.error);
            }
        } else if (getType() == 2) {
            res = this.icReader.read();
            if (res.error != 0) {
                LogUtil.i(TAG, "获取ic卡失败");
                return null;
            }
            try {
                String no = CommonUtil.parseRFCardNum(CommonUtil.long2bytes(((Long) res.data).longValue()));
                if (!(no == null || no.equals(BuildConfig.FLAVOR))) {
                    IDCardData data = new IDCardData();
                    data.setCardNo(no);
                    LogUtil.i(TAG, "物理卡号" + no);
                    return data;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        LogUtil.i(TAG, "获取ic卡失败" + getType());

        return null;
    }

    public int stopScanIdCard() {
        this.softEnable = false;
        if (this.softEnable) {
            return 0;
        }
        return 1;
    }

    public void setSoftEnable(boolean flag) {
        this.softEnable = flag;
    }

    public void setRunnable(boolean flag) {
        this.runnable = flag;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }
}
