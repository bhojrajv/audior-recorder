package com.proseek.mdbsaudio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Userprofile extends AppCompatActivity {
BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
        bottomNavigationView=findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.Hm:
                        Intent home=new Intent(Userprofile.this,MainActivity.class);
                        startActivity(home);
                        return true;
                    case R.id.testmonial:
                        Intent test=new Intent(Userprofile.this,TestMonial.class);
                        test.putExtra("Urltest","https://mymdbs.com/#testimonial");
                        startActivity(test);
                        return true;
                    case R.id.about:
                        Intent about=new Intent(Userprofile.this,TestMonial.class);
                        about.putExtra("Urltest","https://mymdbs.com/#aboutus");
                        startActivity(about);
                        return true;
                    case R.id.contact:
                        Intent cont=new Intent(Userprofile.this,TestMonial.class);
                        cont.putExtra("Urltest","https://mymdbs.com/#footerwrap");
                        startActivity(cont);
                        return true;
                    case R.id.user:
                        Intent prof=new Intent(Userprofile.this,Userprofile.class);
                        startActivity(prof);
                    default: return  false;
                }

            }
        });
    }
}
