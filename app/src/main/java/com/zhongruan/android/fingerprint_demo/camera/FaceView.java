package com.zhongruan.android.fingerprint_demo.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Face;
import android.util.AttributeSet;

import com.zhongruan.android.fingerprint_demo.camera.util.Util;

public class FaceView extends android.support.v7.widget.AppCompatImageView {
    private Paint mLinePaint;
    private Face[] mFaces;
    private Matrix mMatrix = new Matrix();
    private RectF mRect = new RectF();

    public FaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }


    public void setFaces(Face[] faces) {
        this.mFaces = faces;
        invalidate();
    }

    public void clearFaces() {
        mFaces = null;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mFaces == null || mFaces.length < 1) {
            return;
        }
        boolean isMirror = false;
        int Id = CameraInterface.getInstance().getCameraId();
        if (Id == CameraInfo.CAMERA_FACING_BACK) {
            isMirror = false; //后置Camera无需mirror
        } else if (Id == CameraInfo.CAMERA_FACING_FRONT) {
            isMirror = true;  //前置Camera需要mirror
        }
        Util.prepareMatrix(mMatrix, isMirror, CameraInterface.getInstance().cameraRotateSys, getWidth(), getHeight());
        canvas.save();
        mMatrix.postRotate(0); //Matrix.postRotate默认是顺时针
        canvas.rotate(-0);   //Canvas.rotate()默认是逆时针
        for (Face face : this.mFaces) {
            this.mRect.set(face.rect);
            this.mMatrix.mapRect(this.mRect);
            canvas.drawRect(this.mRect, this.mLinePaint);
        }
        canvas.restore();
        super.onDraw(canvas);
    }

    private void initPaint() {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int color = Color.rgb(192, 192, 192);
        mLinePaint.setColor(color);
        mLinePaint.setStyle(Style.STROKE);
        mLinePaint.setStrokeWidth(2f);
        mLinePaint.setAlpha(180);
    }
}
