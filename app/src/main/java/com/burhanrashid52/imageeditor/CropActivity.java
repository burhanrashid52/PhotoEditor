package com.burhanrashid52.imageeditor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import com.burhanrashid52.imageeditor.base.BaseActivity;

import ja.burhanrashid52.photoeditor.CropPhotoView;
import ja.burhanrashid52.photoeditor.CropRatio;
import ja.burhanrashid52.photoeditor.OnSaveBitmap;

public class CropActivity extends BaseActivity {

    private CropPhotoView mCropPhotoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_crop);
        mCropPhotoView = findViewById(R.id.cropPhotoView);

        Bitmap resource = BitmapFactory.decodeResource(getResources(), R.drawable.got_s);
        mCropPhotoView.setImageBitmap(resource);
        mCropPhotoView.setCropRatio(CropRatio.RATIO_1_1);
        mCropPhotoView.setCropMode(true);
    }

    public void cropImage(View view) {
        mCropPhotoView.saveCropImage(new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {
                mCropPhotoView.setImageBitmap(saveBitmap);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }
}
