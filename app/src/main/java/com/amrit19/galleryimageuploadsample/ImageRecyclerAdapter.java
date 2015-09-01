package com.amrit19.galleryimageuploadsample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by amritsingh on 8/27/2015.
 */
public class ImageRecyclerAdapter extends RecyclerView.Adapter<ImageRecyclerAdapter.MyViewHolder> {

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private Context mContext;
    private ArrayList<String> imageUrlsList;
    // private LayoutInflater mInflater;
    SparseBooleanArray mSparseBooleanArray;

    public ImageRecyclerAdapter(Context mContext, ArrayList<String> imageUrlsList,
                                ImageLoader imageLoader, DisplayImageOptions options) {
        this.mContext = mContext;
        this.imageUrlsList = imageUrlsList;
        this.imageLoader = imageLoader;
        this.options = options;
        // this.mInflater = LayoutInflater.from(mContext);
        mSparseBooleanArray = new SparseBooleanArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_multiphoto_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {

        imageLoader.displayImage("file://" + imageUrlsList.get(position), myViewHolder.getImageView(), options);

        myViewHolder.mCheckBox.setChecked(mSparseBooleanArray.get(position));
        myViewHolder.mCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);

    }

    CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
        }
    };

    public ArrayList<String> getCheckedItems() {
        ArrayList<String> mTempList = new ArrayList<String>();

        for (int i = 0; i < imageUrlsList.size(); i++) {
            if (mSparseBooleanArray.get(i)) {
                mTempList.add(imageUrlsList.get(i));
            }
        }

        return mTempList;
    }

    @Override
    public int getItemCount() {
        return imageUrlsList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private CheckBox mCheckBox;

        public MyViewHolder(View itemView) {
            super(itemView);

            mCheckBox = (CheckBox) itemView.findViewById(R.id.checkBox1);
            imageView = (ImageView) itemView.findViewById(R.id.imageView1);

        }

        public ImageView getImageView() {
            return imageView;
        }

        public CheckBox getmCheckBox() {
            return mCheckBox;
        }
    }
}
