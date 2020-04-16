package com.example.musichall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_layout);

    }
    public void back(View view){

        startActivity(new Intent(PlayerActivity.this, MainActivity.class));
    }
    @Override
    public void onBackPressed(){
        startActivity(new Intent(PlayerActivity.this, MainActivity.class));
    }
}
