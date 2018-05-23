package com.burhanrashid52.imageeditor.filters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.burhanrashid52.imageeditor.R;

import ja.burhanrashid52.photoeditor.ImageFilterView;
import ja.burhanrashid52.photoeditor.PhotoFilter;

/**
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * @version 0.1.2
 * @since 5/23/2018
 */
public class FilterViewAdapter extends RecyclerView.Adapter<FilterViewAdapter.ViewHolder> {
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_filter_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mImageFilterView.setFilterEffect(PhotoFilter.values()[position]);
    }

    @Override
    public int getItemCount() {
        return PhotoFilter.values().length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageFilterView mImageFilterView;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageFilterView = itemView.findViewById(R.id.glFilterView);
            Bitmap bm = BitmapFactory.decodeResource(itemView.getResources(), R.drawable.got_s);
            mImageFilterView.setSourceBitmap(bm);
        }
    }
}
