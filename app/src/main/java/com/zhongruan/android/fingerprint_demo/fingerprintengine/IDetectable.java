package com.zhongruan.android.fingerprint_demo.fingerprintengine;

public interface IDetectable {
    int freeEngine();

    int initEngine();

    boolean isAvailable();
}
