package com.zhongruan.android.fingerprint_demo.fingerprintengine;


import com.zhongruan.android.fingerprint_demo.BuildConfig;
import com.zhongruan.android.fingerprint_demo.config.ABLConfig;

import java.util.Map;

public abstract class FingerEngine implements IDetectable {
    private int FingerMaxNum;
    private int SleepTime;
    private String UPath;
    protected String collectType;
    protected int error;
    protected boolean runnable;
    private boolean softEnable;
    protected int state;

    public abstract FingerData bmp2Feature(byte[] bArr, int i, int i2);

    public abstract boolean clear();

    public abstract FingerData fingerCollect();

    public abstract int fingerExtract(byte[] bArr, byte[] bArr2, int i);

    public abstract String fingerSearch(byte[] bArr);

    public abstract int fingerVerify(byte[] bArr, byte[] bArr2);

    public abstract Object[] fingersVerify(Map<String, byte[]> map, byte[] bArr);

    public abstract int freeEngine();

    public abstract int getEnrollCount();

    public abstract int getFormatType(byte[] bArr);

    public abstract int idcardIdentify(Map<String, byte[]> map, byte[] bArr);

    public abstract int idcardVerify(byte[] bArr, byte[] bArr2);

    public abstract int importfinger(byte[] bArr, String str);

    public abstract int initEngine();

    public abstract int setSecurityLevel(int i);

    public FingerEngine() {
        this.FingerMaxNum = 10000;
        this.SleepTime = ABLConfig.SYNTAX_ERROR;
        this.UPath = BuildConfig.FLAVOR;
        this.softEnable = false;
        this.runnable = false;
    }

    public int getFingerMaxNum() {
        return this.FingerMaxNum;
    }

    public void setFingerMaxNum(int fingerMaxNum) {
        this.FingerMaxNum = fingerMaxNum;
    }

    public int getSleepTime() {
        return this.SleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.SleepTime = sleepTime;
    }

    public String getUPath() {
        return this.UPath;
    }

    public void setUPath(String uPath) {
        this.UPath = uPath;
    }

    public boolean isSoftEnable() {
        return this.softEnable;
    }

    public boolean isRunnable() {
        return this.runnable;
    }

    public void setRunnable(boolean runnable) {
        this.runnable = runnable;
    }

    public void setSoftEnable(boolean softEnable) {
        this.softEnable = softEnable;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getError() {
        return this.error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getCollectType() {
        return this.collectType;
    }

    public void setCollectType(String collectType) {
        this.collectType = collectType;
    }
}
