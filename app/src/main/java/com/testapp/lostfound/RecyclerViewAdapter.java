package com.testapp.lostfound;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements View.OnClickListener{

    Context mContext;

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_list_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public void onClick(View v) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profilePhoto;

        TextView name;

        TextView university;

        TextView uploadDate;

        TextView description;

        ViewPager viewPager;

        SpringDotsIndicator dotsIndicator;

        TextView sendMail;

        TextView openFacebookProfile;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePhoto = itemView.findViewById(R.id.profile_photo);
            name = itemView.findViewById(R.id.name);
            university = itemView.findViewById(R.id.varsity);
            uploadDate = itemView.findViewById(R.id.upload_date);
            description = itemView.findViewById(R.id.description);
            viewPager = itemView.findViewById(R.id.view_pager);
            dotsIndicator = itemView.findViewById(R.id.worm_dots_indicator);
            sendMail = itemView.findViewById(R.id.send_mail);
            openFacebookProfile = itemView.findViewById(R.id.open_fb_profile);

        }

    }
}
