package com.zhongruan.android.fingerprint_demo.fingerprintengine;


import android.util.Log;

import com.zhongruan.android.fingerprint_demo.BuildConfig;
import com.zhongruan.android.fingerprint_demo.config.ABLConfig;
import com.zhongruan.android.fingerprint_demo.ui.MyApplication;
import com.zhongruan.android.fingerprint_demo.utils.Base64Util;
import com.zhongruan.android.fingerprint_demo.utils.LogUtil;

import java.util.Map;

import cn.com.aratek.fp.Bione;
import cn.com.aratek.fp.FingerprintImage;
import cn.com.aratek.fp.FingerprintScanner;
import cn.com.aratek.util.Result;

public class YltFingerEngine extends FingerEngine {
    private static final String FP_DB_PATH = "/sdcard/fp.db";
    private static final String TAG = "HNZR";
    private int error;
    private FingerprintScanner mScanner;


    public YltFingerEngine() {
        setFingerMaxNum(10000);
        setSleepTime(ABLConfig.SYNTAX_ERROR);
        setUPath("storage/usbhost1");
    }


    public int initEngine() {
        if (this.mScanner == null) {
            this.mScanner = FingerprintScanner.getInstance();
        }
        int result = 1;
        int powerOn = this.mScanner.powerOn();
        this.error = powerOn;
        if (powerOn != 0) {
            LogUtil.e(TAG, "指纹仪上电失败");
            result = -1;
        }
        powerOn = this.mScanner.open();
        this.error = powerOn;
        if (powerOn != 0) {
            LogUtil.e(TAG, "指纹仪打开失败");
            result = -2;
        }
        powerOn = Bione.initialize(MyApplication.getApplication(), FP_DB_PATH);
        this.error = powerOn;
        if (powerOn != 0) {
            LogUtil.e(TAG, "Bione算法初始化失败");
            result = -4;
        }
        LogUtil.i(TAG, "指纹仪打开完成！");
        if (result == 1) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }


    public FingerData fingerCollect() {
        this.mScanner.prepare();
        Result res = this.mScanner.capture();
        this.mScanner.finish();
        if (res.error != 0) {
            LogUtil.e(TAG, "采集指纹图像失败！错误码：" + res.error);
            return null;
        }
        FingerprintImage fi = (FingerprintImage) res.data;
        LogUtil.e(TAG, "采集指纹图像属性：dpi--" + fi.dpi + "height--" + fi.height + "width--" + fi.width);
        if (fi == null) {
            return null;
        }
        byte[] fpBmp = fi.convert2Bmp();
        if (fpBmp == null) {
            return null;
        }
        if (getCollectType() == null || !getCollectType().equals("PSB")) {
            res = Bione.extractFeature(fi);
        } else {
            res = Bione.extractIDCardFeature(fi);
        }
        if (res.error != 0) {
            LogUtil.e(TAG, "录入失败，提取特征出错！错误码：" + res.error);
            return null;
        }
        byte[] fpFeat = (byte[]) res.data;
        FingerData data = new FingerData();
        data.setFingerImage(fpBmp);
        data.setFingerFeatures(fpFeat);
        data.setQuality(Bione.getFingerprintQuality(fi));
        return data;
    }

    @Override
    public int fingerExtract(byte[] bArr, byte[] bArr2, int i) {
        return 0;
    }

    public int freeEngine() {
        if (this.mScanner != null) {
            int close = this.mScanner.close();
            this.error = close;
            if (close != 0) {
                LogUtil.e(TAG, "指纹仪关闭失败");
                return 0;
            }
            close = this.mScanner.powerOff();
            this.error = close;
            if (close != 0) {
                LogUtil.e(TAG, "指纹仪断电失败");
                return 0;
            }
            close = Bione.exit();
            this.error = close;
            if (close != 0) {
                LogUtil.e(TAG, "Bione算法反初始化失败");
                return 0;
            }
            LogUtil.e(TAG, "指纹仪关闭完成！");
        }
        return 1;
    }


    /**
     * 返回与库中指纹最匹配的指纹id
     *
     * @param rawImage
     * @return
     */
    public String fingerSearch(byte[] rawImage) {
        try {
            if (Bione.getFormatType(rawImage) <= -1) {
                return BuildConfig.FLAVOR;
            }
            //搜索当前指纹库，查询匹配的指纹特征对应的id 并返回结果
            int id = Bione.identify(rawImage);
            LogUtil.i(id);
            if (id < 0) {
                return BuildConfig.FLAVOR;
            }
            return BuildConfig.FLAVOR + id;
        } catch (Exception e) {
            e.printStackTrace();
            return BuildConfig.FLAVOR;
        }
    }

    /**
     * 指纹特征对比
     *
     * @param temp1
     * @param temp2
     * @return
     */
    public int fingerVerify(byte[] temp1, byte[] temp2) {
        if (Bione.getFormatType(temp1) <= -1 || Bione.getFormatType(temp2) <= -1) {
            LogUtil.e(TAG, "指纹格式有问题！");
            return 0;
        }
        Result res = Bione.verify(temp1, temp2);
        if (res == null || res.data == null) {
            LogUtil.e(TAG, "比对失败！res返回结果为空");
            return 0;
        }
        Log.i("==fingerVerify==", res + " | " + res.data);
        if (res.error != 0) {
            LogUtil.e(TAG, "比对失败！错误码：" + res.error);
            return 0;
        } else if (((Boolean) res.data).booleanValue()) {
            return 1;
        } else {
            LogUtil.e(TAG, "指纹不匹配！" + res.error);
            return 0;
        }
    }

    /**
     * 录入考生指纹特征信息
     *
     * @param temp
     * @param ksno
     * @return
     */

    public int importfinger(byte[] temp, String ksno) {
        try {
            int ret;
            int formatType = Bione.getFormatType(temp);//判断指纹特征类型
            if (formatType > -1) {
                LogUtil.i("格式为：ksno : " + ksno + "   formatType : " + formatType);
                ret = Bione.enroll(Integer.parseInt(ksno), temp);
            } else {
                ret = -1;
                LogUtil.i("格式有问题：ksno : " + ksno);
                LogUtil.i("temp : " + Base64Util.encode(temp));
            }
            if (ret == 0) {
                return ret;
            }
            LogUtil.i("" + ret + "录入失败！错误码：  ksno : " + ksno);
            return Integer.parseInt(ksno) + 1;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "录入异常！：ksno" + ksno);
            return Integer.parseInt(ksno) + 1;
        }
    }

    /**
     * 比对用户提供的二代身份证指纹特征集合和设备取到的指纹特征是否有匹配
     *
     * @param idcardFeatureMap
     * @param finger
     * @return
     */

    public Object[] fingersVerify(Map<String, byte[]> idcardFeatureMap, byte[] finger) {
        if (Bione.getFormatType(finger) < 0) {//判断指纹特征类型
            return new Object[]{Integer.valueOf(0), BuildConfig.FLAVOR};
        }
        if (idcardFeatureMap != null && idcardFeatureMap.size() > 0) {
            for (Map.Entry<String, byte[]> entry : idcardFeatureMap.entrySet()) {
                if (Bione.getFormatType((byte[]) entry.getValue()) >= 0) {
                    Result res = Bione.verify(finger, (byte[]) entry.getValue());
                    if (!(res == null || res.data == null || !((Boolean) res.data).booleanValue())) {
                        return new Object[]{Integer.valueOf(1), entry.getKey()};
                    }
                }
            }
        }
        return new Object[]{Integer.valueOf(0), BuildConfig.FLAVOR};
    }


    /**
     * 比对用户提供的二代身份证指纹特征集合和设备取到的指纹特征是否有匹配
     *
     * @param idcardFeatureMap
     * @param finger
     * @return
     */
    public int idcardIdentify(Map<String, byte[]> idcardFeatureMap, byte[] finger) {
        Result res = Bione.idcardIdentify(idcardFeatureMap, finger);
        if (res.error != 0) {
            Log.e(TAG, "比对失败！错误码：" + res.error);
            return 0;
        } else if (((Boolean) res.data).booleanValue()) {
            return 1;
        } else {
            Log.e(TAG, "指纹不匹配！" + res.error);
            return 0;
        }
    }

    @Override
    public int idcardVerify(byte[] bArr, byte[] bArr2) {
        return 0;
    }

    /**
     * 获取当前指纹库中已注册指纹特征或模板数量
     *
     * @return
     */
    public int getEnrollCount() {
        return Bione.getEnrolledCount();
    }

    /**
     * 设置指纹比对的安全等级
     * 参数：level 指纹比对的安全等级（HIGH、MEDIUM、LOW）
     *
     * @param level
     * @return
     */
    public int setSecurityLevel(int level) {
        Bione.setSecurityLevel(level);
        return 1;
    }

    /**
     * 判断指纹特征类型
     * 0 FormatType.GDGK 广东高考特征
     * 1 FormatType.BIONE Bione 特征
     * 3 FormatType.IDCARD 二代证特征
     * 4 FormatType.AUF AUF 特征
     * 5 FormatType.ENROLL_FAILED 注册失败二代证特征
     * - 902 FormatType.UNKNOW 特征错误
     *
     * @param finger
     * @return
     */
    public int getFormatType(byte[] finger) {
        return Bione.getFormatType(finger);
    }

    /**
     * 清空当前指纹特征库
     *
     * @return
     */
    public boolean clear() {
        if (Bione.clear() < 0) {
            return false;
        }
        return true;
    }

    /**
     * 指纹图片转指纹特征
     *
     * @param bmp       指纹图像数据
     * @param dpi       指纹图像dpi
     * @param feaFormat 指纹特征格式 1-Bione     3-二代证特征
     * @return
     */

    public FingerData bmp2Feature(byte[] bmp, int dpi, int feaFormat) {
        FingerData data = new FingerData();
        Result res = Bione.bmp2Feature(bmp, dpi, feaFormat);
        byte[] fpFeat = (byte[]) res.data;
        int state = res.error;
        data.setFingerFeatures(fpFeat);
        if (this.error > 0) {
            data.setQuality(state);
        }
        return data;
    }
}
