package com.zhongruan.android.fingerprint_demo.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.SoundPool;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zhongruan.android.fingerprint_demo.R;
import com.zhongruan.android.fingerprint_demo.adapter.SelectKsAdapter;
import com.zhongruan.android.fingerprint_demo.base.BaseActivity;
import com.zhongruan.android.fingerprint_demo.camera.CameraInterface;
import com.zhongruan.android.fingerprint_demo.camera.CameraSurfaceView;
import com.zhongruan.android.fingerprint_demo.camera.util.DisplayUtil;
import com.zhongruan.android.fingerprint_demo.db.DbServices;
import com.zhongruan.android.fingerprint_demo.db.entity.Bk_ks;
import com.zhongruan.android.fingerprint_demo.db.entity.Rz_ks_zw;
import com.zhongruan.android.fingerprint_demo.dialog.FaceDialog;
import com.zhongruan.android.fingerprint_demo.fingerprintengine.FingerData;
import com.zhongruan.android.fingerprint_demo.utils.ABLSynCallback;
import com.zhongruan.android.fingerprint_demo.utils.Base64Util;
import com.zhongruan.android.fingerprint_demo.utils.DateUtil;
import com.zhongruan.android.fingerprint_demo.utils.FileUtils;
import com.zhongruan.android.fingerprint_demo.utils.LogUtil;
import com.zhongruan.android.fingerprint_demo.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.aratek.dev.Terminal;

/**
 * Created by Administrator on 2017/9/8.
 */

public class RZActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout llSwitch, llPhoto, mLlBack, mLlChangeCc, ll_kwdj, layout_view_finger, layout_view_face, state_camera, layout_view_kslist;
    private TextView mTvCountUnverified, mTvTitle, mTvCountVerified, mTvCountTotal, mTvTime, mTvDate, mTvTip, mTvKsName, mTvKsSeat, mKsResult, mTvKsSfzh, mTvKsKc, mTvKsno;
    private RelativeLayout rl_camera;
    private ImageView mIvKs;
    private Handler handler = new Handler();
    private FingerData fingerData;
    private Bitmap zwBitmap;
    private CameraSurfaceView surfaceView = null;
    private List<Rz_ks_zw> rz_ks_zw;
    private boolean isRzSucceed;
    private GridView gvKs;
    private SelectKsAdapter selectKsAdapter;
    private List<Bk_ks> bk_ks;
    private String zwid, timeZP, timeZW, kmno, kmmc, kcmc, kdno, ccmc;
    private int CS = 0;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_rz);
    }

    @Override
    public void initViews() {
        mLlBack = findViewById(R.id.llBack);
        mLlChangeCc = findViewById(R.id.ll_change_cc);
        ll_kwdj = findViewById(R.id.ll_kwdj);
        mTvTitle = findViewById(R.id.tvTitle);
        mTvTime = findViewById(R.id.tvTime);
        mTvDate = findViewById(R.id.tvDate);
        mTvTip = findViewById(R.id.tvTip);
        mTvCountTotal = findViewById(R.id.tvCountTotal);
        mTvCountVerified = findViewById(R.id.tvCountVerified);
        mTvCountUnverified = findViewById(R.id.tvCountUnverified);
        layout_view_kslist = findViewById(R.id.layout_view_kslist);
        layout_view_finger = findViewById(R.id.layout_view_finger);
        layout_view_face = findViewById(R.id.layout_view_face);
        mIvKs = findViewById(R.id.ivKs);
        mTvKsName = findViewById(R.id.tvKsName);
        mTvKsSeat = findViewById(R.id.tvKsSeat);
        mKsResult = findViewById(R.id.ks_result);
        mTvKsSfzh = findViewById(R.id.tvKsSfzh);
        mTvKsKc = findViewById(R.id.tvKsKc);
        mTvKsno = findViewById(R.id.tvKsno);
        surfaceView = findViewById(R.id.sf_face);
        llPhoto = findViewById(R.id.llPhoto);
        llSwitch = findViewById(R.id.llSwitch);
        state_camera = findViewById(R.id.state_camera);
        rl_camera = findViewById(R.id.rl_camera);
        gvKs = findViewById(R.id.gvKs);

        layout_view_kslist.setVisibility(View.VISIBLE);
        layout_view_finger.setVisibility(View.GONE);
        layout_view_face.setVisibility(View.GONE);
        ccmc = DbServices.getInstance(getBaseContext()).selectCC().get(0).getCc_name();
        kcmc = DbServices.getInstance(getBaseContext()).selectKC().get(0).getKc_name();
        kmmc = DbServices.getInstance(getBaseContext()).selectCC().get(0).getKm_name();
        kmno = DbServices.getInstance(getBaseContext()).selectCC().get(0).getKm_no();
        kdno = DbServices.getInstance(getBaseContext()).loadAllkd().get(0).getKd_no();
        mTvTitle.setText(ccmc + " " + kcmc + " " + kmmc);
        KsList();
    }

    @Override
    public void initListeners() {
        mLlBack.setOnClickListener(this);
        mLlChangeCc.setOnClickListener(this);
        llPhoto.setOnClickListener(this);
        llSwitch.setOnClickListener(this);
        ll_kwdj.setOnClickListener(this);
    }

    @Override
    public void initData() {
        new Thread(runnable01).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llBack:
                if (isRzSucceed) {
                    isZwYz();
                } else {
                    finish();
                }
                break;
            case R.id.ll_change_cc:
                Intent intent = new Intent(this, SelectKcCcActivity.class);
                intent.putExtra("sfrz", "1");
                startActivity(intent);
                finish();
                break;
            case R.id.llPhoto:
                doTakePicture();
                break;
            case R.id.llSwitch:
                CameraInterface.getInstance().cameraSwitch();
                break;
            case R.id.ll_kwdj:
                break;
        }
    }

    Runnable runnable01 = new Runnable() {
        @Override
        public void run() {
            mTvTime.setText(DateUtil.getNowTimeNoDate());
            mTvDate.setText(DateUtil.getDateByFormat("yyyy年MM月dd日"));
            handler.postDelayed(runnable01, 1000);
        }
    };

    private Runnable runnable02 = new Runnable() {
        public void run() {
            fingerData = MyApplication.getYltFingerEngine().fingerCollect();
            if (fingerData != null) {
                CS++;
                playBeep();
                zwid = MyApplication.getYltFingerEngine().fingerSearch(fingerData.getFingerFeatures());
                LogUtil.i("zwid:" + zwid);
                if (Utils.stringIsEmpty(zwid) && CS != 3) {
                    zwid = "";
                    handler.postDelayed(runnable02, 500);// 间隔1秒
                } else {
                    CS = 0;
                    timeZW = DateUtil.getNowTime();
                    LogUtil.i(zwid);
                    KsPZ();
                }
            } else {
                handler.postDelayed(runnable02, 500);// 间隔1秒
            }
        }
    };

    private Runnable runnable03 = new Runnable() {
        public void run() {
            state_camera.setVisibility(View.GONE);
            rl_camera.setVisibility(View.VISIBLE);
        }
    };

    private void playBeep() {
        SoundPool soundPool = new SoundPool(10, 3, 100);
        soundPool.load(RZActivity.this, R.raw.beep, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i2) {
                soundPool.play(1,  //声音id
                        1, //左声道
                        1, //右声道
                        0, //优先级
                        0, // 0表示不循环，-1表示循环播放
                        1);//播放比率，0.5~2，一般为1
            }
        });
    }

    private void KsList() {
        fingerData = null;
        mLlChangeCc.setEnabled(true);
        ll_kwdj.setEnabled(true);
        bk_ks = new ArrayList<>();
        for (int i = 0; i < DbServices.getInstance(getBaseContext()).loadAllbkkssize(); i++) {
            if (i < 9) {
                bk_ks.add(i, DbServices.getInstance(getBaseContext()).selectKszh("0" + (i + 1)));
            } else {
                bk_ks.add(i, DbServices.getInstance(getBaseContext()).selectKszh(i + 1 + ""));
            }
        }
        mTvCountTotal.setText(bk_ks.size() + "");
        int isRzSize = DbServices.getInstance(getBaseContext()).queryBkKsIsTG("1");
        mTvCountVerified.setText(isRzSize + "");
        mTvCountUnverified.setText(bk_ks.size() - isRzSize + "");
        selectKsAdapter = new SelectKsAdapter(this, bk_ks);
        gvKs.setAdapter(selectKsAdapter);
        gvKs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LogUtil.i(i);
                LogUtil.i(bk_ks.get(i).getKs_ksno());
                layout_view_kslist.setVisibility(View.GONE);
                layout_view_finger.setVisibility(View.VISIBLE);
                rz_ks_zw = DbServices.getInstance(getBaseContext()).selectBkKs(bk_ks.get(i).getKs_zjno());
                fingerData = null;
                KsZW();
            }
        });
    }

    private void KsZW() {
        isRzSucceed = true;
        MyApplication.getYltFingerEngine().clear();
        for (int i = 0; i < rz_ks_zw.size(); i++) {
            byte[] bytes = Base64.decode(rz_ks_zw.get(i).getZw_feature(), 2);
            MyApplication.getYltFingerEngine().importfinger(bytes, rz_ks_zw.get(i).getKsid() + "");
        }
        mTvTip.setText("请按手指");
        handler.postDelayed(runnable02, 500);
    }

    private void KsPZ() {
        isRzSucceed = true;
        handler.removeCallbacks(runnable02); //停止刷新
        mLlChangeCc.setEnabled(false);
        ll_kwdj.setEnabled(false);
        Picasso.with(this).load(new File(FileUtils.getAppSavePath() + "/" + rz_ks_zw.get(0).getKs_xp())).into(mIvKs);
        mTvKsName.setText(rz_ks_zw.get(0).getKs_xm() + "|" + (rz_ks_zw.get(0).getKs_xb().equals("1") ? "男" : "女"));
        mTvKsSeat.setText(rz_ks_zw.get(0).getKs_zwh());
        mTvKsSfzh.setText(rz_ks_zw.get(0).getKs_zjno());
        mTvKsKc.setText(rz_ks_zw.get(0).getKs_kcmc());
        mTvKsno.setText(rz_ks_zw.get(0).getKs_ksno());
        if (!Utils.stringIsEmpty(zwid)) {
            mKsResult.setText("指纹比对通过");
            mKsResult.setTextColor(getResources().getColor(R.color.green));
        } else {
            mKsResult.setText("指纹比对不通过");
            mKsResult.setTextColor(getResources().getColor(R.color.collect_yellow));
        }
        layout_view_finger.setVisibility(View.GONE);
        layout_view_face.setVisibility(View.VISIBLE);
        mTvTip.setText("请拍照");
        initViewParams();
        handler.postDelayed(runnable03, 1000);
    }

    private void initViewParams() {
        ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
        Point p = DisplayUtil.getScreenMetrics(this);
        params.width = p.x;
        params.height = p.y;
        surfaceView.setLayoutParams(params);
    }

    /**
     * 拍照
     */
    public void doTakePicture() {
        if (CameraInterface.getInstance().isPreviewing && (CameraInterface.getInstance().getCameraDevice() != null)) {
            CameraInterface.getInstance().getCameraDevice().takePicture(mShutterCallback, null, mJpegPictureCallback);
        }
    }

    /*为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量*/
    Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        //快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
        public void onShutter() {
            // TODO Auto-generated method stub
            LogUtil.i("myShutterCallback:onShutter...");
        }
    };

    Camera.PictureCallback mJpegPictureCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            LogUtil.i("myJpegCallback:onPictureTaken...");
            Bitmap b = null;
            if (null != data) {
                b = BitmapFactory.decodeByteArray(data, 0, data.length);//data是字节数据，将其解析成位图
                int x = b.getWidth() / 4;
                int y = 0;
                int DST_RECT_WIDTH = b.getWidth() / 2;
                int DST_RECT_HEIGHT = b.getHeight();
                final Bitmap bitmap = Bitmap.createBitmap(b, x, y, DST_RECT_WIDTH, DST_RECT_HEIGHT);
                CameraInterface.getInstance().rectBitmap = ThumbnailUtils.extractThumbnail(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight()), 168, 240);
                new FaceDialog(RZActivity.this, R.style.MyDialogStyle, new FaceDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {
                            ShowHintDialog(RZActivity.this, rz_ks_zw.get(0).getKs_xm() + " 验证通过", "提示", R.drawable.img_base_icon_correct, 800, false);
                            timeZP = DateUtil.getNowTime();
                            ABLSynCallback.call(new ABLSynCallback.BackgroundCall() {
                                @Override
                                public Object callback() {
                                    if (DbServices.getInstance(getBaseContext()).selectBKKS(rz_ks_zw.get(0).getKs_zjno()).getIsRZ().equals("1") || !Utils.stringIsEmpty(zwid)) {
                                        return Boolean.valueOf(true);
                                    } else {
                                        return Boolean.valueOf(false);
                                    }
                                }
                            }, new ABLSynCallback.ForegroundCall() {
                                @Override
                                public void callback(Object obj) {
                                    if (((Boolean) obj).booleanValue()) {
                                        DbServices.getInstance(getBaseContext()).saveRzjg("21", rz_ks_zw.get(0).getKs_ksno(), kmno, kdno, rz_ks_zw.get(0).getKs_kcno(), rz_ks_zw.get(0).getKs_zwh(), Terminal.getSN(), timeZP, "0");
                                        if (fingerData != null) {
                                            byte[] bytes = fingerData.getFingerImage();
                                            zwBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length); //生成位图
                                            String timezw = DateUtil.getNowTime_Millisecond4();
                                            FileUtils.saveBitmap(zwBitmap, "sfrz_rzjl/kstz_a_zw/", rz_ks_zw.get(0).getKs_ksno() + "_" + timezw);
                                            DbServices.getInstance(getBaseContext()).saveRzjl("8003", rz_ks_zw.get(0).getKs_ksno(), kmno, kdno, rz_ks_zw.get(0).getKs_kcno(), rz_ks_zw.get(0).getKs_zwh(), Terminal.getSN(), "1", timeZW, Base64Util.encode(fingerData.getFingerFeatures()), "sfrz_rzjl/kstz_a_zw/" + rz_ks_zw.get(0).getKs_ksno() + "_" + timezw + ".jpg", DbServices.getInstance(getBaseContext()).selectRzjg(rz_ks_zw.get(0).getKs_ksno()).toString(), "0");
                                        }
                                        String timezp = DateUtil.getNowTime_Millisecond4();
                                        FileUtils.saveBitmap(CameraInterface.getInstance().rectBitmap, "sfrz_rzjl/kstz_a_pz/", rz_ks_zw.get(0).getKs_ksno() + "_" + timezp);
                                        DbServices.getInstance(getBaseContext()).saveRzjl("8007", rz_ks_zw.get(0).getKs_ksno(), kmno, kdno, rz_ks_zw.get(0).getKs_kcno(), rz_ks_zw.get(0).getKs_zwh(), Terminal.getSN(), "1", DateUtil.getNowTime(), "", "sfrz_rzjl/kstz_a_pz/" + rz_ks_zw.get(0).getKs_ksno() + "_" + timezp + ".jpg", DbServices.getInstance(getBaseContext()).selectRzjg(rz_ks_zw.get(0).getKs_ksno()).toString(), "0");
                                    } else {
                                        DbServices.getInstance(getBaseContext()).saveRzjg("22", rz_ks_zw.get(0).getKs_ksno(), kmno, kdno, rz_ks_zw.get(0).getKs_kcno(), rz_ks_zw.get(0).getKs_zwh(), Terminal.getSN(), timeZP, "0");
                                        String timezp = DateUtil.getNowTime_Millisecond4();
                                        FileUtils.saveBitmap(CameraInterface.getInstance().rectBitmap, "sfrz_rzjl/kstz_a_pz/", rz_ks_zw.get(0).getKs_ksno() + "_" + timezp);
                                        DbServices.getInstance(getBaseContext()).saveRzjl("8006", rz_ks_zw.get(0).getKs_ksno(), kmno, kdno, rz_ks_zw.get(0).getKs_kcno(), rz_ks_zw.get(0).getKs_zwh(), Terminal.getSN(), "0", DateUtil.getNowTime(), "", "sfrz_rzjl/kstz_a_pz/" + rz_ks_zw.get(0).getKs_ksno() + "_" + timezp + ".jpg", DbServices.getInstance(getBaseContext()).selectRzjg(rz_ks_zw.get(0).getKs_ksno()).toString(), "0");
                                    }
                                    DbServices.getInstance(getBaseContext()).saveBkKs(rz_ks_zw.get(0).getKs_zjno());
                                    isZwYz();
                                    KsList();
                                }
                            });
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                        }
                    }
                }).setFaceBitmap(CameraInterface.getInstance().rectBitmap).show();
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isRzSucceed) {
            isZwYz();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void isZwYz() {
        zwid = "";
        isRzSucceed = false;
        mTvTip.setText("请选择考生");
        layout_view_kslist.setVisibility(View.VISIBLE);
        layout_view_finger.setVisibility(View.GONE);
        layout_view_face.setVisibility(View.GONE);
        rl_camera.setVisibility(View.GONE);
        state_camera.setVisibility(View.VISIBLE);
        handler.removeCallbacks(runnable02);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable01);
        handler.removeCallbacks(runnable02);
        handler.removeCallbacks(runnable03);
        handler = null;
        fingerData = null;
        CameraInterface.getInstance().doStopCamera();
    }
}
