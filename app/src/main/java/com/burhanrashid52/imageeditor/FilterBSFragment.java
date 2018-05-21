package com.burhanrashid52.imageeditor;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ja.burhanrashid52.photoeditor.filters.PhotoFilter;

public class FilterBSFragment extends BottomSheetDialogFragment {

    public FilterBSFragment() {
        // Required empty public constructor
    }

    private FilterListener mFilterListener;

    public void setFilterListener(FilterListener filterListener) {
        mFilterListener = filterListener;
    }

    public interface FilterListener {
        void onFilterSelected(PhotoFilter photoFilter);
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvEmoji.setLayoutManager(linearLayoutManager);
        FilterAdapter filterAdapter = new FilterAdapter();
        rvEmoji.setAdapter(filterAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_filter, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.txtFilter.setText(PhotoFilter.values()[position].name());
        }

        @Override
        public int getItemCount() {
            return PhotoFilter.values().length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtFilter;

            ViewHolder(View itemView) {
                super(itemView);
                txtFilter = itemView.findViewById(R.id.txtFilter);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mFilterListener != null) {
                            mFilterListener.onFilterSelected(PhotoFilter.values()[getLayoutPosition()]);
                        }
                        dismiss();
                    }
                });
            }
        }
    }
}