package com.testapp.lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private DrawerLayout mDrawer;

    Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        setDrawerAndToolbar();
        initializeVariables();

        if(savedInstanceState==null)    {
            mToolBar.setTitle("Profile");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,new ProfileFragment()).commit();
        }
    }

    @Override
    protected void onStart() {
        mAuth.addAuthStateListener(mAuthStateListener);
        super.onStart();
    }

    @Override
    protected void onPause() {
        mAuth.removeAuthStateListener(mAuthStateListener);
        super.onPause();
    }

    private void initializeVariables() {

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setCheckedItem(R.id.nav_user_profile);

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mAuth.getCurrentUser() == null)  {
                    makeToast("Signing Out!");
                    startActivity(new Intent(MainActivity.this , SignIn.class));
                    finish();
                }
            }
        };

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.nav_add_post:
                        mToolBar.setTitle("Add Post");
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container,new AddPostFragment()).commit();
                        break;
                    case R.id.nav_user_profile:
                        mToolBar.setTitle("Profile");
                        getSupportFragmentManager().popBackStack();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container,new ProfileFragment()).commit();
                        break;
                    case R.id.nav_lost:
                        mToolBar.setTitle("Lost");
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container,new LostFragment()).commit();
                        break;
                    case R.id.nav_found:
                        mToolBar.setTitle("Found");
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container,new FoundFragment()).commit();
                        break;
                    case R.id.nav_user_posts:
                        mToolBar.setTitle("My Posts");
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container,new MyPostsFragment()).commit();
                        break;
                    case R.id.nav_about_dev:
                        mToolBar.setTitle("About Developers");
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container,new AboutDevs()).commit();
                        break;
                    case R.id.nav_sign_out:
                        FirebaseAuth.getInstance().signOut();
                        break;

                }
                mDrawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void setDrawerAndToolbar() {
        mDrawer = findViewById(R.id.drawer_layout);
        mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,mDrawer,mToolBar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if(mDrawer.isDrawerOpen(GravityCompat.START))   {
            mDrawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
}
