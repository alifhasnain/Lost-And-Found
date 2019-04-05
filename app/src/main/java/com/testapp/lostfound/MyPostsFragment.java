package com.testapp.lostfound;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyPostsFragment extends Fragment {

    private ArrayList<PostObject> mAllPostList = new ArrayList<>();

    private CollectionReference mAllPostRef;

    private DocumentReference mUserProfileDoc;

    private String mUid;

    private ProfileObject currentUser;

    private MyPostsRecyclerAdapter adapter;

    View mView;


    public MyPostsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_my_posts, container, false);

        initVariables();

        loadData();

        Handler handler = new Handler(Looper.getMainLooper());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initRecyclerView(mView);
            }
        },1000);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAllPostRef.whereEqualTo("mUid",mUid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            PostObject temp = documentSnapshot.toObject(PostObject.class);
                            mAllPostList.add(temp);
                        }
                    }
                });

        try {
            adapter.notifyDataSetChanged();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView(View view) {

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Log.d("Fuck1",String.valueOf(mAllPostList.size()));

        adapter = new MyPostsRecyclerAdapter(getContext(),mAllPostList,currentUser);

        recyclerView.setAdapter(adapter);

    }

    private void loadData() {

        mUserProfileDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())   {
                    currentUser = documentSnapshot.toObject(ProfileObject.class);
                }
            }
        });

        /*mAllPostRef.whereEqualTo("mUid",mUid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)    {
                    PostObject temp = documentSnapshot.toObject(PostObject.class);
                    mAllPostList.add(temp);
                }
            }
        });*/
    }

    private void initVariables() {
        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mAllPostRef = FirebaseFirestore.getInstance().collection("all_posts");
        mUserProfileDoc = FirebaseFirestore.getInstance().document("/profile_details/"+ mUid);
    }

}
