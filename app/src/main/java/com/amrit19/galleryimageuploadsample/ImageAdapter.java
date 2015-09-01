package com.amrit19.galleryimageuploadsample;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by amritsingh on 8/27/2015.
 */
public class ImageAdapter extends BaseAdapter {

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private Context mContext;
    private ArrayList<String> imageUrlsList;
    private LayoutInflater mInflater;
    SparseBooleanArray mSparseBooleanArray;

    public ImageAdapter(Context mContext, ArrayList<String> imageUrlsList) { //for Picasso
        this.mContext = mContext;
        this.imageUrlsList = imageUrlsList;
        this.mInflater = LayoutInflater.from(mContext);
        mSparseBooleanArray = new SparseBooleanArray();
    }

    public ImageAdapter(Context mContext, ArrayList<String> imageUrlsList,
                        ImageLoader imageLoader, DisplayImageOptions options) {
        this.mContext = mContext;
        this.imageUrlsList = imageUrlsList;
        this.imageLoader = imageLoader;
        this.options = options;
        this.mInflater = LayoutInflater.from(mContext);
        mSparseBooleanArray = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return imageUrlsList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_multiphoto_item, null);
            holder = new ViewHolder();
            holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //Picasso.with(mContext).load("file://" + imageUrlsList.get(position)).into(imageView);
        // Picasso.with(mContext).load(new File(imageUrlsList.get(position))).into(holder.imageView);
        //imageLoader.displayImage("file://" + imageUrlsList.get(position), holder.imageView, options);

        imageLoader.displayImage("file://" + imageUrlsList.get(position),
                holder.imageView, options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
                        holder.imageView.setAnimation(anim);
                        anim.start();
                    }
                }
        );

        holder.mCheckBox.setTag(position);
        holder.mCheckBox.setChecked(mSparseBooleanArray.get(position));
        holder.mCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);

        return convertView;
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



    public void updateAdapter() {
        mSparseBooleanArray = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    static class ViewHolder {
        CheckBox mCheckBox;
        ImageView imageView;
    }

}
