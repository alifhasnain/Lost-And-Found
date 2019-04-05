package com.testapp.lostfound;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>   {

    Context mContext;

    ArrayList<PostObject> mAllPosts;

    ArrayList<ProfileObject> mAllProfiles;

    public RecyclerViewAdapter(Context mContext, ArrayList<PostObject> mAllPosts, ArrayList<ProfileObject> mAllProfiles) {
        this.mContext = mContext;
        this.mAllPosts = mAllPosts;
        this.mAllProfiles = mAllProfiles;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_list_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {

        int currentProfileDataLocation = 0;

        String uid = mAllPosts.get(position).getmUid();

        for(int i = 0 ; i < mAllProfiles.size() ; i ++) {
            if(mAllProfiles.get(i).getuID().equals(uid))    {
                currentProfileDataLocation = i;
            }
        }

        Log.e("Post size",String.valueOf(mAllPosts.size()));
        Log.e("Profile size",String.valueOf(mAllProfiles.size()));

        Glide.with(mContext)
                .load(mAllProfiles.get(currentProfileDataLocation).getProfilePhotoUrl())
                .placeholder(R.drawable.dummy_photo)
                .into(holder.profilePhoto);

        String name = mAllProfiles.get(currentProfileDataLocation).getFirstName() + " "
                + mAllProfiles.get(currentProfileDataLocation).getLastName();

        holder.name.setText(name);

        holder.university.setText("Daffodil International University");

        holder.uploadDate.setText(mAllPosts.get(position).getmPostDate());

        holder.description.setText(mAllPosts.get(position).getmDescription());

        ImageSliderAdapterForPostList viewPagerAdapter = new ImageSliderAdapterForPostList(mContext,mAllPosts.get(position).getmPhotoUrlList());

        holder.viewPager.setAdapter(viewPagerAdapter);

        holder.dotsIndicator.setViewPager(holder.viewPager);

        final int finalCurrentProfileDataLocation = currentProfileDataLocation;

        holder.sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] emails = new String[1];
                emails[0] = mAllProfiles.get(finalCurrentProfileDataLocation).geteMail();
                String sub = "This is maybe my lost item";
                String body = "Hi! Thing this thing belongs to me";
                sendMail(emails,sub,body);
            }
        });
        holder.openFacebookProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = mAllProfiles.get(finalCurrentProfileDataLocation).getFbProfileUrl();
                if (!url.startsWith("http://") && !url.startsWith("https://"))  {
                    url = "http://" + url;
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                mContext.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAllPosts.size();
    }

    private void sendMail(String[] email,String subject,String body)    {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL,email);
        intent.putExtra(Intent.EXTRA_SUBJECT,subject);
        intent.putExtra(Intent.EXTRA_TEXT,body);
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            mContext.startActivity(intent);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profilePhoto;

        TextView name;

        TextView university;

        TextView uploadDate;

        TextView description;

        ViewPager viewPager;

        WormDotsIndicator dotsIndicator;

        ImageView sendMail;

        ImageView openFacebookProfile;


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
            openFacebookProfile = itemView.findViewById(R.id.fb_profile_link);

        }

    }
}
