package com.example.googletts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.googletts.Fragment.FragmentMysentence;
import com.example.googletts.Fragment.FragmentResult;
import com.example.googletts.Fragment.FragmentWord;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MypageActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentResult fragmentResult = new FragmentResult();
    private FragmentMysentence fragmentSentence = new FragmentMysentence();
    private FragmentWord fragmentWord = new FragmentWord();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentResult).commitAllowingStateLoss();

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
                    transaction.replace(R.id.frameLayout, fragmentResult).commitAllowingStateLoss();
                    break;
                case R.id.sentenceItem:
                    transaction.replace(R.id.frameLayout, fragmentSentence).commitAllowingStateLoss();
                    break;
                case R.id.wordItem:
                    transaction.replace(R.id.frameLayout, fragmentSentence).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }
}
