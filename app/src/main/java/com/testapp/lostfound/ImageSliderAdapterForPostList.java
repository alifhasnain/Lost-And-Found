package com.testapp.lostfound;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.viewpager.widget.PagerAdapter;

public class ImageSliderAdapterForPostList extends PagerAdapter {

    private Context mContext;

    private ArrayList<String> mPhotoUrlsList;

    public ImageSliderAdapterForPostList(Context mContext, ArrayList<String> mPhotoUrlsList) {
        this.mContext = mContext;
        this.mPhotoUrlsList = mPhotoUrlsList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_pager_image_for_recyclerview,null);
        ImageView imageView = view.findViewById(R.id.image_holder);

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
        circularProgressDrawable.setStrokeWidth(15f);
        circularProgressDrawable.setCenterRadius(70f);
        circularProgressDrawable.setColorSchemeColors(Color.parseColor("#D32F2F"), Color.parseColor("#009688") , Color.parseColor("#0288D1"));
        circularProgressDrawable.start();

        Glide.with(view)
                .load(mPhotoUrlsList.get(position))
                .placeholder(circularProgressDrawable)
                .into(imageView);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return mPhotoUrlsList.size();
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
