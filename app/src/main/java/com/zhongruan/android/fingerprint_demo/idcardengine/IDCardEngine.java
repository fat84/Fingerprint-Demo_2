package com.zhongruan.android.fingerprint_demo.idcardengine;


import com.zhongruan.android.fingerprint_demo.config.ABLConfig;
import com.zhongruan.android.fingerprint_demo.fingerprintengine.IDetectable;

public abstract class IDCardEngine implements IDetectable {
    protected int SleepTime;
    protected int error;
    protected boolean runnable;
    protected boolean softEnable;
    protected int state;
    protected int type;

    public abstract int freeEngine();

    public abstract int getState();

    public abstract int initEngine();

    public abstract void setRunnable(boolean z);

    public abstract void setSoftEnable(boolean z);

    public abstract void setState(int i);

    public abstract IDCardData startScanIdCard();

    public abstract int stopScanIdCard();

    public IDCardEngine() {
        this.runnable = false;
        this.softEnable = false;
        this.SleepTime = ABLConfig.SYNTAX_ERROR;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSleepTime() {
        return this.SleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.SleepTime = sleepTime;
    }

    public boolean isSoftEnable() {
        return this.softEnable;
    }

    public int getError() {
        return this.error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public boolean isRunnable() {
        return this.runnable;
    }
}
