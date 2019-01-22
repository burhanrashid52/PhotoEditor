package com.burhanrashid52.imageeditor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.burhanrashid52.imageeditor.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import ja.burhanrashid52.photoeditor.CropImageView;
import ja.burhanrashid52.photoeditor.imagezoom.RatioItem;
import ja.burhanrashid52.photoeditor.imagezoom.utils.Matrix3;

public class CropActivity extends BaseActivity {

    private static List<RatioItem> dataList = new ArrayList<RatioItem>();
    private RectF mRectF;


    static {
        // init data
        dataList.add(new RatioItem("none", -1f));
        dataList.add(new RatioItem("1:1", 1f));
        dataList.add(new RatioItem("1:2", 1 / 2f));
        dataList.add(new RatioItem("1:3", 1 / 3f));
        dataList.add(new RatioItem("2:3", 2 / 3f));
        dataList.add(new RatioItem("3:4", 3 / 4f));
        dataList.add(new RatioItem("2:1", 2f));
        dataList.add(new RatioItem("3:1", 3f));
        dataList.add(new RatioItem("3:2", 3 / 2f));
        dataList.add(new RatioItem("4:3", 4 / 3f));
    }

    private CropImageView mCropImageView;
    private ImageView mImageViewTouch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_crop);
        mCropImageView = findViewById(R.id.crop_panel);
        mImageViewTouch = findViewById(R.id.imgTouchView);
        Bitmap resource = BitmapFactory.decodeResource(getResources(), R.drawable.got_s);
        mRectF = new RectF(0, 0, resource.getWidth(), resource.getHeight());
        mImageViewTouch.setImageResource(R.drawable.got_s);
        mImageViewTouch.post(new Runnable() {
            @Override
            public void run() {
                mCropImageView.setCropRect(mRectF);
                mCropImageView.setRatioCropRect(mRectF, dataList.get(4).getRatio());
            }
        });
    }

    /*public void cropImage(View view) {
        new CropImageTask().execute(BitmapFactory.decodeResource(getResources(), R.drawable.got_s));
    }

    private final class CropImageTask extends AsyncTask<Bitmap, Void, Bitmap> {


        @SuppressWarnings("WrongThread")
        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            RectF cropRect = mCropImageView.getCropRect();// 剪切区域矩形
            Matrix touchMatrix = mImageViewTouch.getImageViewMatrix();
            // Canvas canvas = new Canvas(resultBit);
            float[] data = new float[9];
            touchMatrix.getValues(data);// 底部图片变化记录矩阵原始数据
            Matrix3 cal = new Matrix3(data);// 辅助矩阵计算类
            Matrix3 inverseMatrix = cal.inverseMatrix();// 计算逆矩阵
            Matrix m = new Matrix();
            m.setValues(inverseMatrix.getValues());
            m.mapRect(cropRect);// 变化剪切矩形

            // Paint paint = new Paint();
            // paint.setColor(Color.RED);
            // paint.setStrokeWidth(10);
            // canvas.drawRect(cropRect, paint);
            // Bitmap resultBit = Bitmap.createBitmap(params[0]).copy(
            // Bitmap.Config.ARGB_8888, true);
            Bitmap resultBit = Bitmap.createBitmap(params[0],
                    (int) cropRect.left, (int) cropRect.top,
                    (int) cropRect.width(), (int) cropRect.height());

            //saveBitmap(resultBit, activity.saveFilePath);
            return resultBit;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result == null)
                return;

            mImageViewTouch.setImageBitmap(result);
            mCropImageView.setCropRect(mImageViewTouch.getBitmapRect());
        }
    }//*/
}
