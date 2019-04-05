package com.testapp.lostfound;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostsRecyclerAdapter extends RecyclerView.Adapter<MyPostsRecyclerAdapter.ViewHolder>{

    Context mContext;

    ArrayList<PostObject> mAllPosts;

    ProfileObject ownProfile;

    public MyPostsRecyclerAdapter(Context mContext, ArrayList<PostObject> mAllPosts, ProfileObject ownProfile) {
        this.mContext = mContext;
        this.mAllPosts = mAllPosts;
        this.ownProfile = ownProfile;
    }

    @NonNull
    @Override
    public MyPostsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.my_post_recycler_list_item,parent,false);
        MyPostsRecyclerAdapter.ViewHolder holder = new MyPostsRecyclerAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyPostsRecyclerAdapter.ViewHolder holder, int position) {

        Glide.with(mContext)
                .load(ownProfile.getProfilePhotoUrl())
                .placeholder(R.drawable.dummy_photo)
                .into(holder.profilePhoto);

        String name = ownProfile.getFirstName() + " "
                + ownProfile.getLastName();

        holder.name.setText(name);

        holder.university.setText("Daffodil International University");

        holder.uploadDate.setText(mAllPosts.get(position).getmPostDate());

        holder.description.setText(mAllPosts.get(position).getmDescription());

        ImageSliderAdapterForPostList viewPagerAdapter = new ImageSliderAdapterForPostList(mContext,mAllPosts.get(position).getmPhotoUrlList());

        holder.viewPager.setAdapter(viewPagerAdapter);

        holder.dotsIndicator.setViewPager(holder.viewPager);

    }

    @Override
    public int getItemCount() {
        return mAllPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profilePhoto;

        TextView name;

        TextView university;

        TextView uploadDate;

        TextView description;

        ViewPager viewPager;

        WormDotsIndicator dotsIndicator;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePhoto = itemView.findViewById(R.id.profile_photo);
            name = itemView.findViewById(R.id.name);
            university = itemView.findViewById(R.id.varsity);
            uploadDate = itemView.findViewById(R.id.upload_date);
            description = itemView.findViewById(R.id.description);
            viewPager = itemView.findViewById(R.id.view_pager);
            dotsIndicator = itemView.findViewById(R.id.worm_dots_indicator);
        }

    }
}
