package com.example.googletts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.googletts.Fragment.FragmentMysentence;
import com.example.googletts.Fragment.FragmentResult;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MypageActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentResult fragmentSearch = new FragmentResult();
    private FragmentMysentence fragmentCamera = new FragmentMysentence();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentSearch).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());
    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch(menuItem.getItemId())
            {
                case R.id.resultItem:
                    transaction.replace(R.id.frameLayout, fragmentSearch).commitAllowingStateLoss();

                    break;
                case R.id.mysentenceItem:
                    transaction.replace(R.id.frameLayout, fragmentCamera).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }
}
