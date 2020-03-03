package com.example.aslapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

//Page asking user to choose gesture to learn
public class MainActivity extends AppCompatActivity {

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uid = getIntent().getStringExtra("ID");
        Toast.makeText(getApplicationContext(), "Welcome " + uid, Toast.LENGTH_SHORT).show();

    }

    public void pracSign(View view) {
        Spinner gestures =  findViewById(R.id.sp_words);
        Intent intent = new Intent(MainActivity.this, LearnActivity.class);
        intent.putExtra("gesture", gestures.getSelectedItem().toString());
        intent.putExtra("ID", uid);
        startActivity(intent);
    }
}
