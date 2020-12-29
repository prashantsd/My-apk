package com.example.clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.clone.fragment.HomeFragment;
import com.example.clone.fragment.ProfileFragment;
import com.example.clone.fragment.SearchFragment;
import com.example.clone.fragment.likeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomePageActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment FG;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        bottomNavigationView = findViewById(R.id.Bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottom_click);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottom_click = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    FG = new HomeFragment();
                    break;
                case R.id.nav_search:
                    FG = new SearchFragment();
                    break;
                case R.id.nav_add_post:
                    FG = null;
                    startActivity(new Intent(HomePageActivity.this, PostActivity.class));
                    break;
                case R.id.nav_like:
                    FG = new likeFragment();
                    break;
                case R.id.nav_profile:
                    SharedPreferences.Editor editor = getSharedPreferences("PRA", MODE_PRIVATE).edit();
                    editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();
                    FG = new ProfileFragment();
                    break;
            }

            if (FG != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        FG).commit();
            }
            return true;
        }
    };
}

