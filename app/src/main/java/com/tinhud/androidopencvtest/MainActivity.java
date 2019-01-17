package com.tinhud.androidopencvtest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.lqr.picselect.LQRPhotoSelectUtils;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    static {
        OpenCVLoader.initDebug();    //简单加载下openCV  没有错误处理
    }

    private ImageView mIv_Src, mIv_Dst;
    private Button mBt_S, mBt_P;

    private Bitmap mBitmap;

    //选择图片后置true
    private boolean selectFlag = false;

    private Context mContext;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 在Activity中的onActivityResult()方法里与LQRPhotoSelectUtils关联
        mLQR.attachToActivityForResult(requestCode, resultCode, data);
    }

    private LQRPhotoSelectUtils mLQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        //初始化控件
        mIv_Src = (ImageView)findViewById(R.id.imageView_src);
        mIv_Dst = (ImageView)findViewById(R.id.imageView_dst);
        mBt_S = (Button)findViewById(R.id.button_select);
        mBt_S.setOnClickListener(this);
        mBt_P = (Button)findViewById(R.id.button_proc);
        mBt_P.setOnClickListener(this);

        //申请读SD卡权限
        requestPermission();


        mLQR = new LQRPhotoSelectUtils(this, new LQRPhotoSelectUtils.PhotoSelectListener() {
            @Override
            public void onFinish(File outputFile, Uri outputUri) {
                // 当拍照或从图库选取图片成功后回调
                mBitmap = BitmapFactory.decodeFile(outputFile.getAbsolutePath());
                Log.i("Dir:", outputFile.getAbsolutePath());
                mIv_Src.setImageBitmap(mBitmap);
                selectFlag = true;
            }
        }, false);
    }

    //权限申请
    public void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                }, 1);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_select:
                mLQR.selectPhoto();
                break;
            case R.id.button_proc:
                if (!selectFlag) {
                    Toast.makeText(mContext, "未选择图片", Toast.LENGTH_SHORT).show();
                } else {
                    proc();
                }
                break;
            default:
                break;
        }
    }

    private void proc() {

        Mat rgba = new Mat();
        Utils.bitmapToMat(mBitmap, rgba);

        Mat gray = new Mat();
        Imgproc.cvtColor(rgba, gray, Imgproc.COLOR_RGBA2GRAY);

        Bitmap dst = Bitmap.createBitmap(gray.width(), gray.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(gray, dst);

        mIv_Dst.setImageBitmap(dst);

    }


}
