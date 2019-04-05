package com.testapp.lostfound;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ImageSliderAdapterForAddPost extends PagerAdapter {

    Context mContext;

    ArrayList<Bitmap> mPhotoList;

    public ImageSliderAdapterForAddPost(Context mContext, ArrayList<Bitmap> mPhotoList) {
        this.mContext = mContext;
        this.mPhotoList = mPhotoList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_pager_image_item,null);
        ImageView imageView = view.findViewById(R.id.image_holder);
        imageView.setImageBitmap(mPhotoList.get(position));
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return mPhotoList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
