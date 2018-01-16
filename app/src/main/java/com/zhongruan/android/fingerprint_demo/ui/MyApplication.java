package com.zhongruan.android.fingerprint_demo.ui;


import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.zhongruan.android.fingerprint_demo.BuildConfig;
import com.zhongruan.android.fingerprint_demo.db.DaoMaster;
import com.zhongruan.android.fingerprint_demo.db.DaoSession;
import com.zhongruan.android.fingerprint_demo.db.DbServices;
import com.zhongruan.android.fingerprint_demo.db.GreenDaoContext;
import com.zhongruan.android.fingerprint_demo.fingerprintengine.YltFingerEngine;
import com.zhongruan.android.fingerprint_demo.idcardengine.YltIdCardEngine;
import com.zhongruan.android.fingerprint_demo.utils.LogUtil;


public class MyApplication extends Application {

    private static DaoSession daoSession;
    private static YltIdCardEngine yltIdCardEngine;
    private static YltFingerEngine yltFingerEngine;

    //获取到主线程的上下文
    private static MyApplication mContext;
    private static DaoMaster daoMaster;
    private boolean shouldStopUploadingData = false;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        this.mContext = this;
        DbServices.getInstance(mContext);
        yltIdCardEngine = new YltIdCardEngine();
        yltFingerEngine = new YltFingerEngine();

        if (BuildConfig.DEBUG) {
            LogUtil.init(true, Log.VERBOSE);
        } else {
            LogUtil.init(false);
        }
    }

    public static MyApplication getApplication() {
        return mContext;
    }

    public static YltIdCardEngine getYltIdCardEngine() {
        return yltIdCardEngine;
    }

    public static YltFingerEngine getYltFingerEngine() {
        return yltFingerEngine;
    }

    /**
     * 取得DaoMaster
     *
     * @param context 上下文
     * @return DaoMaster
     */
    public static DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(new GreenDaoContext(), "aries.db", null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    public boolean isShouldStopUploadingData() {
        return this.shouldStopUploadingData;
    }

    public void setShouldStopUploadingData(boolean shouldStopUploadingData) {
        this.shouldStopUploadingData = shouldStopUploadingData;
    }

    /**
     * 取得DaoSession
     *
     * @param context 上下文
     * @return DaoSession
     */
    public static DaoSession getDaoInstant(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }
}
