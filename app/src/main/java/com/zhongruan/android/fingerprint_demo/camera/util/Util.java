package com.zhongruan.android.fingerprint_demo.camera.util;

import android.graphics.Matrix;

public class Util {
    public static void prepareMatrix(Matrix matrix, boolean mirror, int displayOrientation, int viewWidth, int viewHeight) {
        matrix.setScale(mirror ? -1 : 1, 1);
        matrix.postRotate(displayOrientation);
        matrix.postScale(viewWidth / 2000f, viewHeight / 2000f);
        matrix.postTranslate(viewWidth / 2f, viewHeight / 2f);
    }
}
