package com.burhanrashid52.photoediting;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class StickerBSFragment extends BottomSheetDialogFragment {

    // Use same size stickers
    private static int[] stickerList = new int[]{
            R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb,
            R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb,
            R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb,
            R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb,
            R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb,
            R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb,
            R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb,
            R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb,
            R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb,
            R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb,
            R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb,
            R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb,
            R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb,
            R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb,
            R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb,
            R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb,
            R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb,
            R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb, R.drawable.aa, R.drawable.bb,};

    private BitmapFactory.Options options = new BitmapFactory.Options();
    private int itemSize;

    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private int convertDpToPixel(int dp)
    {
        return dp * (PhotoApp.getPhotoApp().getResources().getDisplayMetrics().densityDpi
                / DisplayMetrics.DENSITY_DEFAULT);
    }

    public StickerBSFragment() {
        // Required empty public constructor
    }

    private StickerListener mStickerListener;

    public void setStickerListener(StickerListener stickerListener) {
        mStickerListener = stickerListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // imageView: 50 * 50 dp
        itemSize = convertDpToPixel(50);

        // First decode with inJustDecodeBounds=true to check dimensions
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), stickerList[0], options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, itemSize, itemSize);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
    }

    public interface StickerListener {
        void onStickerClick(Bitmap bitmap);
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sticker_emoji_dialog, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        RecyclerView rvEmoji = contentView.findViewById(R.id.rvEmoji);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        rvEmoji.setLayoutManager(gridLayoutManager);
        StickerAdapter stickerAdapter = new StickerAdapter();
        rvEmoji.setAdapter(stickerAdapter);
        rvEmoji.setHasFixedSize(true);
        rvEmoji.setDrawingCacheEnabled(true);
        rvEmoji.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        rvEmoji.setItemViewCacheSize(stickerList.length);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sticker, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.imgSticker.setImageBitmap(BitmapFactory.decodeResource(getResources(), stickerList[position], options));
        }

        @Override
        public int getItemCount() {
            return stickerList.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgSticker;

            ViewHolder(View itemView) {
                super(itemView);
                imgSticker = itemView.findViewById(R.id.imgSticker);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mStickerListener != null) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), stickerList[getLayoutPosition()]);
                            mStickerListener.onStickerClick(Bitmap.createScaledBitmap(bitmap,
                                    256, 256, true));
                        }
                        dismiss();
                    }
                });
            }
        }
    }
}