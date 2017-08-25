package com.example.roit.face;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.face.interfaces.OnFaceDetectorListener;
import com.face.interfaces.OnOpenCVInitListener;
import com.face.util.FaceUtil;
import com.face.view.CameraFaceDetectionView;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class MainActivity extends AppCompatActivity implements OnFaceDetectorListener {


    private static final String TAG = "MainActivity";

    private static final String FACE1 = "face1";
    private static final String FACE2 = "face2";

    private ImageView mImageViewFace1;
    private ImageView mImageViewFace2;

    private static boolean isGettingFace = false;
    private Bitmap mBitmapFace1;
    private Bitmap mBitmapFace2;
    private TextView mCmpPic;
    private Button bn_get_face,switch_camera;
    private CameraFaceDetectionView mCameraFaceDetectionView;


    private double cmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 检测人脸的View
        mCameraFaceDetectionView = (CameraFaceDetectionView) findViewById(R.id.cameraFaceDetectionView);
        if (mCameraFaceDetectionView != null) {
            mCameraFaceDetectionView.setOnFaceDetectorListener(this);
            mCameraFaceDetectionView.setOnOpenCVInitListener(new OnOpenCVInitListener() {
                @Override
                public void onLoadSuccess() {
                    Log.i(TAG, "onLoadSuccess: ");
                }

                @Override
                public void onLoadFail() {
                    Log.i(TAG, "onLoadFail: ");
                }

                @Override
                public void onMarketError() {
                    Log.i(TAG, "onMarketError: ");
                }

                @Override
                public void onInstallCanceled() {
                    Log.i(TAG, "onInstallCanceled: ");
                }

                @Override
                public void onIncompatibleManagerVersion() {
                    Log.i(TAG, "onIncompatibleManagerVersion: ");
                }

                @Override
                public void onOtherError() {
                    Log.i(TAG, "onOtherError: ");
                }
            });
            mCameraFaceDetectionView.loadOpenCV(getApplicationContext());
        }

        mCmpPic = (TextView) findViewById(R.id.text_view);
        mImageViewFace1 = (ImageView) findViewById(R.id.face1);
        mImageViewFace2 = (ImageView) findViewById(R.id.face2);
        bn_get_face  = (Button) findViewById(R.id.bn_get_face);
        // 抓取一张人脸
        bn_get_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGettingFace = true;
                if(isGettingFace)
                    bn_get_face.setText("人脸识别");
                else
                    bn_get_face.setText("人脸检测");

            }
        });
         switch_camera = (Button) findViewById(R.id.switch_camera);
        // 切换摄像头（如果有多个）
        switch_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 切换摄像头
                Toast.makeText(getApplicationContext(),  "摄像头切换" , Toast.LENGTH_SHORT).show();
                boolean isSwitched = mCameraFaceDetectionView.switchCamera();
                Toast.makeText(getApplicationContext(), isSwitched ? "摄像头切换成功" : "摄像头切换失败", Toast.LENGTH_SHORT).show();
            }
        });


    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//        // 要校验的权限
//        String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA};
//        // 检查权限
//        mPermissionsManager.checkPermissions(0, PERMISSIONS);
//    }
//
//    /**
//     * 设置应用权限
//     *
//     * @param view view
//     */
//    public void setPermissions(View view) {
//        PermissionsManager.startAppSettings(getApplicationContext());
//    }

    @Override
    public void onFace(Mat mat, Rect rect) {

        Log.e(TAG,"onFaceonFaceonFace");

        if (isGettingFace) {
            if (null == mBitmapFace1 || null != mBitmapFace2) {
               Log.e(TAG,"保存人脸信息");
                mBitmapFace1 = null;
                mBitmapFace2 = null;

                // 保存人脸信息并显示
                FaceUtil.saveImage(this, mat, rect, FACE1);
                mBitmapFace1 = FaceUtil.getImage(this, FACE1);
                cmp = 0.0d;
            } else {
                Log.e(TAG,"计算相似度");
                FaceUtil.saveImage(this, mat, rect, FACE2);
                mBitmapFace2 = FaceUtil.getImage(this, FACE2);

                // 计算相似度
                cmp = FaceUtil.compare(this, FACE1, FACE2);
                Log.i(TAG, "onFace: 相似度 : " + cmp);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (null == mBitmapFace1) {
                        mImageViewFace1.setImageResource(R.mipmap.ic_contact_picture);
                    } else {
                        mImageViewFace1.setImageBitmap(mBitmapFace1);

                    }
                    if (null == mBitmapFace2) {
                        mImageViewFace2.setImageResource(R.mipmap.ic_contact_picture);
                    } else {
                        mImageViewFace2.setImageBitmap(mBitmapFace2);
                    }
                    mCmpPic.setText(String.format("相似度 :  %.2f", cmp) + "%");
                }
            });

            isGettingFace = false;

        }
    }
}
