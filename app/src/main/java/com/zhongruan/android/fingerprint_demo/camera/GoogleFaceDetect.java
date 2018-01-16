package com.zhongruan.android.fingerprint_demo.camera;

import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.os.Handler;
import android.os.Message;

import com.zhongruan.android.fingerprint_demo.config.ABLConfig;

public class GoogleFaceDetect implements FaceDetectionListener {
    private Handler mHander;

    public GoogleFaceDetect(Handler handler) {
        mHander = handler;
    }

    @Override
    public void onFaceDetection(Face[] faces, Camera camera) {
        if (faces != null) {
            Message m = mHander.obtainMessage();
            m.what = ABLConfig.UPDATE_FACE_RECT;
            m.obj = faces;
            m.sendToTarget();
        }
    }
}
